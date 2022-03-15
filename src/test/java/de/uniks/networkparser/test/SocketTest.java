package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.Server_Time;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SocketTest {

	@Test
	public void testTimeServer() {
		Server_Time server_Time = new Server_Time(true);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		server_Time.close();
	}
	
	private int i;
	@Test
	public void testSocket() {

		// Server
		NodeProxyTCP server=NodeProxyTCP.createServer(5000);
		server.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				Message msg = (Message) value;
				msg.write("Welt");
				return false;
			}
		});
		server.start();

		// Client and send Hallo
		
		String[] output = {"Welt","Hallo"};
		
		NodeProxyTCP client=NodeProxyTCP.create("localhost", 5000);
		client.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				assertEquals(output[i++], value.toString());
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
		server.close();
		client.close();
	}
}
