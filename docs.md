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
* [Tutorial](#tutorial)
* [Examples](#examples)
	* [Hello world](#hello-world-example)
	* [Router](#router-example)
	* [Error handler](#error-handler-example)
	* [Query parameters](#query-parameters-example)
	* [POST echoer](#post-echoer-example)
	* [Multiple methods](#multiple-methods-example)
	* [IP finder](#ip-finder-example)

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

### `Server` class

The `Server` class forms the main API for interacting with the library. `Server` handles the main logic and handling of requests.

* `int port` - The port the server listens on (default is `-1`)

#### `Server()`

Initialises the server.

#### `boolean isListening()`

Returns whether the server is actively listening for requests, or whether it is idle.

#### `void listen()`

Starts listening for requests using `int port`. Throws an exception if it is not set (`== -1`), or if already listening.

#### `void listen(int port)`

Starts listening for requests using the specified port, and sets `int port` to the given port. Throws an exception if already listening.

#### `void stop()`

Stops listening for requests. Throws an exception if not already listening.

#### `boolean isListening()`

Returns whether the server is listening, or if it is idle.

#### `void use(RouterPoint point, Route handler)`

Adds `handler` to the stack of routers at the specified `RouterPoint`. Routers are added in the order of the calls to `use`.

#### `void METHOD(String path, Route handler)`

Adds `handler` as the handler for `path`, which is converted to a `Path`. This method exists for all `Method`s excluding `Method.OTHER`.

## Tutorial

This is a basic tutorial to get started with the library. The main code is in the [Hello World example](#hello-world-example).

1. Download the latest release JAR, and add it to your build path.

2. Import the library in your code:
	```java
	import com._19wintersp.http.*;
	```

3. In your `main` (or wherever you want to start the server), create your `Server` instance:
	```java
	final Server svr = new Server();
	```

4. Add a route to the server:
	```java
	svr.all("/", new Route() {
		public void callback(Request req, Response res, Routing route) {
			res.write("Hello world!");
			res.end();
			route.next();
		}
	});
	```

5. Start listening:
	```java
	svr.listen(80);
	```

6. Run your program, and then your server will begin running on port 80. When you request to "/", the message gets returned.

## Examples

These are some basic examples. All examples assume inclusion of the JAR as well as importing `com._19wintersp.http.*`.

### Hello World example

```java
final Server svr = new Server();
svr.all("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("Hello world!");
		res.end();
		route.next();
	}
});
svr.listen(80);
```

### Router example

```java
final Server svr = new Server();
svr.use(RouterPoint.PREROUTE, new Route() {
	public void callback(Request req, Response res, Routing route) {
		System.out.println("Request to " + req.urlPath);
		route.next();
	}
});
/* more routes here */
svr.listen(80);
```

### Error handler example

```java
final Server svr = new Server();
svr.use(RouterPoint.ERROR, new Route() {
	public void callback(Request req, Response res, Routing route) {
		System.out.println("Error!");
		res.write("An error occurred!");
		res.end();
	}
});
/* more routes here */
svr.listen(80);
```

### Query parameters example

```java
final Server svr = new Server();
svr.all("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		if (req.query.containsKey("name")) {
			res.write("Hi there ");
			res.write(req.get("name"));
		} else {
			res.write("What's your name?");
		}
		res.end();
		route.next();
	}
});
svr.listen(80);
```

### POST echoer example

```java
final Server svr = new Server();
svr.post("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("Here's what you sent:\n\n");
		res.write(req.getBodyString());
		res.end();
		route.next();
	}
});
svr.all("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("POST some data to /");
		res.end();
		route.next();
	}
});
svr.listen(80);
```

### Multiple methods example

```java
final Server svr = new Server();
svr.get("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("GET");
		res.end();
		route.next();
	}
});
svr.post("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("POST");
		res.end();
		route.next();
	}
});
svr.all("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("Something else");
		res.end();
		route.next();
	}
});
svr.listen(80);
```

### IP finder example

```java
final Server svr = new Server();
svr.get("/", new Route() {
	public void callback(Request req, Response res, Routing route) {
		res.write("Your IP address is: ");
		res.write(req.remoteAddress.substring(1));
		res.end();
		route.next();
	}
});
svr.listen(80);
```
