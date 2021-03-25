package de.uniks.networkparser.test;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class LudoBroadCast {

	public static void main(String[] args) {
		Space spaceA = new Space();
//		spaceA.search(4242);
		NodeProxyTCP proxy = new NodeProxyTCP().withServerType(NodeProxy.TYPE_IN);
		proxy.withPort(4242);
		spaceA.with(proxy);



//		Space spaceB = new Space();
//		NodeProxyTCP search = spaceB.search(4242);
	}
}
