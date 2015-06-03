package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.JabberBindMessage;

public class JabberBindMessageCreator implements SendableEntityCreatorTag {
	public static final String[] props = new String[] {
		JabberBindMessage.PROPERTY_ID, JabberBindMessage.PROPERTY_JID, JabberBindMessage.PROPERTY_TYPE, JabberBindMessage.PROPERTY_BINDXMLNS, JabberBindMessage.PROPERTY_RESOURCE};

	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new JabberBindMessage();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((JabberBindMessage) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((JabberBindMessage) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "iq";
	}
}
