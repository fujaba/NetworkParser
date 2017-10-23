package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class PetaFTest {

	@Test
	public void testServer() throws InterruptedException {
		NodeProxyTCP server = NodeProxyTCP.createServer(5000);
		Assert.assertTrue(server.start());
		server.start();
		server.close();
//		Thread.sleep(5000);
	}
}
