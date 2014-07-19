package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.DBEntity;

public class DBEntityCreator implements SendableEntityCreator{
	@Override
	public String[] getProperties() {
		return new String[]{DBEntity.PROPERTY_TEXT, DBEntity.PROPERTY_CHECK, DBEntity.PROPERTY_COMBO, DBEntity.PROPERTY_PASSWORD,
				DBEntity.PROPERTY_DATE, DBEntity.PROPERTY_NUMBER, DBEntity.PROPERTY_SPINNER, DBEntity.PROPERTY_PERSON};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new DBEntity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((DBEntity)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((DBEntity)entity).set(attribute, value);
	}

}
