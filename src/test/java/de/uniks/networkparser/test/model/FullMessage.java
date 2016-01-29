package de.uniks.networkparser.test.model;

import java.util.Date;

public class FullMessage {
	public static final String PROPERTY_TEXT = "txt";
	public static final String PROPERTY_VALUE = "number";
	public static final String PROPERTY_DATE = "date";
	public static final String PROPERTY_EMPTYVALUE = "emptyvalue";
	public static final String PROPERTY_LOCATION = "location";
	private String text;
	private int value;
	private Date date;
	private int emptyValue;
	private Location location;

	public FullMessage() {

	}
	public FullMessage(int value, String text) {
		this.setValue(value);
		this.setText(text);
	}
	public FullMessage(Date date, int value, String text) {
		this.setDate(date);
		this.setValue(value);
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_TEXT)) {
			return getText();
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return getValue();
		} else if (attribute.equalsIgnoreCase(PROPERTY_DATE)) {
			return getDate();
		} else if (attribute.equalsIgnoreCase(PROPERTY_EMPTYVALUE)) {
			return getEmptyValue();
		} else if (attribute.equalsIgnoreCase(PROPERTY_LOCATION)) {
			return getLocation();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_TEXT)) {
			setText((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			setValue((Integer) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_DATE)) {
			setDate((Date) value);
		} else if (attribute.equalsIgnoreCase(PROPERTY_EMPTYVALUE)) {
			setEmptyValue((Integer) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_LOCATION)) {
			setLocation((Location) value);
			return true;
		}
		return false;
	}

	public int getEmptyValue() {
		return emptyValue;
	}

	public void setEmptyValue(int emptyValue) {
		this.emptyValue = emptyValue;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
