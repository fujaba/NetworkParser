package de.uniks.networkparser.ext.petaf.messages.util;

import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;

public class ChangeMessageCreator extends MessageCreator {

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ChangeMessage();
	}
}
