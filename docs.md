# Java-Http Documentation

## Contents

* [Contents](#contents)
* [About](#about)
* [Reference](#reference)
	* Method
	* Path
	* Request
	* Response
	* Route
	* RouterPoint
	* Routing
	* Server

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

#### `boolean matches(java.net.URI path, Method method)`

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

#### `Request(com.sun.net.httpserver.HttpExchange httpExchange)`

Constructs from `HttpExchange` `httpExchange`.

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


