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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.Iterator;

import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleMap;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.list.SortingDirection;

/**
 * The Class TableList.
 *
 * @author Stefan
 */
public class TableList extends SortedList<Object> implements SendableEntity, SendableEntityCreator {
	
	/** The Constant PROPERTY_ITEMS. */
	public static final String PROPERTY_ITEMS = "items";
	
	/** The Constant properties. */
	public static final String[] properties = new String[] { PROPERTY_ITEMS };
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/**
	 * Instantiates a new table list.
	 */
	public TableList() {
		super(false);
	}

	/**
	 * Instantiates a new table list.
	 *
	 * @param comparator the comparator
	 */
	public TableList(boolean comparator) {
		super(comparator);
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototype the prototype
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototype) {
		return new TableList();
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
		if (TableList.PROPERTY_ITEMS.equalsIgnoreCase(attribute)) {
			return entity;
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
		if (SendableEntityCreator.REMOVE.equalsIgnoreCase(type)) {
			attribute += SendableEntityCreator.REMOVE;
		}
		return ((TableList) entity).setValue(attribute, value);
	}

	/**
	 * Sets the id map.
	 *
	 * @param map the map
	 * @return true, if successful
	 */
	public boolean setIdMap(IdMap map) {
		if (isComparator() == false) {
			return false;
		}
		if (comparator() instanceof EntityComparator<?>) {
			((EntityComparator<?>) comparator()).withMap(map);
		}
		return false;
	}

	/**
	 * Sets the value.
	 *
	 * @param attrName the attr name
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String attrName, Object value) {
		if (PROPERTY_ITEMS.equalsIgnoreCase(attrName)) {
			add(value);
			return true;
		} else if ((PROPERTY_ITEMS + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName)) {
			remove(value);
			return true;
		}
		return false;
	}

	@Override
	protected boolean fireProperty(String type, Object oldValue, Object newValue, Object beforeValue, int index,
			Object value) {
		boolean result = super.fireProperty(type, oldValue, newValue, beforeValue, index, value);

		getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, oldValue, newValue);

		return result;
	}

	/**
	 * Gets the property change support.
	 *
	 * @return the property change support
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}

	/**
	 * Removes the all from items.
	 *
	 * @return true, if successful
	 */
	public boolean removeAllFromItems() {
		Object[] array = toArray(new Object[size()]);
		for (Object item : array) {
			if (remove(item) == false) {
				return false;
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, item, null);
		}
		return true;
	}

	/**
	 * With sort.
	 *
	 * @param field the field
	 * @param direction the direction
	 * @param cellValueCreator the cell value creator
	 * @return the table list
	 */
	public TableList withSort(String field, SortingDirection direction, EntityValueFactory cellValueCreator) {

		EntityComparator<Object> cpr;
		if (isComparator() && comparator() instanceof EntityComparator<?>) {
			cpr = (EntityComparator<Object>) comparator();
		} else {
			cpr = new EntityComparator<Object>();
			withComparator(cpr);
		}
		cpr.withColumn(field);
		cpr.withDirection(direction);
		cpr.withCellCreator(cellValueCreator);
		return this;
	}

	/**
	 * With comparator.
	 *
	 * @param comparator the comparator
	 * @return the table list
	 */
	@Override
	public TableList withComparator(Comparator<Object> comparator) {
		super.withComparator(comparator);
		return this;
	}

	/**
	 * With sort.
	 *
	 * @param field the field
	 * @param direction the direction
	 * @return the table list
	 */
	public TableList withSort(String field, SortingDirection direction) {
		EntityComparator<Object> cpr;
		if (isComparator() && comparator() instanceof EntityComparator<?>) {
			cpr = (EntityComparator<Object>) comparator();
		} else {
			cpr = new EntityComparator<Object>();
			withComparator(cpr);
		}
		cpr.withColumn(field);
		cpr.withDirection(direction);
		return this;
	}

	/**
	 * Change direction.
	 *
	 * @return the sorting direction
	 */
	public SortingDirection changeDirection() {
		if (isComparator() && comparator() instanceof EntityComparator) {
			return ((EntityComparator<?>) comparator()).changeDirection();

		}
		return null;
	}

	/**
	 * Gets the sorted index.
	 *
	 * @return the sorted index
	 */
	public Object[] getSortedIndex() {
		if (comparator() instanceof EntityComparator<?> == false) {
			return null;
		}
		EntityComparator<Object> comparator = (EntityComparator<Object>) comparator();
		if (comparator == null) {
			return null;
		}
		SimpleMap map = comparator.getMap();
		Iterator<Object> iterator = iterator();
		SendableEntityCreator creator = null;
		if (iterator.hasNext()) {
			creator = map.getCreatorClass(iterator.next());
		}
		String column = comparator.getColumn();
		if (creator != null && column != null) {
			Object[] returnValues = new Object[super.size()];
			EntityValueFactory cellCreator = comparator.getCellCreator();
			if (comparator.getDirection() == SortingDirection.ASC) {
				int pos = 0;
				for (Iterator<Object> i = iterator(); i.hasNext();) {
					Object item = i.next();
					returnValues[pos++] = cellCreator.getCellValue(item, creator, column);
				}
			} else {
				int pos = super.size() - 1;
				for (Iterator<Object> i = iterator(); i.hasNext();) {
					Object item = i.next();
					returnValues[pos--] = cellCreator.getCellValue(item, creator, column);
				}
			}
			return returnValues;
		}

		return null;
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param name the name
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean removePropertyChangeListener(String name, PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(name, listener);
		return true;
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param name the name
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean addPropertyChangeListener(String name, PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(name, listener);
		return true;

	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "TableList with " + size() + " Elements";
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public TableList getNewList(boolean keyValue) {
		return new TableList();
	}
}
