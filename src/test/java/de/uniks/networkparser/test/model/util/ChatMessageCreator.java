package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.ChatMessage;

public class ChatMessageCreator implements SendableEntityCreator, SendableEntityCreatorTag {
	@Override
	public String[] getProperties() {
		return new String[] { ChatMessage.PROPERTY_SENDER,
				ChatMessage.PROPERTY_TIME, ChatMessage.PROPERTY_TEXT, ChatMessage.PROPERTY_COUNT, ChatMessage.PROPERTY_ACTIV};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new ChatMessage();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((ChatMessage) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((ChatMessage) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "chatmsg";
	}
}