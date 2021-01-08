# Java-Http Documentation

## Contents

* [Contents](#contents)
* [About](#about)
* [Reference](#reference)
	* [Method](#method-enum)
	* [Path](#path-class)
	* [Request](#request-class)
	* [Response](#response-class)
	* [Route](#route-class)
	* [RouterPoint](#routerpoint-enum)
	* [Routing](#routing-class)
	* [Server](#server-class)

## About

Java-Http is an ExpressJS-style server library which allows easy creation of a HTTP server with Java. it is non-blocking, and is based on the built-in Java Http library (com.sun.net.httpserver). The library has been mostly tested, and is actively maintained.

## Reference

Reference documentation for Java-Http.

### `Method` enum

The `Method` enum represents a request method, such as "GET" or "POST".

* `GET` - HTTP GET request
* `POST` - HTTP POST request
* `PUT` - HTTP PUT request
* `DELETE` - HTTP DELETE request
* `ALL` - matches all methods
* `OTHER` - represents a method not yet implemented, such as "HEAD"

### `Path` class

The `Path` class represents a request path to match. `Path` is intended to be used internally.

* `final String source` - The source string specified in the constructor
* `final Method method` - The method specified in the constructor

#### `Path(String source)`

Constructs a `Path` from the specified `String` `source`. `source` should be a valid URI pathname. The implicitly applied `Method` of this constructor is `Method.ALL`.

#### `Path(String source, Method method)`

Constructs a `Path` from the specified `String` `source`, which also matches the request method `method`. `source` should be a valid URI pathname.

#### `boolean matches(URI path, Method method)`

Checks whether the specified `URI` and request method `method` matches the `Path`. Used internally.

### `Request` class

The `Request` class represents an incoming HTTP request.

* `final int port` - The local port which the request was received on
* `final String rawUrl` - The escaped request path, including query and fragment (starts with `/`)
* `final String urlFragment` - The escaped url fragment (does not include `#` at start)
* `final String urlQuery` - The escaped url query (does not include `?` at start)
* `final String urlPath` - The escaped request pathname (starts with `/`)
* `final String httpVersion` - The received HTTP version in the first line of the request, such as `1.1`
* `final String remoteAddress` - The remote connection address
* `final String localAddress` - The local connection address
* `final Map<String, String> query` - The parsed query string, with the parameter as the item key and the value (or `null`) as the value
* `final Map<String, List<String>> headers` - The request headers
* `final Method method` - The request method

#### `Request(HttpExchange httpExchange)`

Constructs from `HttpExchange` `httpExchange`. Intended to be used internally only.

#### `List<Byte> getBody()`

Gets the request body and returns it as a `List<Byte>`.

#### `String getBodyString()`

Gets the request body, converts it (using UTF-8) and returns it as a `String`.

#### `void set(String key, Object value)`

Sets an attribute to this request object, which will be passed to downstream routers.

#### `Object get(String key)`

Gets an attribute from this request object, applied by upstream routers. (See `void set(String, String)`)

### `Response` class

The `Response` class represents an outgoing HTTP response.

* `int responseCode` - The response status code (default is `200`)
* `final Map<String, List<String>>` - The response headers

#### `Response(HttpExchange httpExchange)`

Constructs from `HttpExchange` `httpExchange`. Intended to be used internally only.

#### `void write(byte[] data)`

Appends `data` to the response body. Note that this is not directly appended to the output stream and is stored internally.

#### `void write(Byte[] data)`

Appends `data` to the response body. Note that this is not directly appended to the output stream and is stored internally.

#### `void write(List<Byte> data)`

Appends `data` to the response body. Note that this is not directly appended to the output stream and is stored internally.

#### `void write(String data)`

Appends `data` to the response body. Note that this is not directly appended to the output stream and is stored internally.

#### `void writeHeader(String name, String value)`

Sets response header `name` to `value`. This is a convenience method, and `headers` can be modified directly.

#### `void writeHeader(String name, List<String> value)`

Sets response header `name` to multiple values stored in `value`. This is a convenience method, and `headers` can be modified directly.

#### `void writeHeader(String name, String[] value)`

Sets response header `name` to multiple values stored in `value`. This is a convenience method, and `headers` can be modified directly.

#### `void status(int code)`

Sets the response status code. This is a convenience method, and `responseCode` can be modified directly.

#### `boolean end(boolean cancel)`

Ends the response and sends headers and body to the client. If `cancel` is `true`, it will cancel the request and return no data. It returns `true` on success and `false` on error.

#### `boolean end()`

Same as `boolean end(boolean)`, but sets `cancel` to false.

#### `boolean hasEnded()`

Returns whether the response has already ended.

### `Route` class

The `Route` class is a specific handler for a request.

#### `void callback(Request req, Response res, Routing route)`

The main callback for this handler. By default, it calls [`route.next()`](#void-next) and returns, which has the effect of continuing the routing chain. It is intended to be overridden in actual handlers:

```java
Route myHandler = new Route() {
	public void callback(Request req, Response res, Routing route) {
		/* ... */
	}
};
```

### `RouterPoint` enum

The `RouterPoint` enum represents the purpose of a router.

* `PREHANDLER` - The handler is executed before main request handlers
* `POSTHANDLER` - The handler is executed after main request handlers
* `ERROR` - The handler is to be executed on errors

### `Routing` class

Represents an individual request's handlers and routing through the backend.

#### `Routing(List<Route> routing, Request req, Response res)`

Constructs a `Routing` and starts it. `routing` is a list of the routes that the `Routing` will take, and `req` and `res` are passed to the handlers.

#### `void next()`

Calls the next handler in the `routing` if existent, and passes `req`, `res` and `this` to the handler.
