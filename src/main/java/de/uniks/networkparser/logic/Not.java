package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
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
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * Not Clazz for neg. Condition.
 *
 * @author Stefan Lindel
 */

public class Not implements ParserCondition, SendableEntityCreator {
	
	/** The Constant TAG. */
	public static final String TAG = "not";
	/** Constant for ITEM. */
	public static final String ITEM = "item";
	/** Varibale for Condition. */
	private ObjectCondition item;

	/**
	 * Update.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object evt) {
		if (item != null) {
			return !item.update(evt);
		}
		return false;
	}

	/**
	 * Gets the item.
	 *
	 * @return Not Condition
	 */
	public ObjectCondition getItem() {
		return item;
	}

	/**
	 * With.
	 *
	 * @param value for new Condition
	 * @return Not Instance
	 */
	public Not with(ObjectCondition value) {
		this.item = value;
		return this;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { ITEM };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Not();
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
		if (ITEM.equalsIgnoreCase(attribute)) {
			return ((Not) entity).getItem();
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
		if (ITEM.equalsIgnoreCase(attribute)) {
			if (value instanceof ObjectCondition) {
				((Not) entity).with((ObjectCondition) value);
			}
		}
		return false;
	}

	/**
	 * Static Method for instance a new Instance of Not Object.
	 *
	 * @param condition Condition to negate
	 * @return The new Instance
	 */
	public static Not create(ObjectCondition condition) {
		return new Not().with(condition);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		if (this.item != null) {
			return "!" + this.item.toString();
		}
		return "!";
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return TAG;
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		return true;
	}

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	/**
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		if(buffer == null) {
			return;
		}
		buffer.skip();
		buffer.skip();

		ObjectCondition expression = parser.parsing(buffer, customTemplate, true, true, "endnot");
		this.item = expression;
		buffer.skipTo(SPLITEND, false);
		buffer.skip();
		buffer.skip();
	}
}
