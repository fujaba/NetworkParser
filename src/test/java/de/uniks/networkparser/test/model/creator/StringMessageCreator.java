package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreatorByte;
import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.StringMessage;

public class StringMessageCreator implements SendableEntityCreatorByte,
		SendableEntityCreatorXML {
	private final String[] properties = new String[] {StringMessage.PROPERTY_ID, StringMessage.PROPERTY_VALUE };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new StringMessage();
	}

	@Override
	public byte getEventTyp() {
		return 0x02;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((StringMessage) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((StringMessage) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "p";
	}
}
