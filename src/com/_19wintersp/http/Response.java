package com._19wintersp.http;

import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Response {
	public int responseCode = 200;
	public final Map<String, List<String>> headers;
	
	private final HttpExchange exchange;
	private final List<Byte> data;
	private boolean postRoute = false;
	private boolean ended = false;
	
	public Response(HttpExchange httpExchange) {
		this.exchange = httpExchange;
		this.data = new LinkedList<Byte>();
		this.headers = httpExchange.getResponseHeaders();
	}
	
	public void write(byte[] data) {
		final Byte[] pData = new Byte[data.length];
		int i = 0;
		for (byte b : data) pData[i++] = Byte.valueOf(b);
		this.data.addAll(Arrays.asList(pData));
	}
	public void write(Byte[] data) {
		this.data.addAll(Arrays.asList(data));
	}
	public void write(List<Byte> data) {
		this.data.addAll(data);
	}
	public void write(String data) {
		write(data.getBytes());
	}
	
	public void writeHeader(String name, String value) {
		this.headers.put(name, new LinkedList<String>());
		this.headers.get(name).add(value);
	}
	public void writeHeader(String name, List<String> value) {
		this.headers.put(name, value);
	}
	public void writeHeader(String name, String[] value) {
		writeHeader(name, Arrays.asList(value));
	}
	
	public void status(int status) {
		this.responseCode = status;
	}
	
	public boolean end() {
		return end(false);
	}
	public boolean end(boolean cancel) {
		if (this.ended) return false;
		
		final byte[] responseData = new byte[this.data.size()];
		int i = 0;
		for (Byte b : this.data) responseData[i++] = b;
		
		try {
			this.exchange.sendResponseHeaders(this.responseCode, cancel ? -1 : 0);
			if (!cancel) this.exchange.getResponseBody().write(responseData);
			this.exchange.close();
			this.ended = true;
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public boolean hasEnded() {
		return this.ended;
	}
}
