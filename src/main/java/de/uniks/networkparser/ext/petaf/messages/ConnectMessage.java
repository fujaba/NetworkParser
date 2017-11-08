package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.NodeProxy;
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
	public boolean runTask() throws Exception {
		if(super.runTask() ) {
			return true;
		}
		
		AcceptMessage acceptTaskSend = AcceptMessage.create();
		NodeProxy sender = this.getReceiver();
		if(sender != null) {
			if(sender.sendMessage(acceptTaskSend)) {
				this.receiver.withOnline(true);
			}
		}
		return true;
	}

	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}
	
	@Override
	public boolean isSendingToPeers() {
		return false;
	}
}