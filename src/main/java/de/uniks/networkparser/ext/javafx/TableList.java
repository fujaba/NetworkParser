package de.uniks.networkparser.ext.javafx;

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
