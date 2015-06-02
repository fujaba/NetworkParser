package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import de.uniks.networkparser.interfaces.Buffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/**
 * @author Stefan Lindel Clazz of EqualsCondition
 */

public class Equals extends ConditionMap implements SendableEntityCreator {
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
	public boolean check(ValuesMap values) {
		if (values.entity instanceof Buffer) {
			Buffer buffer = (Buffer) values.entity;
			int pos;
			if (position < 0) {
				pos = buffer.position();
			} else {
				pos = position;
			}
			return buffer.byteAt(pos) == bytevalue;
		}
		if (values.value == null) {
			return (strValue == null);
		}
		return values.value.equals(strValue);
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
