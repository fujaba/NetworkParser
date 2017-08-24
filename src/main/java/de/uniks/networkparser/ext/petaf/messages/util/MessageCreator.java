package de.uniks.networkparser.ext.petaf.messages.util;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class MessageCreator implements SendableEntityCreator, SendableEntityCreatorNoIndex{
	private final String[] props=new String[]{
			Message.PROPERTY_HISTORYID,
			Message.PROPERTY_MSG,
			Message.PROPERTY_PREVIOUSCHANGE,
			Message.PROPERTY_RECEIVER,
			Message.PROPERTY_RECEIVED
	};

	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Message();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Message)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((Message)entity).set(attribute, value);
	}
}
