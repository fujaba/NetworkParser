package de.uniks.networkparser.test.model;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class Change {
	public static final String PROPERTY_KEY="key";
	public static final String PROPERTY_VALUE="value";
	public static final String PROPERTY_LIST="list";
	private Long key;
	private JsonObject value;
	private JsonArray list;

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_KEY)) {
			return getKey();
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return getValue();
		} else if(attribute.equalsIgnoreCase(PROPERTY_LIST)) {
			return getList();
		}
		return null;
	}
	
	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_KEY)) {
			setKey((Long) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			setValue((JsonObject) value);
			return true;
		} else if(attribute.equalsIgnoreCase(PROPERTY_LIST)) {
			setList((JsonArray) value);
			return true;
		}
		return false;
	}
	public JsonObject getValue() {
		return value;
	}

	public void setValue(JsonObject value) {
		this.value = value;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public JsonArray getList() {
		return list;
	}

	public void setList(JsonArray list) {
		this.list = list;
	}
}