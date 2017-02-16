package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class EntityEquals implements UpdateListener, SendableEntityCreator {
	/** Constant of KEY. */
	public static final String PROPERTY_KEY = "key";

	/** Constant of VALUE. */
	public static final String PROPERTY_VALUE = "value";
	
	/** Variable of StrValue. */
	private String key;
	private Object value;

	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_KEY, PROPERTY_VALUE };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new EntityEquals();
	}

	@Override
	public boolean update(Object map) {
		if(map instanceof SimpleKeyValueList<?, ?>) {
			SimpleKeyValueList<?, ?> keyValueList = (SimpleKeyValueList<?, ?>) map;
			Object value = keyValueList.get(this.key);
			if(value != null) {
				return value.equals(this.value);
			}
		}
		return false;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			return ((EntityEquals) entity).getKey();
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((EntityEquals) entity).getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			((EntityEquals) entity).withKey(String.valueOf(value));
			return true;
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			((EntityEquals) entity).withValue(value);
			return true;
		}
		return false;
	}

	public String getKey() {
		return key;
	}

	public EntityEquals withKey(String key) {
		this.key = key;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public EntityEquals withValue(Object value) {
		this.value = value;
		return this;
	}

	public static EntityEquals create(String key, Object value) {
		EntityEquals condition = new EntityEquals();
		condition.withKey(key);
		condition.withValue(value);
		return condition;
	}
}
