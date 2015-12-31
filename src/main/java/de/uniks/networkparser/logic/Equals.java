package de.uniks.networkparser.logic;

import de.uniks.networkparser.SimpleValuesMap;
import de.uniks.networkparser.interfaces.BufferedBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/**
 * @author Stefan Lindel Clazz of EqualsCondition
 */

public class Equals extends SimpleConditionMap implements SendableEntityCreator {
	/** Constant of StrValue. */
	public static final String STRINGVALUE = "stringvalue";
	/** Constant of Position. */
	public static final String POSITION = "position";
	/** Constant of ByteValue. */
	public static final String BYTEVALUE = "bytevalue";

	/** Variable of StrValue. */
	private String strValue;
	/**
	 * Variable of Position. Position of the Byte or -1 for currentPosition
	 */
	private int position = -1;
	/** Variable of ByteValue. */
	private Byte bytevalue;

	@Override
	public boolean check(SimpleValuesMap values) {
		if (values.getEntity() instanceof BufferedBuffer) {
			BufferedBuffer buffer = (BufferedBuffer) values.getEntity();
			int pos;
			if (position < 0) {
				pos = buffer.position();
			} else {
				pos = position;
			}
			return buffer.byteAt(pos) == bytevalue;
		}
		if (values.getValue() == null) {
			return (strValue == null);
		}
		return values.getValue().equals(strValue);
	}

	/**
	 * @param value
	 *            The new Position
	 * @return Equals Instance
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
	 * @param value
	 *            The new ByteValue
	 * @return Equals Instance
	 */
	public Equals withValue(Byte value) {
		this.bytevalue = value;
		return this;
	}

	/**
	 * @return The ByteValue
	 */
	public Byte getBytevalue() {
		return bytevalue;
	}

	/**
	 * @param value
	 *            The new StringValue
	 * @return Equals Instance
	 */
	public Equals withValue(String value) {
		this.strValue = value;
		return this;
	}

	/** @return The StringVlaue */
	public String getStringvalue() {
		return strValue;
	}

	@Override
	public String toString() {
		if (strValue != null) {
			return "==" + strValue + " ";
		}
		return "==" + bytevalue + " ";
	}

	@Override
	public String[] getProperties() {
		return new String[] {STRINGVALUE, POSITION, BYTEVALUE };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Equals();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (STRINGVALUE.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getStringvalue();
		}
		if (POSITION.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getPosition();
		}
		if (BYTEVALUE.equalsIgnoreCase(attribute)) {
			return ((Equals) entity).getBytevalue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (STRINGVALUE.equalsIgnoreCase(attribute)) {
			((Equals) entity).withValue(String.valueOf(value));
			return true;
		}
		if (POSITION.equalsIgnoreCase(attribute)) {
			((Equals) entity).withPosition(Integer.parseInt("" + value));
			return true;
		}
		if (BYTEVALUE.equalsIgnoreCase(attribute)) {
			((Equals) entity).withValue((Byte) value);
			return true;
		}
		return false;
	}
}
