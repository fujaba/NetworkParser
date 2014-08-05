package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.Uni;

public class UniCreator implements SendableEntityCreatorXML {

	@Override
	public String[] getProperties() {
		return new String[] { Uni.PROPERTY_NAME, Uni.PROPERTY_VALUE,
				Uni.PROPERTY_USER, Uni.PROPERTY_ICH};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Uni();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Uni) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Uni) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "uni";
	}
}