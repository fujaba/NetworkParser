package de.uniks.networkparser.test.model;

public class StringMessage {
	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_VALUE = "value";
	private String value;
	private int id;

	public StringMessage() {
	}

	public StringMessage(String message) {
		setValue(message);
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			return id;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return getValue();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			setId((Integer) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			setValue((String) value);
			return true;
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Value: " + getValue();

	}

}
