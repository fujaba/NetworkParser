package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.network.Message;

@FunctionalInterface
public interface NodeProxyListener {
	public void send(Message msg, String blob);
}
