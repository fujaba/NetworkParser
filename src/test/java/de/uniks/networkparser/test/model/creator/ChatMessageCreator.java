package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorByte;
import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.ChatMessage;

public class ChatMessageCreator implements SendableEntityCreator,SendableEntityCreatorByte, SendableEntityCreatorXML {
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
	public byte getEventTyp() {
		return (byte)0x80;
	}

	@Override
	public String getTag() {
		return "chatmsg";
	}
}