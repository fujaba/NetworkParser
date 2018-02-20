package de.uniks.networkparser.gui.controls;

public class DateTimeField extends Input<String> {
	/*
	 * Constants
	 */
	protected static final String TIME = "time";
	protected static final String DATE = "date";

	public DateTimeField() {
		super();
		this.type = DATE;
	}

	public DateTimeField(String type) {
		super();
		this.type = type;
	}
	public static DateTimeField createDateField() {
		return new DateTimeField();
	}
	public static DateTimeField createTimeField() {
		return new DateTimeField(TIME);
	}
}
