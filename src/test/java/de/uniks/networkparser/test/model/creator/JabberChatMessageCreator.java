package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.JabberChatMessage;

public class JabberChatMessageCreator implements SendableEntityCreatorXML {
	public static final String[] props = new String[] {
			JabberChatMessage.PROPERTY_FROM, JabberChatMessage.PROPERTY_TO,
			JabberChatMessage.PROPERTY_ID, JabberChatMessage.PROPERTY_TYPE,
			JabberChatMessage.PROPERTY_BODY };

	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new JabberChatMessage();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((JabberChatMessage) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((JabberChatMessage) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "message";
	}
}
