package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.MapEntryElement;

public class MapEntryElementCreator implements SendableEntityCreator{

	@Override
	public String[] getProperties() {
		return new String[]{MapEntryElement.PROPERTY_VALUE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new MapEntryElement();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((MapEntryElement)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((MapEntryElement)entity).set(attribute, value);
	}
}
