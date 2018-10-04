package de.uniks.networkparser.ext.generic;

import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {
	protected ClassLoader parentClassloader;

	public JarClassLoader(ClassLoader parentClassloader, URL... urls) {
		super(urls, parentClassloader);
		this.parentClassloader = parentClassloader;
	}

	public JarClassLoader(URL... urls) {
		super(urls);
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
}