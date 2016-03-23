package de.uniks.networkparser.test.model;

public class Location {
	public static final String PROPERTY_X = "x";
	public static final String PROPERTY_Y = "y";
	private int x;
	private int y;

	public Location() {

	}

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean set(String attrName, Object value) {
		if (PROPERTY_X.equalsIgnoreCase(attrName)) {
			setX((Integer) value);
			return true;
		} else if (PROPERTY_Y.equalsIgnoreCase(attrName)) {
			setY((Integer) value);
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
		if (PROPERTY_X.equalsIgnoreCase(attribute)) {
			return getX();
		} else if (PROPERTY_Y.equalsIgnoreCase(attribute)) {
			return getY();
		}
		return null;
	}

	public String toStringShort(){
		return getX()+ ":" +getY();
	}
}