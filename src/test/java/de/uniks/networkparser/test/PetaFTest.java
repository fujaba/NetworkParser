package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.json.JsonObject;

public class PetaFTest {

//	@Test
	public void testServer() throws InterruptedException {
		NodeProxyTCP server = NodeProxyTCP.createServer(5000);
		Assert.assertTrue(server.start());
		server.start();
		server.close();
//		Thread.sleep(5000);
	}
	
	@Test
	public void test() {
		Space space = new Space();
		NodeProxy proxy = space.getOrCreateProxy("141.51.116.1", 5000);
		space.getOrCreateProxy("141.51.116.1", 5010);
		
		ConnectMessage message=new ConnectMessage();
		message.withReceiver(proxy);
		
		String convertMessage = space.convertMessage(message);
		
		ByteBuffer buffer = new ByteBuffer();
		buffer.with(convertMessage);
		
		ConnectMessage newMessage = (ConnectMessage) space.getMap().decode(buffer);
		System.out.println(newMessage);
		Assert.assertEquals(newMessage.getReceiver(), proxy);
		
	}
	
	@Test
	public void Serialization() {
		Space space = new Space();
		
		ConnectMessage connect = ConnectMessage.create();
		JsonObject json = new JsonObject();
		json.with("Key", "42");
		connect.withMessage(json);
		
		System.out.println(space.convertMessage(connect));
		
	}
}
