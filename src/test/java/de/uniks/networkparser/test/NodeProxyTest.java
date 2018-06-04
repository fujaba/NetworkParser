package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroker;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.HTMLEntity;

public class NodeProxyTest {

//	@Test
	public void UniversityOfMadness() {
		HTMLEntity answer = NodeProxyTCP.getHTTP("avocado.uniks.de", 33000, "user/info");
		System.out.println(answer);
	}

//	@Test
	public void UniversityOfMadnessCreate() {
		HTMLEntity answer = NodeProxyTCP.postHTTP("avocado.uniks.de", 33000, "user/create", NodeProxyTCP.BODY_JSON, "Username", "Eraser");

		HTMLEntity login = NodeProxyTCP.postHTTP("avocado.uniks.de", 33000, "user/login", NodeProxyTCP.BODY_JSON, "Username", "Eraser", "Password", "crazy");
		System.out.println(login.getStatusCode()+": "+login.getStatusMessage());
		System.out.println("BODY: "+login.getBody().getValue());

//		answer = NodeProxyTCP.postHTTP(login, "api/games/create", NodeProxyTCP.HEADER_PLAIN, "name", "Springfield");
//		System.out.println(answer.getStatusCode()+": "+answer.getStatusMessage());
//		System.out.println("BODY: "+answer.getBody().getValue());

		// Call Chatinfo
		answer = NodeProxyTCP.getHTTP(login, "/api/chat/info");
		System.out.println(answer.getStatusCode()+": "+answer.getStatusMessage());
		System.out.println("BODY: "+answer.getBody().getValue());

		JsonObject value = JsonObject.create(answer.getBody().getValue());
		String user = value.getString("rabbit_user");
		String password = value.getString("rabbit_password");
		String queueName = value.getString("chat_queue");

		NodeProxyBroker broker = new NodeProxyBroker("avocado.uniks.de:32777");
		broker.withAuth(user, password);
		broker.connect();
//		broker.bindExchange("General", queueName);
		broker.subscribe(queueName, new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				SimpleEvent event=(SimpleEvent) value;
				System.out.println(event.getNewValue());
				
				return true;
			}
		});
//		connection = factory.newConnection();
//		Channel channel = connection.createChannel();
//		channel.exchangeDeclare(PUBLIC_CHANNEL, BuiltinExchangeType.FANOUT);
//		channel.queueBind(queueName, PUBLIC_CHANNEL, "");

		
		
		answer = NodeProxyTCP.postHTTP(login, "/api/chat/channel/General", NodeProxyTCP.BODY_PLAIN, "Hallo Welt");
		System.out.println(answer.getStatusCode()+": "+answer.getStatusMessage() + "BODY: "+answer.getBody().getValue());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
