package de.uniks.networkparser.test.model.ludo.creator;

import java.util.Date;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;

public class DateCreator implements SendableEntityCreator {
	private final String[] properties = new String[] {};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Date();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return null;
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (JsonIdMap.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return false;
	}
}
