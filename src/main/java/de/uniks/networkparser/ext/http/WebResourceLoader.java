package de.uniks.networkparser.ext.http;

import java.io.PrintWriter;

import de.uniks.networkparser.interfaces.Condition;

/**
 * The Class WebResourceLoader.
 *
 * @author Stefan
 */
public class WebResourceLoader implements Condition<HTTPRequest> {
	private String redirect;

	/**
	 * Creates the.
	 *
	 * @param redirect the redirect
	 * @return the web resource loader
	 */
	public static WebResourceLoader create(String redirect) {
		WebResourceLoader loader = new WebResourceLoader();
		loader.with(redirect);
		return loader;
	}

	/**
	 * With.
	 *
	 * @param redirect the redirect
	 * @return the web resource loader
	 */
	public WebResourceLoader with(String redirect) {
		this.redirect = redirect;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(HTTPRequest value) {
		if (value != null) {
			value.withURL(redirect);
			PrintWriter output = value.getOutput();
			if (output != null) {
				output.println("HTTP/1.1 301 Moved Permanently");
				output.println("Location: " + redirect);
				output.println("Connection: close");
			}
		}
		return true;
	}
}
