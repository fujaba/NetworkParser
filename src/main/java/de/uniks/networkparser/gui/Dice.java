package de.uniks.networkparser.gui;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class Dice extends SendableItem implements SendableEntityCreator {
	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_ID = "id";

	@Override
	public String toString() {
		return "Dice: " + this.getValue();
	}

	private int value;
	private String id;

	public int getValue() {
		return this.value;
	}

	public boolean setValue(int value) {
		if (this.value != value) {
			int oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
			return true;
		}
		return false;
	}

	public boolean setValue(Object value) {
		if (value instanceof Integer == false) {
			return false;
		}
		int newValue = (Integer) value;
		if (this.value != newValue) {
			int oldValue = this.value;
			this.value = newValue;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
			return true;
		}
		return false;
	}

	public Dice withValue(int value) {
		setValue(value);
		return this;
	}

	public boolean setId(String value) {
		if (this.id != value) {
			int oldValue = this.value;
			this.id = value;
			firePropertyChange(PROPERTY_ID, oldValue, value);
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}

	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_ID, PROPERTY_VALUE };
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof Dice == false) {
			return null;
		}
		Dice dice = (Dice) entity;
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return dice.getValue();
		}
		if (PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return dice.getId();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof Dice == false) {
			return false;
		}
		Dice dice = (Dice) entity;
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			if (value instanceof Integer) {
				return dice.setValue((Integer) value);
			}
		}
		if (PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return dice.setId("" + value);
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return null;
	}
}
