package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyFileSystem;

public class FileWatcher {
    protected NodeProxy proxy;
    protected String fileName;

    public FileWatcher init(NodeProxyFileSystem owner, String fileName) {
		this.proxy = owner;
		this.fileName = fileName;
		return this;
	}
	public void close() {
		
	}
	
}
