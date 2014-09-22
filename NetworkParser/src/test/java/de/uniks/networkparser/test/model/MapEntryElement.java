package de.uniks.networkparser.test.model;

import java.util.HashMap;

import de.uniks.networkparser.AbstractEntity;

public class MapEntryElement {
	public static final String PROPERTY_VALUE="value";
	private HashMap<String, Object> value;

	public HashMap<String, Object> getValue() {
		return value;
	}

	public void setValue(HashMap<String, Object> value) {
		this.value = value;
	}
	
	public boolean addToValue(String key, Object value) {
		boolean changed = false;

		if (value != null) {
			if (this.value == null) {
				this.value = new HashMap<String, Object>();

			}
			this.value.put(key, value);
			changed=true;
		}
		return changed;
	}
	
	@SuppressWarnings("unchecked")
	public boolean set(String attrName, Object value) {
		if (PROPERTY_VALUE.equalsIgnoreCase(attrName)) {
			if(value instanceof HashMap<?, ?>){
				setValue((HashMap<String, Object>) value);	
			} else if(value instanceof AbstractEntity){
				AbstractEntity<?,?> item=(AbstractEntity<?,?>) value;
				addToValue((String) item.getKey(), item.getValue());
			}
			return true;
		}
		return false;
	}
	public Object get(String attrName) {
		int pos = attrName.indexOf(".");
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return getValue();
		}
		return null;
	}
}
