package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.network.Message;

/**
 * Sending Connection Link with all Input Proxies and Filter 
 * @author Stefan Lindel
 */
public class ConnectMessage extends Message{
	public static ConnectMessage create() {
		ConnectMessage msg = new ConnectMessage();
		msg.withSendAnyHow(true);
		return msg; 
	}
}
