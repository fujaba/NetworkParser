package de.uniks.networkparser.gui.controls;

public class Input<T> extends Control {
	/* constants */
	public static final String INPUT = "input";
	public static final String TYPE = "type";
	public static final String VALUE = "value";

	/* variables */
	protected T value;
	protected String type;

	public Input() {
		super();
		/* Set variables of parent class */
		this.className = INPUT;
		this.addBaseElements(VALUE);
		this.addBaseElements(TYPE);
	}

	@Override
	public Object getValue(String key) {
		if (VALUE.equals(key)) {
			return this.value;
		} else if (TYPE.equals(key)) {
			return this.type;
		} 
		return super.getValue(key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean setValue(String key, Object value) {
		if (VALUE.equalsIgnoreCase(key)) {
			this.value = (T) value;
			return true;
		} else if (TYPE.equals(key)) {
			this.type = String.valueOf(value);
			return true;
		} 
		return super.setValue(key, value);
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value	 the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}
}
