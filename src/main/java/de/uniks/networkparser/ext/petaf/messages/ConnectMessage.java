package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;

/**
 * Sending Connection Link with all Input Proxies and Filter 
 * @author Stefan Lindel
 */
public class ConnectMessage extends ReceivingTimerTask {
	public static final String PROPERTY_TYPE="connect";
	public static ConnectMessage create() {
		ConnectMessage msg = new ConnectMessage();
		msg.withSendAnyHow(true);
		return msg; 
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ConnectMessage();
	}

	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}
}