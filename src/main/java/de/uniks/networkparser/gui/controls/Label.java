package de.uniks.networkparser.gui.controls;

public class Label extends Control {
	public static final String SPACER = "spacer";
	public static final String TITLE = "title";
	public static final String VALUE = "value";
	private String type;
	private String value;

	public Label() {
		this.addBaseElements(VALUE);
	}

	public String getType() {
		return type;
	}

	public Label withType(String type) {
		this.type = type;
		return this;
	}

	public Label withValue(String value) {
		this.value = value;
		return this;
	}

	public boolean setValue(String value) {
		if(value != this.value) {
			this.value = value;
			return true;
		}
		return false;
	}


	public String getValue() {
		return this.value;
	}

	public int length() {
		if(this.value== null ) {
			return 0;
		}
		return this.value.length();
	}
}
