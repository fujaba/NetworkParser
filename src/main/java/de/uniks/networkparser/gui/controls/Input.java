package de.uniks.networkparser.gui.controls;

public class Input<T> extends Control {
	/* constants */
	public static final String INPUT = "input";
	public static final String VALUE = "value";

	/* variables */
	protected T value;

	public Input() {
		super();
		/* Set variables of parent class */
		this.className = INPUT;
	}

	@Override
	public Object getValue(String key) {
		if (VALUE.equals(key)) {
			return this.value;
		} 
		return super.getValue(key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean setValue(String key, Object value) {
		if (VALUE.equalsIgnoreCase(key)) {
			this.value = (T) value;
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
