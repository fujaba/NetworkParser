package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroker;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

public class MQTTClient {
	public static void main(String[] args) {
		NodeProxyBroker broker = NodeProxyBroker.createMQTTBroker("broker.hivemq.com");
		broker.withCallback(new SimpleEventCondition() {
			@Override
			public boolean update(SimpleEvent event) {
				if(NodeProxyBroker.EVENT_MESSAGE.equals(event.getType())) {
					System.out.println("TOPIC: "+event.getPropertyName());
					System.out.println("Message: "+event.getNewValue());
				} else {
					System.out.println(event.getType());
				}
				return true;
			}
		});
		broker.connect();
		broker.subscribe("ALL");
		broker.publish("ALL", "Hallo World 42");
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		broker.close();
	}
}
