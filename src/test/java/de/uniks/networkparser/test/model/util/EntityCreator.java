package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.Entity;
import de.uniks.networkparser.xml.XMLTokener;

public class EntityCreator implements SendableEntityCreatorTag {

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
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(XMLTokener.CHILDREN.equals(type)) {
			((Entity) entity).setChild((Entity) value);
			return true;
		}
 		return ((Entity) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "item";
	}
}
