package de.uni.kassel.peermessage.event.creater;

import de.uni.kassel.peermessage.event.ByteMessage;
import de.uni.kassel.peermessage.interfaces.PrimaryEntityCreator;

public class ByteMessageCreator implements PrimaryEntityCreator {
	private final String[] properties = new String[] { ByteMessage.PROPERTY_VALUE };

	public String[] getProperties() {
		return properties;
	}

	public Object getSendableInstance(boolean reference) {
		return new ByteMessage();
	}

	public byte getEventTyp() {
		return 0x01;
	}

	public Object getValue(Object entity, String attribute) {
		return ((ByteMessage) entity).get(attribute);
	}

	public boolean setValue(Object entity, String attribute, Object value) {
		return ((ByteMessage) entity).set(attribute, value);
	}
}
