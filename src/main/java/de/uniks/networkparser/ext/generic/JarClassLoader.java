package de.uniks.networkparser.ext.generic;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * The Class JarClassLoader.
 *
 * @author Stefan
 */
public class JarClassLoader extends URLClassLoader {
	protected ClassLoader parentClassloader;

	/**
	 * Instantiates a new jar class loader.
	 */
	public JarClassLoader() {
		super(new URL[] {});
	}

	/**
	 * Instantiates a new jar class loader.
	 *
	 * @param parentClassloader the parent classloader
	 * @param urls the urls
	 */
	public JarClassLoader(ClassLoader parentClassloader, URL... urls) {
		super(urls, parentClassloader);
		this.parentClassloader = parentClassloader;
	}

	/**
	 * Instantiates a new jar class loader.
	 *
	 * @param urls the urls
	 */
	public JarClassLoader(URL... urls) {
		super(urls);
	}

	/**
	 * Adds the URL.
	 *
	 * @param url the url
	 */
	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}