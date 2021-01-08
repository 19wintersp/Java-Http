package com._19wintersp.http;

import java.util.*;

public class Routing {
	private final List<Route> routing;
	private final int routingLength;
	private final Request req;
	private final Response res;
	private int index = -1;
	
	public Routing(List<Route> _routing, Request _req, Response _res) {
		this.routing = _routing;
		this.routingLength = this.routing.size();
		this.req = _req;
		this.res = _res;
		next();
	}
	
	public void next() {
		this.index++;
		if (this.index >= this.routingLength) return;
		this.routing.get(this.index).callback(this.req, this.res, this);
	}
}
