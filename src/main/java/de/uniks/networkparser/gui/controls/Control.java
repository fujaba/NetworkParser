package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.SimpleObject;

public abstract class Control extends SimpleObject{
	/* Constants */
	public static final String PROPERTY = "property";

	public Control() {
		this.baseElements.add(PROPERTY);
	}
	
	/* Variables */
	protected String property;

	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param value the property to set
	 * @return success for setting
	 */
	public boolean setProperty(String value) {
		if(this.property != value) {
			this.property = value;
			return true;
		}
		return false;
	}
	
	public Object getValue(String key) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.property;
		}
		return super.getValue(key);
	}
	
	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			this.property = ""+value;
			return true;
		}
		return super.setValue(key, value);
	}
}
