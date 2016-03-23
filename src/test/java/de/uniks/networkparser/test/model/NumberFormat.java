package de.uniks.networkparser.test.model;

public class NumberFormat {
	public static final String PROPERTY_NUMBER="number";
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean set(String attrName, Object value) {
		if (PROPERTY_NUMBER.equalsIgnoreCase(attrName)) {
			setNumber(Integer.valueOf("" +value));
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
		if (PROPERTY_NUMBER.equalsIgnoreCase(attribute)) {
			return getNumber();
		}
		return null;
	}
}
