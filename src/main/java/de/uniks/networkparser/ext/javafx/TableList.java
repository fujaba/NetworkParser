package de.uniks.networkparser.ext.javafx;

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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.Iterator;
import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.list.SortingDirection;
public class TableList extends SortedList<Object> implements

		SendableEntity, SendableEntityCreator {
	public static final String PROPERTY_ITEMS = "items";
	public static final String[] properties = new String[] {PROPERTY_ITEMS };
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new TableList();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (TableList.PROPERTY_ITEMS.equalsIgnoreCase(attribute)) {
			return entity;
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (IdMap.REMOVE.equalsIgnoreCase(type)) {
			attribute += IdMap.REMOVE;
		}
		return ((TableList) entity).setValue(attribute, value);
	}

	public boolean setIdMap(IdMap map) {
		if(!isComparator() ) {
			return false;
		}
		if (comparator() instanceof EntityComparator<?>) {
			((EntityComparator<?>) comparator()).withMap(map);
		}
		return false;
	}

	public boolean setValue(String attrName, Object value) {
		if (PROPERTY_ITEMS.equalsIgnoreCase(attrName)) {
			add(value);
			return true;
		} else if ((PROPERTY_ITEMS + IdMap.REMOVE)
				.equalsIgnoreCase(attrName)) {
			remove(value);
			return true;
		}
		return false;
	}

	@Override
	protected boolean fireProperty(String type, Object oldValue, Object newValue,
			Object beforeValue, Object value) {
		boolean result = super.fireProperty(type, oldValue, newValue, beforeValue, value);

		getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, oldValue,
				newValue);

		return result;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}

	public boolean removeAllFromItems() {
		Object[] array = toArray(new Object[size()]);
		for (Object item : array) {
			if (!remove(item)) {
				return false;
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, item,
					null);
		}
		return true;
	}

	public TableList withSort(String field, SortingDirection direction,
			EntityValueFactory cellValueCreator) {

		EntityComparator<Object> cpr;
		if (isComparator() && comparator() instanceof EntityComparator<?>) {
			cpr = (EntityComparator<Object>) comparator();
		}else{
			cpr = new EntityComparator<Object>();
			withComparator(cpr);
		}
		cpr.withColumn(field);
		cpr.withDirection(direction);
		cpr.withCellCreator(cellValueCreator);
		return this;
	}

	@Override
	public TableList withComparator(Comparator<Object> comparator) {
		super.withComparator(comparator);
		return this;
	}

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

	public SortingDirection changeDirection() {
		if(isComparator() && comparator() instanceof EntityComparator) {
			return ((EntityComparator<?>)comparator()).changeDirection();

		}
		return null;
	}

	public Object[] getSortedIndex() {
		if(!(comparator() instanceof EntityComparator<?>)){
			return null;
		}
		EntityComparator<Object> comparator = (EntityComparator<Object>) comparator();
		if (comparator == null) {
			return null;
		}
		IdMap map = comparator.getMap();
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
					returnValues[pos++] = cellCreator.getCellValue(item,
							creator, column);
				}
			} else {
				int pos = super.size() - 1;
				for (Iterator<Object> i = iterator(); i.hasNext();) {
					Object item = i.next();
					returnValues[pos--] = cellCreator.getCellValue(item,
							creator, column);
				}
			}
			return returnValues;
		}

		return null;
	}

	// ==========================================================================
	@Override
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
		return true;
	}

	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String name,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(name, listener);
		return true;
	}

	@Override
	public boolean addPropertyChangeListener(String name,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(name, listener);
		return true;

	}

	@Override
	public String toString() {
		return "TableList with " + size() + " Elements";
	}

	@Override
	public TableList getNewList(boolean keyValue) {
		return new TableList();
	}
}
