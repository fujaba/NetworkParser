package de.uniks.networkparser.graph;

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
import java.util.Iterator;
import java.util.Set;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

/**
 * GraphCreate Clazz for Condition.
 *
 * @author Stefan Lindel
 */

public class GraphPatternMatch implements ObjectCondition, SendableEntityCreator {
	/** Constant for ITEM. */
	public static final String ITEM = "item";
	
	/** The Constant MATCHES. */
	public static final String MATCHES = "matches";
	
	/** The Constant PROPERTY. */
	public static final String PROPERTY = "property";
	/** Varibale for Condition. */
	private Object item;
	private String property;
	private Set<ObjectCondition> matches;

	/**
	 * Update.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object evt) {
		return true;
	}

	/**
	 * Gets the item.
	 *
	 * @return The Item to Create
	 */
	public Object getItem() {
		return item;
	}

	/**
	 * With item.
	 *
	 * @param value for new Condition
	 * @return Not Instance
	 */
	public GraphPatternMatch withItem(Object value) {
		this.item = value;
		return this;
	}

	/**
	 * With.
	 *
	 * @param value for new Condition
	 * @return Not Instance
	 */
	public GraphPatternMatch with(ObjectCondition... value) {
		if (value == null) {
			return this;
		}
		for (ObjectCondition listener : value) {
			if (listener != null) {
				if (this.matches == null) {
					this.matches = new SimpleSet<ObjectCondition>();
				}
				this.matches.add(listener);
			}
		}
		return this;
	}

	/**
	 * Gets the matches.
	 *
	 * @return the matches
	 */
	public Set<ObjectCondition> getMatches() {
		return matches;
	}

	/**
	 * With property.
	 *
	 * @param value for Property
	 * @return GraphPatternMatch Instance
	 */
	public GraphPatternMatch withProperty(String value) {
		this.property = value;
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
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { ITEM, PROPERTY, MATCHES };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GraphPatternMatch();
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
			return ((GraphPatternMatch) entity).getItem();
		}
		if (MATCHES.equalsIgnoreCase(attribute)) {
			return ((GraphPatternMatch) entity).getMatches();
		}
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			return ((GraphPatternMatch) entity).getProperty();
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
			((GraphPatternMatch) entity).withItem(value);
			return true;
		}
		if (MATCHES.equalsIgnoreCase(attribute)) {
			if (value instanceof ObjectCondition) {
				((GraphPatternMatch) entity).with((ObjectCondition) value);
			} else {
				((GraphPatternMatch) entity).with((ObjectCondition[]) value);
			}
			return true;
		}
		if (PROPERTY.equalsIgnoreCase(attribute)) {
			((GraphPatternMatch) entity).withProperty("" + value);
			return true;
		}
		return false;
	}

	/**
	 * Creates the.
	 *
	 * @param property the property
	 * @param item the item
	 * @return the graph pattern match
	 */
	public static GraphPatternMatch create(String property, Object item) {
		return new GraphPatternMatch().withItem(item);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		if (this.matches == null) {
			return 0;
		}
		int size = 0;
		for (Iterator<ObjectCondition> i = this.matches.iterator(); i.hasNext();) {
			ObjectCondition child = i.next();
			if (child instanceof GraphPatternMatch) {
				size += ((GraphPatternMatch) child).size();
			} else {
				size++;
			}
		}
		return size;
	}
}
