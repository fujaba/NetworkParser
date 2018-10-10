package org.sdmlib.simple.model.enums_d;


public enum TestEnum {
TEACHER(42);
TestEnum(Object value){}
	public static final String PROPERTY_VALUE = "value";

	private Object value;

	public Object getValue() {
		return this.value;
	}

	public boolean setValue(Object value) {
		if (this.value != value) {
			this.value = value;
			return true;
		}
		return false;
	}

	public TestEnum withValue(Object value) {
		setValue(value);
		return this;
	}

}