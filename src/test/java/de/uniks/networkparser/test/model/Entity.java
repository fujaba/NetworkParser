package de.uniks.networkparser.test.model;

public class Entity {
	public static final String PROPERTY_CHILD="item";
	public static final String PROPERTY_VALUE="value";
	private Entity child;
	private String value;
	public Entity getChild() {
		return child;
	}
	public void setChild(Entity child) {
		this.child = child;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_CHILD)) {
			setChild((Entity) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			withValue((String) value);
			return true;
		}
		return false;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_CHILD)) {
			return getChild();
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return getValue();
		}
		return null;
	}
	public String getValue() {
		return value;
	}
	public Entity withValue(String value) {
		this.value = value;
		return this;
	}
}
