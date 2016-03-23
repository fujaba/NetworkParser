package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.FIXMLMessage;

public class FIXMLMessageCreator implements SendableEntityCreatorTag {
	private final String[] properties = new String[] { FIXMLMessage.PROPERTY_APPLICATIONMESSAGE, };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new FIXMLMessage();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((FIXMLMessage) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (IdMap.REMOVE.equals(type) && value != null) {
			attrName = attrName + type;
		}
		return ((FIXMLMessage) target).set(attrName, value);
	}

	@Override
	public String getTag() {
		return "FIXML.FIXMLMessage";
	}
}
