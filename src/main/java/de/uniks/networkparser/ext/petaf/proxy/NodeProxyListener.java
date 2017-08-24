package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.Message;

@FunctionalInterface
public interface NodeProxyListener {
	public void send(Message msg, String blob);
}
