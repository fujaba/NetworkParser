package de.uniks.networkparser.graph;

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
 * GraphCreate Clazz for Condition.
 *
 * @author Stefan Lindel
 */
public class GraphPatternChange implements UpdateListener, SendableEntityCreator {
	/** Constant for ITEM. */
	public static final String OLD = "oldValue";
	public static final String NEW = "newValue";
	public static final String PROPERTY = "property";
	/** Varibale for Condition. */
	private String property;
	private Object oldValue;
	private Object newValue;

	@Override
	public boolean update(Object evt) {
		return true;
	}

	/** @return The OldValue */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * @param value		for The OldValue
	 * @return 			GraphPatternChange Instance
	 */
	public GraphPatternChange withOldValue(Object value) {
		this.oldValue = value;
		return this;
	}

	/** @return The OldValue */
	public Object getNewValue() {
		return newValue;
	}
	
	/**
	 * @param value		for Property
	 * @return 			GraphPatternChange Instance
	 */
	public GraphPatternChange withProperty(String value) {
		this.property = value;
		return this;
	}

	/** @return The Property */
	public String getProperty() {
		return property;
	}

	/**
	 * @param value		for new Condition
	 * @return 			GraphPatternChange Instance
	 */
	public GraphPatternChange withNewValue(Object value) {
		this.newValue = value;
		return this;
	}
	
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY, OLD, NEW};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GraphPatternChange();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			return ((GraphPatternChange) entity).getProperty();
		}
		if (OLD.equalsIgnoreCase(attribute)) {
			return ((GraphPatternChange) entity).getOldValue();
		}
		if (NEW.equalsIgnoreCase(attribute)) {
			return ((GraphPatternChange) entity).getNewValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			((GraphPatternChange) entity).withProperty(""+value);
			return true;
		}
		if (OLD.equalsIgnoreCase(attribute)) {
			((GraphPatternChange) entity).withOldValue(value);
			return true;
		}
		if (NEW.equalsIgnoreCase(attribute)) {
			((GraphPatternChange) entity).withNewValue(value);
			return true;
		}
		return false;
	}

	public static GraphPatternChange createCreate(Object newValue) {
		return new GraphPatternChange().withNewValue(newValue);
	}
	public static GraphPatternChange createCreate(String property, Object newValue) {
		return new GraphPatternChange().withProperty(property).withNewValue(newValue);
	}

	public static GraphPatternChange createChange(Object oldValue,Object newValue) {
		return new GraphPatternChange().withOldValue(oldValue).withNewValue(newValue);
	}
	public static GraphPatternChange createChange(String property, Object oldValue,Object newValue) {
		return new GraphPatternChange().withProperty(property).withOldValue(oldValue).withNewValue(newValue);
	}

	public static GraphPatternChange createDelete(Object oldValue) {
		return new GraphPatternChange().withOldValue(oldValue);
	}
	public static GraphPatternChange createDelete(String property, Object oldValue) {
		return new GraphPatternChange().withProperty(property).withOldValue(oldValue);
	}
}
