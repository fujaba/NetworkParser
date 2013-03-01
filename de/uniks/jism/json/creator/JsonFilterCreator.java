package de.uniks.jism.json.creator;

import de.uniks.jism.interfaces.SendableEntityCreator;
import de.uniks.jism.json.JsonFilter;

public class JsonFilterCreator implements SendableEntityCreator{
	private String[] properties=new String[]{JsonFilter.PROPERTY_DEEP, JsonFilter.PROPERTY_FULLSERIALIZATION, JsonFilter.PROPERTY_ID, JsonFilter.PROPERTY_EXCLUSIVEPROPERTY,JsonFilter.PROPERTY_EXCLUSIVEOBJECT}; 

	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new JsonFilter();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((JsonFilter)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return ((JsonFilter)entity).set(attribute, value);
	}
	

}
