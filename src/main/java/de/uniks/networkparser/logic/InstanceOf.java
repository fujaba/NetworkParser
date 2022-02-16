package de.uniks.networkparser.logic;

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

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * InstanceOf Condition.
 *
 * @author Stefan Lindel
 */

public class InstanceOf implements ObjectCondition, SendableEntityCreator {
	/** Constant of CLAZZNAME. */
	public static final String CLAZZNAME = "clazzname";
	/** Constant of PROPERTY. */
	public static final String PROPERTY = "property";
	/** Constant of VALUE. */
	public static final String VALUE = "value";

	/** Variable of ClazzName. */
	private Object clazzName;

	/** Variable of WhiteList of ClassNames. */
	private boolean whiteList;

	/** Variable of Property. */
	private String property;
	/** Variable of Item. */
	private Object item;

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { CLAZZNAME, PROPERTY, VALUE };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InstanceOf();
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
		if (CLAZZNAME.equalsIgnoreCase(attribute)) {
			return ((InstanceOf) entity).getClazzName();
		}
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			return ((InstanceOf) entity).getProperty();
		}
		if (VALUE.equalsIgnoreCase(attribute)) {
			return ((InstanceOf) entity).getValue();
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
		if (CLAZZNAME.equalsIgnoreCase(attribute)) {
			((InstanceOf) entity).withClazzName((Class<?>) value);
			return true;
		}
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			((InstanceOf) entity).withProperty("" + value);
			return true;
		}
		if (VALUE.equalsIgnoreCase(attribute)) {
			((InstanceOf) entity).withValue(value);
			return true;
		}
		return false;
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param clazzName The ClazzName
	 * @return The new Instance
	 */
	public static InstanceOf create(Class<?> clazzName) {
		return new InstanceOf().withClazzName(clazzName);
	}
	
	/**
	 * Creates the.
	 *
	 * @param clazzName the clazz name
	 * @return the instance of
	 */
	public static InstanceOf create(Object clazzName) {
		InstanceOf result = new InstanceOf();
		if(clazzName instanceof Class<?>) {
			return result.withClazzName((Class<?>)clazzName);
		}
		if(clazzName instanceof Clazz) {
			return result.withClazzName(((Clazz) clazzName).getName(true));
		}
		if(clazzName != null) {
			return result.withClazzName(clazzName.getClass());
		}
		return result;
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param clazz    The ClazzName
	 * @param property The Property
	 * @return The new Instance
	 */
	public static InstanceOf create(Object clazz, String property) {
		InstanceOf result = new InstanceOf().withProperty(property);
		if (clazz instanceof Class<?>) {
			result.withClazzName((Class<?>) clazz);
		} else if(clazz != null){
			result.withClazzName(clazz.getClass());
		}
		return result;
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param property The Property
	 * @return The new Instance
	 */
	public static InstanceOf create(String property) {
		return new InstanceOf().withProperty(property);
	}

	/**
	 * Gets the clazz name.
	 *
	 * @return The ClazzName
	 */
	public Class<?> getClazzName() {
		if(clazzName instanceof Class<?>) {
			return (Class<?>) clazzName;
		}
		return null;
	}

	/**
	 * With clazz name.
	 *
	 * @param value The new ClazzName
	 * @return InstacneOf Instance
	 */
	public InstanceOf withClazzName(Object value) {
		this.clazzName = value;
		return this;
	}

	/**
	 * Gets the property.
	 *
	 * @return The Property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * With property.
	 *
	 * @param value The new Property
	 * @return InstanceOf Instance
	 */
	public InstanceOf withProperty(String value) {
		this.property = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return The Value of InstanceOf
	 */
	public Object getValue() {
		return item;
	}

	/**
	 * With value.
	 *
	 * @param value The new Value
	 * @return InstanceOf Instance
	 */
	public InstanceOf withValue(Object value) {
		this.item = value;
		return this;
	}

	private boolean checkClazz(Object value) {
		if(this.clazzName == null || value == null) {
			return false;
		}
		Class<?> clazzElement = getClazzName();
		if(clazzElement != null ) {
			return clazzElement.isInstance(value);
		}
		if(this.clazzName instanceof String) {
			return this.clazzName.equals(value.getClass().getName());
		}
		return false;
	}
	
	/**
	 * Update.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object evt) {
		/* Filter for ClazzTyp */
		if (evt == null || !(evt instanceof PropertyChangeEvent)) {
			return false;
		}
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		if (this.clazzName != null) {
			Object newValue = event.getNewValue();
			if(!checkClazz(newValue)) {
				/* Check for whiteList */
				if (evt instanceof SimpleEvent) {
					SimpleEvent se = (SimpleEvent) evt;
					IdMap map = (IdMap) se.getSource();
					String className = newValue.getClass().getName();
					if (map.getCreator(className, false, true, null) != null) {
						return false;
					}
				}
				/* Turn around if WhiteList */
				return whiteList;
			} else if (this.property == null) {
				return true;
			} else if (this.property.equalsIgnoreCase(event.getPropertyName())) {
				return false;
			}
		} else if (this.property != null) {
			return !this.property.equalsIgnoreCase(event.getPropertyName());
		}
		/* Filter for one item */
		return (this.item == null || this.item != event.getNewValue());
	}

	/**
	 * With white list.
	 *
	 * @param whiteList the white list
	 * @return the instance of
	 */
	public InstanceOf withWhiteList(boolean whiteList) {
		this.whiteList = whiteList;
		return this;
	}
 	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		if(clazzName != null) {
			if(clazzName instanceof Clazz) {
				return this.getClass().getSimpleName()+" "+clazzName;
			}
			return this.getClass().getSimpleName()+" "+clazzName;
		}
		return super.toString();
	}
}
