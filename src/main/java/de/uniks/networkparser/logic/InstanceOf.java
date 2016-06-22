package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
/**
 * InstanceOf Condition.
 *
 * @author Stefan Lindel
 */

public class InstanceOf implements UpdateListener, SendableEntityCreator {
	/** Constant of CLAZZNAME. */
	public static final String CLAZZNAME = "clazzname";
	/** Constant of PROPERTY. */
	public static final String PROPERTY = "property";
	/** Constant of VALUE. */
	public static final String VALUE = "value";
	/** Variable of ClazzName. */
	private Class<?> clazzName;

	/** Variable of Property. */
	private String property;
	/** Variable of Item. */
	private Object item;

	@Override
	public String[] getProperties() {
		return new String[] {CLAZZNAME, PROPERTY, VALUE };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InstanceOf();
	}

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

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
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
	 * @param clazzName		The ClazzName
	 * @return 				The new Instance
	 */
	public static InstanceOf value(Class<?> clazzName) {
		return new InstanceOf().withClazzName(clazzName);
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param clazz		The ClazzName
	 * @param property	The Property
	 * @return 			The new Instance
	 */
	public static InstanceOf value(Object clazz, String property) {
		InstanceOf result = new InstanceOf().withProperty(property);
		if(clazz instanceof Class<?>) {
			result.withClazzName((Class<?>) clazz);
		}else {
			result.withClazzName(clazz.getClass());
		}
		return result;
	}

	/** @return The ClazzName */
	public Class<?> getClazzName() {
		return clazzName;
	}

	/**
	 * @param value	The new ClazzName
	 * @return 		InstacneOf Instance
	 */
	public InstanceOf withClazzName(Class<?> value) {
		this.clazzName = value;
		return this;
	}

	/** @return The Property */
	public String getProperty() {
		return property;
	}

	/**
	 * @param value	The new Property
	 * @return 		InstanceOf Instance
	 */
	public InstanceOf withProperty(String value) {
		this.property = value;
		return this;
	}

	/** @return The Value of InstanceOf */
	public Object getValue() {
		return item;
	}

	/**
	 * @param value	The new Value
	 * @return 		InstanceOf Instance
	 */
	public InstanceOf withValue(Object value) {
		this.item = value;
		return this;
	}

	@Override
	public boolean update(Object evt) {
		// Filter for ClazzTyp
		if(evt == null || evt instanceof PropertyChangeEvent == false) {
			return false;
		}
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		if (this.clazzName != null ) {
			Object newValue = event.getNewValue();
			if(this.clazzName.isInstance(newValue) == false) {
				return false;
			}else if(this.property==null) {
				return true;
			}else if(this.property.equalsIgnoreCase(event.getPropertyName())) {
				return false;
			}
		}
		// Filter for one item
		return (this.item == null || this.item != event.getNewValue());
	}
}
