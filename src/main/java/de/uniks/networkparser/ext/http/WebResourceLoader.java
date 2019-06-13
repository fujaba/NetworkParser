package de.uniks.networkparser.ext.http;

import java.io.PrintWriter;

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
		if(value != null) {
			value.withPath(redirect);
			PrintWriter output = value.getOutput();
			if(output != null) {
				output.println("HTTP/1.1 301 Moved Permanently");
				output.println("Location: "+redirect);
				output.println("Connection: close");
			}
		}
		
		return true;
	}
}
