package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.Message;

//TODO ADD FUNCTIONALITY
public class GetObjectMessage extends Message {

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GetObjectMessage();
	}

	@Override
	public boolean isSendingToPeers() {
		return false;
	}
}
