package de.uniks.networkparser.logic;

/*
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
import java.util.Collection;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class WhiteListCondition.
 *
 * @author Stefan
 */
public class WhiteListCondition implements ObjectCondition, SendableEntityCreator {
	private SimpleKeyValueList<String, SimpleList<String>> whiteList = new SimpleKeyValueList<String, SimpleList<String>>();
	private boolean primitive = true;

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return null;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new WhiteListCondition();
	}

	/**
	 * With primititve.
	 *
	 * @param value the value
	 * @return the white list condition
	 */
	public WhiteListCondition withPrimititve(boolean value) {
		this.primitive = value;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		Object newValue = event.getNewValue();
		if (newValue == null) {
			return false;
		}
		String className = newValue.getClass().getSimpleName();
		String propertyName = event.getPropertyName();
		SimpleList<String> simpleList = whiteList.get(className);
		IdMap map = (IdMap) event.getSource();
		SendableEntityCreator creator = map.getCreatorClass(newValue);
		if (newValue instanceof Collection<?>) {
			return true;
		}
		if (creator != null) {
			if (simpleList != null) {
				return simpleList.size() == 0 || simpleList.indexOf(propertyName) >= 0;
			}
			return false;
		}
		return this.primitive;
	}

	/**
	 * With.
	 *
	 * @param className the class name
	 * @param attributes the attributes
	 * @return the white list condition
	 */
	public WhiteListCondition with(Class<?> className, String... attributes) {
		if (className != null) {
			with(className.getSimpleName(), attributes);
		}
		return this;
	}

	/**
	 * With.
	 *
	 * @param className the class name
	 * @param attributes the attributes
	 * @return the white list condition
	 */
	public WhiteListCondition with(String className, String... attributes) {
		SimpleList<String> simpleList = whiteList.get(className);
		if (simpleList == null) {
			simpleList = new SimpleList<String>();
			whiteList.put(className, simpleList);
		}
		if (attributes == null) {
			return this;
		}
		for (String item : attributes) {
			simpleList.add(item);
		}
		return this;
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
		return false;
	}

}
