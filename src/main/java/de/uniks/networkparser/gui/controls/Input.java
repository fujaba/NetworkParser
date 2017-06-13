package de.uniks.networkparser.gui.controls;

public class Input<T> extends Control {
	/* constants */
	public static final String INPUT = "input";
	public static final String TYPE = "type";
	public static final String VALUE = "value";

	/* variables */
//	protected SimpleGUI $view;
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
		} 
		if (TYPE.equals(key)) {
			return this.type;
		} 
		return super.getValue(key);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean setValue(String key, Object value) {
		key = key.trim();
		if (VALUE.equalsIgnoreCase(key)) {
			return this.setValue((T)value);
		}
		if (TYPE.equals(key)) {
			return this.setType(""+value);
		} 
		return super.setValue(key, value);
	}

	public boolean setType(String value) {
		String oldValue = this.type;
		this.type = value;
		return firePropertyChange(TYPE, oldValue, value);
	}
	
	public String getType(String value) {
		return this.type;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value	 the value to set
	 * @return success
	 */
	public boolean setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		return firePropertyChange(VALUE, oldValue, value);
	}
}
