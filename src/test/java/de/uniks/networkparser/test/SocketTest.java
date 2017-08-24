package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SocketTest {

	@Test
	public void testSocket() {
		
		// Server
		NodeProxyTCP server=NodeProxyTCP.createServer(5000);
		server.withListener(new ObjectCondition() {
			
			@Override
			public boolean update(Object value) {
				Message msg = (Message) value;
				System.out.println(value);
				msg.write("Welt");
				return false;
			}
		});
		server.start();

		// Client and send Hallo
		NodeProxyTCP client=NodeProxyTCP.create("localhost", 5000);
		client.withListener(new ObjectCondition() {
			
			@Override
			public boolean update(Object value) {
				System.out.println(value.toString());
				return false;
			}
		});
		client.sendMessage(Message.createSimpleString("Hallo"));

		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("END");
	}
}
