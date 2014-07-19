package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.ApplicationMessage;

public class ApplicationMessageCreator implements SendableEntityCreatorXML {
	private final String[] properties = new String[] {
			ApplicationMessage.PROPERTY_FIXMLMESSAGE,
			ApplicationMessage.PROPERTY_ORDER, };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new ApplicationMessage();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((ApplicationMessage) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (JsonIdMap.REMOVE.equals(type) && value != null) {
			attrName = attrName + type;
		}
		return ((ApplicationMessage) target).set(attrName, value);
	}

	@Override
	public String getTag() {
		return "ApplicationMessage";
	}
}
