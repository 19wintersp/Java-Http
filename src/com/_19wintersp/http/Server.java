package com._19wintersp.http;

import java.io.IOException;
import java.net.URI;
import java.net.InetSocketAddress;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;

final class Internal {
	public static String[] permittedMethods = { "GET", "POST", "PUT", "DELETE" };
}

class Handler implements HttpHandler {
	private final Map<Path, Route> routes;
	private final List<Route> preRouters;
	private final List<Route> postRouters;
	private final Route errorHandler;
	
	public Handler(Map<Path, Route> _routes, List<Route> _preRouters, List<Route> _postRouters, Route _errorHandler) {
		this.routes = _routes;
		this.preRouters = _preRouters;
		this.postRouters = _postRouters;
		this.errorHandler = _errorHandler;
	}
	
	@Override
	public void handle(HttpExchange exchange) {
		final URI path = exchange.getRequestURI();
		Method method = Method.OTHER;
		try {
			method = Method.valueOf(exchange.getRequestMethod().toUpperCase());
		} catch (IllegalArgumentException e) { }

		final List<Route> routing = new LinkedList<Route>(this.preRouters);
		final Request req = new Request(exchange);
		final Response res = new Response(exchange);
		
		final Iterator<Map.Entry<Path, Route>> itr = this.routes.entrySet().iterator();
		while (true) {
			if (!itr.hasNext()) {
				routing.add(this.errorHandler);
				break;
			}
			
			Map.Entry<Path, Route> current = itr.next();
			if (current.getKey().matches(path, method)) {
				routing.add(current.getValue());
				break;
			}
		}
		
		routing.addAll(this.postRouters);
		
		new Routing(routing, req, res);
	}
}

public class Server {
	public int port = -1;
	public boolean isListening() { return this._listening; }

	private boolean _listening = false;
	private boolean bound = false;
	private HttpContext mainContext;
	private final HttpServer server;
	private final Map<Path, Route> routes = new HashMap<Path, Route>();
	private final List<Route> preRouters = new LinkedList<Route>();
	private final List<Route> postRouters = new LinkedList<Route>();
	private Route errorRouter = new Route();
	
	public Server() throws Exception {
		this.server = HttpServer.create();
		this._listening = false;
	}
	
	public void all(String path, Route handler) {
		this.routes.put(new Path(path, Method.ALL), handler);
	}
	public void get(String path, Route handler) {
		this.routes.put(new Path(path, Method.GET), handler);
	}
	public void post(String path, Route handler) {
		this.routes.put(new Path(path, Method.POST), handler);
	}
	public void put(String path, Route handler) {
		this.routes.put(new Path(path, Method.PUT), handler);
	}
	public void delete(String path, Route handler) {
		this.routes.put(new Path(path, Method.DELETE), handler);
	}
	
	public void use(RouterPoint point, Route handler) throws Exception {
		if (point.equals(RouterPoint.PREROUTE)) this.preRouters.add(handler);
		else if (point.equals(RouterPoint.POSTROUTE)) this.postRouters.add(handler);
		else if (point.equals(RouterPoint.ERROR)) this.errorRouter = handler;
		else throw new Exception("Invalid or null RouterPoint");
	}
	
	public void listen() throws Exception {
		if (this.port < 0) throw new Exception("Port has not been set");
		else listen(this.port);
	}
	public void listen(int port) throws Exception {
		if (isListening()) throw new Exception("Server already listening");
		final Handler finalHandler = new Handler(this.routes, this.preRouters, this.postRouters, this.errorRouter);
		this.mainContext = this.server.createContext("/", finalHandler);
		if (!this.bound) {
			this.server.bind(new InetSocketAddress(port), 0);
			this.bound = true;
		}
		this.server.start();
		this.port = port;
		this._listening = true;
	}
	
	public void stop() throws Exception {
		if (!isListening()) throw new Exception("Server not already listening");
		this.server.stop(0);
		this.server.removeContext(this.mainContext);
		this._listening = false;
	}
}
