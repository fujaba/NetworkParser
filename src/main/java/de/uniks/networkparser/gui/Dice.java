package de.uniks.networkparser.gui;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class Dice.
 *
 * @author Stefan
 */
public class Dice extends SendableItem implements SendableEntityCreator {
	
	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";
	
	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "id";

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "Dice: " + this.getValue();
	}

	private int value;
	private String id;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(int value) {
		if (this.value != value) {
			int oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(Object value) {
		if (!(value instanceof Integer)) {
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

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the dice
	 */
	public Dice withValue(int value) {
		setValue(value);
		return this;
	}

	/**
	 * Sets the id.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setId(String value) {
		if (this.id != value) {
			int oldValue = this.value;
			this.id = value;
			firePropertyChange(PROPERTY_ID, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_ID, PROPERTY_VALUE };
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof Dice)) {
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

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof Dice)) {
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

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return null;
	}
}
