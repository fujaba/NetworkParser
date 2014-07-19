package de.uniks.networkparser.test.model;

public class Uni {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_VALUE = "&fg?value";
	public static final String PROPERTY_USER = "&fg&user";
	public static final String PROPERTY_ICH = "&child&value";
	private String name;
	private String value;
	private String user;
	private String ich;

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_NAME)) {
			return getName();
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return getValue();
		} else if (attribute.equalsIgnoreCase(PROPERTY_USER)) {
			return getUser();
		} else if (attribute.equalsIgnoreCase(PROPERTY_ICH)) {
			return getIch();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_NAME)) {
			setName((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			setValue((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_USER)) {
			setUser((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_ICH)) {
			setIch((String) value);
			return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getIch() {
		return ich;
	}

	public void setIch(String ich) {
		this.ich = ich;
	}
}
