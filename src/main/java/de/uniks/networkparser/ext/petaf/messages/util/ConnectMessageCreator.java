package de.uniks.networkparser.ext.petaf.messages.util;

import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;

public class ConnectMessageCreator  extends MessageCreator {
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ConnectMessage();
	}
}