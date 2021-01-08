package com._19wintersp.http;

public class Route {
	/**
	 * Main callback executed as route handler; should be overridden in actual handlers. To go to the next handler, call route.next()
	 */
	public void callback(Request req, Response res, Routing route) {
		route.next();
	}
}
