package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.FullMessage;

public class FullMessageCreator implements SendableEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[] { FullMessage.PROPERTY_TEXT,
				FullMessage.PROPERTY_VALUE, FullMessage.PROPERTY_DATE,
				FullMessage.PROPERTY_EMPTYVALUE, FullMessage.PROPERTY_LOCATION };
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new FullMessage();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((FullMessage) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((FullMessage) entity).set(attribute, value);
	}
}