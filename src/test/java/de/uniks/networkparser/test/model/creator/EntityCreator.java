package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.Entity;

public class EntityCreator implements SendableEntityCreatorXML {

	@Override
	public String[] getProperties() {
		return new String[] { Entity.PROPERTY_CHILD};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Entity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Entity) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Entity) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "item";
	}
}
