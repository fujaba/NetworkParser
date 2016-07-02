package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
