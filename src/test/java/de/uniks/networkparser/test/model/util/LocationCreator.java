package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Location;

public class LocationCreator implements SendableEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[] { Location.PROPERTY_X, Location.PROPERTY_Y };
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Location();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Location) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Location) entity).set(attribute, value);
	}

}
