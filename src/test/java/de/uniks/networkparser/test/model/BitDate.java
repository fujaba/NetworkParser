package de.uniks.networkparser.test.model;

public class BitDate {
	public static final String PROPERTY_DAY = "day";
	public static final String PROPERTY_MONTH = "month";
	public static final String PROPERTY_YEAR = "year";
	public static final String PROPERTY_MINUTE = "minute";
	public static final String PROPERTY_HOUR = "hour";
	private int day;
	private int month;
	private int year;
	private int minute;
	private int hour;
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_DAY)) {
			return getDay();
		} else if (attribute.equalsIgnoreCase(PROPERTY_MONTH)) {
			return getMonth();
		} else if (attribute.equalsIgnoreCase(PROPERTY_YEAR)) {
			return getYear();
		} else if (attribute.equalsIgnoreCase(PROPERTY_MINUTE)) {
			return getMinute();
		} else if (attribute.equalsIgnoreCase(PROPERTY_HOUR)) {
			return getHour();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_DAY)) {
			setDay(Integer.valueOf("" +value));
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_MONTH)) {
			setMonth(Integer.valueOf("" +value));
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_YEAR)) {
			setYear(Integer.valueOf("" +value));
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_MINUTE)) {
			setMinute(Integer.valueOf("" +value));
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_HOUR)) {
			setHour(Integer.valueOf("" +value));
			return true;
		}
		return false;
	}
}
