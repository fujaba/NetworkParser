package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Barbarian;

public class BarbarianCreator implements SendableEntityCreator {

	@Override
	public String[] getProperties() {
		return new String[]{Barbarian.PROPERTY_POSITION, Barbarian.PROPERTY_GAME};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Barbarian();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Barbarian)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((Barbarian)entity).set(attribute, value);
	}

}