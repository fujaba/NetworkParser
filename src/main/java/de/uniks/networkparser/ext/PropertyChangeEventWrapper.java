package de.uniks.networkparser.ext;

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
import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;

/**
 * Property Change Event.
 *
 * @author Stefan Lindel
 */
public class PropertyChangeEventWrapper extends SendableEntityCreatorWrapper {
	
	/** The Constant PROPERTY_SOURCE. */
	public static final String PROPERTY_SOURCE = "source";
	
	/** The Constant PROPERTY_PROPERTY. */
	public static final String PROPERTY_PROPERTY = "property";
	
	/** The Constant PROPERTY_OLDVALUE. */
	public static final String PROPERTY_OLDVALUE = "oldValue";
	
	/** The Constant PROPERTY_NEWVALUE. */
	public static final String PROPERTY_NEWVALUE = "newValue";
	
	/** The Constant SENDABLECLASSSTRING. */
	public static final String SENDABLECLASSSTRING = "java.beans.PropertyChangeEvent";

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_SOURCE, PROPERTY_PROPERTY, PROPERTY_OLDVALUE, PROPERTY_NEWVALUE };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PropertyChangeEvent(this, null, null, null);
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
		if (entity instanceof PropertyChangeEvent) {
			if (PROPERTY_SOURCE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent) entity).getSource();
			}
			if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent) entity).getPropertyName();
			}
			if (PROPERTY_OLDVALUE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent) entity).getOldValue();
			}
			if (PROPERTY_NEWVALUE.equalsIgnoreCase(attribute)) {
				return ((PropertyChangeEvent) entity).getNewValue();
			}
		}
		return null;
	}

	/**
	 * New instance.
	 *
	 * @param item the item
	 * @return the object
	 */
	@Override
	public Object newInstance(Entity item) {
		Object source = item.getValue(PROPERTY_SOURCE);
		String property = "" + item.getValue(PROPERTY_PROPERTY);
		Object oldValue = item.getValue(PROPERTY_OLDVALUE);
		Object newValue = item.getValue(PROPERTY_NEWVALUE);
		return new PropertyChangeEvent(source, property, oldValue, newValue);
	}
}
