package de.uniks.networkparser.test;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyServer;

public class LudoBroadCast {

	public static void main(String[] args) {
		Space spaceA = new Space();
//		spaceA.search(4242);
		NodeProxyServer proxy = new NodeProxyServer(NodeProxy.TYPE_IN);
		proxy.withPort(4242);
		spaceA.with(proxy);

		
		
		Space spaceB = new Space();
		NodeProxyServer search = spaceB.search(4242);
	}
}
