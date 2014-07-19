package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Change;

public class ChangeCreator implements SendableEntityCreator{
	private final String[] properties=new String[]{Change.PROPERTY_KEY, Change.PROPERTY_VALUE, Change.PROPERTY_LIST};
	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Change();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Change)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Change)entity).set(attribute, value);
	}

}
