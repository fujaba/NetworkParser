package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyMQTT;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

public class MQTTClient {
	public static void main(String[] args) throws MqttException {
		NodeProxyMQTT client = new NodeProxyMQTT("tcp://broker.hivemq.com:1883", NodeProxyMQTT.generateClientId());
		client.withCallback(new SimpleEventCondition() {
				@Override
				public boolean update(SimpleEvent event) {
					if(NodeProxyMQTT.EVENT_MESSAGE.equals(event.getType())) {
						System.out.println("TOPIC: "+event.getPropertyName());
						System.out.println("Message: "+event.getNewValue());
					}
					return true;
				}
			}
		);
		client.connect();
		client.subscribe("ALL");
		MqttMessage message = new MqttMessage("Hallo World 42".getBytes());
		client.publish("ALL", message);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.close();

	}
}
