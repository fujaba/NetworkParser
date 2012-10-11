package de.uniks.jism.event.creater;

import java.util.Map.Entry;

import de.uniks.jism.event.JsonEntry;
import de.uniks.jism.interfaces.NoIndexCreator;
import de.uniks.jism.interfaces.SendableEntityCreator;

public class JsonEntryCreator implements SendableEntityCreator, NoIndexCreator{
	public static final String PROPERTY_KEY="key";
	public static final String PROPERTY_VALUE="value";
	private final String[] properties=new String[]{PROPERTY_KEY, PROPERTY_VALUE};
	
	public String[] getProperties() {
		return properties;
	}

	public Object getSendableInstance(boolean prototyp) {
		return new JsonEntry();
	}

	public Object getValue(Object entity, String attribute) {
		Entry<?,?> obj=((Entry<?,?>)entity);
		if(PROPERTY_KEY.equalsIgnoreCase(attribute)){
			return obj.getKey();
		}else if(PROPERTY_VALUE.equalsIgnoreCase(attribute)){
			return obj.getValue();
		}
		return null;
	}

	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		JsonEntry entry=(JsonEntry) entity;
		if(PROPERTY_KEY.equalsIgnoreCase(attribute)){
			entry.setKey(value);
			return true;
		}else if(PROPERTY_VALUE.equalsIgnoreCase(attribute)){
			entry.setValue(value);
			return true;
		}
		return false;
	}

}
