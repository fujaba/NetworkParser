package de.uniks.networkparser.ext.petaf.messages.util;

import de.uniks.networkparser.ext.petaf.messages.PingMessage;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PingMessageCreator extends MessageCreator implements SendableEntityCreator{

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PingMessage();
	}
}
