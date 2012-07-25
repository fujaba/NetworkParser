package de.uni.kassel.peermessage.event.creater;

import de.uni.kassel.peermessage.interfaces.SendableEntityCreator;
import de.uni.kassel.peermessage.json.JsonObject;

public class JsonObjectCreator implements SendableEntityCreator{
	private final String[] properties= new String[]{"VALUE"};
	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new JsonObject();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return entity.toString();
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value) {
		return ((JsonObject)entity).setAllValue((String) value);
	}

}
