package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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

/**
 * InstanceOf Condition.
 *
 * @author Stefan Lindel
 */
public class InstanceOf extends ConditionMap implements SendableEntityCreator {
	/** Constant of CLAZZNAME. */
	public static final String CLAZZNAME = "clazzname";
	/** Constant of PROPERTY. */
	public static final String PROPERTY = "property";
	/** Constant of CLAZZ. */
	public static final String CLAZZ = "clazz";
	/** Constant of VALUE. */
	public static final String VALUE = "value";
	/** Variable of ClazzName. */
	private Class<?> clazzName;
	/** Variable of Clazz. */
	private Object clazz;
	/** Variable of Property. */
	private String property;
	/** Variable of Item. */
	private Object item;

	@Override
	public String[] getProperties() {
		return new String[] {CLAZZNAME, CLAZZ, PROPERTY, VALUE };
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
		if (CLAZZ.equalsIgnoreCase(attribute)) {
			return ((InstanceOf) entity).getClazz();
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
		if (CLAZZ.equalsIgnoreCase(attribute)) {
			((InstanceOf) entity).withClazz(value);
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
	 * @param clazzName
	 *            The ClazzName
	 * @param property
	 *            The PropertyName
	 * @param element
	 *            The Element for search
	 * @return The new Instance
	 */
	public static InstanceOf value(Class<?> clazzName, String property,
			Object element) {
		return new InstanceOf().withClazzName(clazzName).withProperty(property)
				.withValue(element);
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param clazzName
	 *            The ClazzName
	 * @param property
	 *            The PropertyName
	 * @return The new Instance
	 */
	public static InstanceOf value(Class<?> clazzName, String property) {
		return new InstanceOf().withClazzName(clazzName).withProperty(property);
	}

	/**
	 * Static Method for instance a new Instance of InstanceOf Object.
	 *
	 * @param clazz
	 *            The ClazzName
	 * @param property
	 *            The Property
	 * @return The new Instance
	 */
	public static InstanceOf value(Object clazz, String property) {
		return new InstanceOf().withClazz(clazz).withProperty(property);
	}

	/** @return The ClazzName */
	public Class<?> getClazzName() {
		return clazzName;
	}

	/**
	 * @param value
	 *            The new ClazzName
	 * @return INstacneOf Instance
	 */
	public InstanceOf withClazzName(Class<?> value) {
		this.clazzName = value;
		return this;
	}

	/** @return The Clazz Property */
	public Object getClazz() {
		return clazz;
	}

	/**
	 * @param value
	 *            The new Clazz
	 * @return InstanceOf Instance
	 */
	public InstanceOf withClazz(Object value) {
		this.clazz = value;
		return this;
	}

	/** @return The Property */
	public String getProperty() {
		return property;
	}

	/**
	 * @param value
	 *            The new Property
	 * @return InstanceOf Instance
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
	 * @param value
	 *            The new Value
	 * @return InstanceOf Instance
	 */
	public InstanceOf withValue(Object value) {
		this.item = value;
		return this;
	}

	@Override
	public boolean check(ValuesMap values) {
		if (this.clazzName != null
				&& values.entity.getClass() != this.clazzName) {
			return true;
		}
		if (this.clazz != null && values.entity != this.clazz) {
			return true;
		}
		if (!this.property.equalsIgnoreCase(values.property)) {
			return true;
		}
		return (this.item != null && this.item == values.value);
	}
}
