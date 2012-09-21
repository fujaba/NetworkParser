package de.uniks.peermessage.event.creater;

import de.uniks.peermessage.interfaces.SendableEntityCreator;
import de.uniks.peermessage.json.JsonArray;

public class JsonArrayCreator implements SendableEntityCreator{
	private final String[] properties= new String[]{"VALUE"};
	@Override
	public String[] getProperties() {
		return this.properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new JsonArray();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return entity.toString();
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((JsonArray)entity).setAllValue((String) value);
	}

}
