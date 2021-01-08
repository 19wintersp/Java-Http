package com._19wintersp.http;

import java.net.URI;

public class Path {
	public final String source;
	public final Method method;
	
	public Path(String _source) {
		this.source = _source;
		this.method = Method.ALL;
	}
	public Path(String _source, Method _method) {
		this.source = _source;
		this.method = _method;
	} 
	
	public boolean matches(URI path, Method method) {
		try {
			final boolean methodMatch = method.equals(this.method) || this.method.equals(Method.ALL);
			return this.source.equals(path.getPath()) && methodMatch;
		} catch(Exception e) {
			return false;
		}
	}
}
