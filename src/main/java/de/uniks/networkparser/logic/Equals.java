package de.uniks.networkparser.logic;
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
/**
 * @author Stefan Lindel Clazz of EqualsCondition
 */

public class Equals implements ObjectCondition, SendableEntityCreator {
	/** Constant of KEY. */
	public static final String PROPERTY_KEY = "key";
	
	/** Constant of StrValue. */
	public static final String PROPERTY_VALUE = "value";
	/** Constant of Position. */
	public static final String PROPERTY_POSITION = "position";

	/** Variable of StrValue. */
	private String key;

	/** Variable of Value. */
	private Object value;
	
	/** Variable of ValueStrValue. */
	private Object delta;
	/**
	 * Variable of Position. Position of the Byte or -1 for currentPosition
	 */
	private int position = -1;

	@Override
	public boolean update(Object evt) {
		if (evt == null) {
			return value == null;
		}
		if(value == null) {
			return evt == null;
		}
		if(evt instanceof SimpleKeyValueList<?, ?>) {
			SimpleKeyValueList<?, ?> keyValueList = (SimpleKeyValueList<?, ?>) evt;
			Object value = keyValueList.get(this.key);
			if(value != null) {
				return value.equals(this.value);
			}
		}
		if((evt instanceof PropertyChangeEvent) == false) {
			if(value instanceof Number && evt instanceof Number) {
				// Check for Number
				if(value instanceof Byte 
						|| value instanceof Short
						|| value instanceof Integer
						|| value instanceof Long) {
					Long expValue = (Long)value;
					Long evtValue = (Long) evt;
					if(delta != null) {
						Long deltaValue = (Long) delta;
						return ((expValue - deltaValue) <= evtValue && (expValue + deltaValue)>= evtValue);
					}
					return expValue == evtValue;
				}
				// FLOAT DOUBLE AND OTHER
				Double expValue = (Double)value;
				Double evtValue = (Double) evt;
				if(delta != null) {
					Double deltaValue = (Double) delta;
					return ((expValue - deltaValue) <= evtValue && (expValue + deltaValue)>= evtValue);
				}
				return expValue == evtValue;
			}
			return value.equals(evt);
		}
		
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		if (event.getSource() instanceof BufferedBuffer && value instanceof Byte) {
			Byte btrValue = (Byte) value;
			BufferedBuffer buffer = (BufferedBuffer) event.getSource();
			int pos;
			if (position < 0) {
				pos = buffer.position();
			} else {
				pos = position;
			}
			return buffer.byteAt(pos) == btrValue;
		}
		
		if(event.getPropertyName() == null) {
			return false;
		}
		return event.getPropertyName().equals(value);
	}

	/**
	 * @param value		The new Position
	 * @return 			Equals Instance
	 */
	public Equals withPosition(int value) {
		this.position = value;
		return this;
	}

	/**
	 * @return The Position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param value		The new StringValue
	 * @return 			Equals Instance
	 */
	public Equals withValue(Object value) {
		this.value = value;
		return this;
	}
	
	public Equals withValue(Object value, Object delta) {
		this.withValue(value);
		this.withDelta(delta);
		return this;
	}

	/** @return The StringVlaue */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "==" + value + " ";
	}

	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_KEY, PROPERTY_VALUE, PROPERTY_POSITION};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Equals();
	}
	
	public String getKey() {
		return key;
	}

	public Equals withKey(String key) {
		this.key = key;
		return this;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getKey();
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getValue();
		}
		if (PROPERTY_POSITION.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getPosition();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(entity instanceof Equals == false) {
			return false;
		}
		Equals element = (Equals) entity;
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			element.withKey(String.valueOf(value));
			return true;
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			element.withValue(value);
			return true;
		}
		if (PROPERTY_POSITION.equalsIgnoreCase(attribute)) {
			element.withPosition(Integer.parseInt("" + value));
			return true;
		}
		return false;
	}

	public Object getDelta() {
		return delta;
	}

	public Equals withDelta(Object delta) {
		this.delta = delta;
		return this;
	}
	
	public static Equals create(String key, Object value) {
		Equals condition = new Equals();
		condition.withKey(key);
		condition.withValue(value);
		return condition;
	}
	
	public static Equals createNullCondition() {
		return new Equals().withValue(null);
	}
}
