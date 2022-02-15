package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.ObjectCondition;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class BooleanCondition.
 *
 * @author Stefan
 */
public class BooleanCondition implements ObjectCondition, SendableEntityCreator {
	
	/** The Constant VALUE. */
	public static final String VALUE = "value";
	private boolean value;

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		return this.value;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the boolean condition
	 */
	public BooleanCondition withValue(boolean value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Static Method for instance a new Instance of BooleanCondition Object.
	 *
	 * @param value Value
	 * @return The new Instance
	 */
	public static BooleanCondition create(boolean value) {
		return new BooleanCondition().withValue(value);
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { VALUE };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new BooleanCondition();
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
		if (VALUE.equalsIgnoreCase(attribute)) {
			return ((BooleanCondition) entity).getValue();
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
		if (VALUE.equalsIgnoreCase(attribute)) {
			((BooleanCondition) entity).withValue((Boolean) value);
			return true;
		}
		return false;
	}
}
