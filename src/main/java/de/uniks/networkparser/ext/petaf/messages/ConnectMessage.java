package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.Space;

/**
 * Sending Connection Link with all Input Proxies and Filter 
 * @author Stefan Lindel
 */
public class ConnectMessage extends Message {
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
	public boolean handle(Space space) {
		// TODO Auto-generated method stub
		return super.handle(space);
	}
}
