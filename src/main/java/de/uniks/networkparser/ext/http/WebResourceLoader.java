package de.uniks.networkparser.ext.http;

import de.uniks.networkparser.interfaces.Condition;

public class WebResourceLoader implements Condition<HTTPRequest> {
	private String redirect;
	public static WebResourceLoader create(String redirect) {
		WebResourceLoader loader = new WebResourceLoader();
		loader.with(redirect);
		return loader;
	}

	public WebResourceLoader with(String redirect) {
		this.redirect = redirect;
		return this;
	}

	@Override
	public boolean update(HTTPRequest value) {
		value.withPath(redirect);
		value.getOutput().println("HTTP/1.1 301 Moved Permanently");
		value.getOutput().println("Location: "+redirect);
		value.getOutput().println("Connection: close");
		
		return true;
	}
}
