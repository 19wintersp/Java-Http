package com._19wintersp.http;

import java.util.*;
import java.net.URI;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Request {
	public final int port;
	public final String rawUrl;
	public final String urlFragment;
	public final String urlQuery;
	public final String urlPath;
	public final String httpVersion;
	public final String remoteAddress;
	public final String localAddress;
	public final Map<String, String> query;
	public final Map<String, List<String>> headers;
	public final Method method;
	
	private final HttpExchange exchange;
	private final URI uri;
	private boolean bodyFetched = false;
	private boolean bodyConverted = false;
	private List<Byte> body;
	private String bodyAsString;
	
	public Request(HttpExchange httpExchange) {
		final List<String> methods = Arrays.asList(Internal.permittedMethods);
		final String reqMethod = httpExchange.getRequestMethod();
		final String proto = httpExchange.getProtocol();

		this.exchange = httpExchange;
		this.httpVersion = proto.substring(proto.indexOf("/") + 1);
		this.port = httpExchange.getLocalAddress().getPort();
		this.localAddress = httpExchange.getLocalAddress().getAddress().toString();
		this.remoteAddress = httpExchange.getRemoteAddress().getAddress().toString();
		this.method = methods.contains(reqMethod)
				? Method.valueOf(reqMethod)
				: Method.OTHER;
		this.headers = httpExchange.getRequestHeaders();
		this.uri = httpExchange.getRequestURI();
		this.rawUrl = this.uri.toString();
		this.urlFragment = this.uri.getFragment();
		this.urlQuery = this.uri.getQuery();
		this.urlPath = this.uri.getPath();
		this.query = parseQuery(this.urlQuery);
	}
	
	public List<Byte> getBody() {
		if (this.bodyFetched) return this.body;
		
		final List<Byte> out = new LinkedList<Byte>();
		final InputStream is = this.exchange.getRequestBody();
		
		int data = 0;
		try {
			while (true) {
				data = is.read();
				if (data == -1) break;
				out.add((byte)data);
			}
		} catch(IOException e) { }
		
		this.bodyFetched = true;
		this.body = out;
		return out;
	}
	public String getBodyString() {
		if (this.bodyConverted) return this.bodyAsString;
		
		final List<Byte> unconverted = getBody();
		final byte[] bytes = new byte[unconverted.size()];
		
		int i = 0;
		for (Byte b : unconverted) bytes[i++] = (byte)b;
		
		this.bodyConverted = true;
		this.bodyAsString = new String(bytes, StandardCharsets.UTF_8);
		return this.bodyAsString;
	}
	
	public void set(String key, Object value) {
		this.exchange.setAttribute(key, value);
	}
	public void get(String key) {
		this.exchange.getAttribute(key);
	}
	
	private Map<String, String> parseQuery(String query) {
		final Map<String, String> out = new HashMap<String, String>();
		
		if (query == null) return out;
		
		for (String param : query.split("&")) {
			String[] parts = param.split("=");
			out.put(parts[0], parts.length >= 2 ? parts[1] : null);
		}
		
		return out;
	}
}
