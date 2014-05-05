package de.uniks.networkparser.gui.table;

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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uniks.networkparser.ArrayEntryList;
import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class TableList extends ArrayEntryList implements SendableEntity {
	public static final String PROPERTY_ITEMS = "items";
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	@Override
	public EntityComparator<MapEntry> comparator() {
		return (EntityComparator<MapEntry>) cpr;
	}
	
	public ArrayList<Object> getItems(){
		return (ArrayList<Object>) super.values();
	}
	
	public void setIdMap(IdMapEncoder map){
		if(cpr instanceof EntityComparator<?>){
			((EntityComparator<?>)this.cpr).withMap(map);
		}
	}
	
    public boolean setValue(String attrName, Object value) {
        if (PROPERTY_ITEMS.equalsIgnoreCase(attrName)) {
            add(value);
            return true;
        }else if ((PROPERTY_ITEMS+IdMapEncoder.REMOVE).equalsIgnoreCase(attrName)) {
            remove(value);
            return true;
        }
        return false;
    }

	
	public boolean addItem(Object value){
		if (!super.add(value)) 
		{
			getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, null, value);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove(Object value) {
		if (contains(value)) {
			List<MapEntry> items = this.values;
			if(items.remove(value)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public MapEntry remove(int index) {
		MapEntry item = super.remove(index);
		getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, item,null);
		return item;
	}
	
	@Override
	public void clear() {
		removeAll(iterator());
	}
	
	@Override
	public boolean removeAll(Collection<?> list) {
		return removeAll(list.iterator());
	}
	
	public boolean removeAll(Iterator<?> i) {
		while(i.hasNext()){
			Object item = i.next();
			if(item!=null){
				i.remove();
				getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, item,null);
			}
		}
		return true;
	}
	
	public boolean addAll(TableList list){
		for(Object item : this){
			if(!add(item)){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends MapEntry> c) {
		for(Iterator<? extends Object> iterator = super.iterator();iterator.hasNext();){
			if(!add(iterator.next())){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int lastIndexOf(Object obj) {
		return indexOf(obj);
	}

	@Override
	public ListIterator<MapEntry> listIterator() {
		return new ListIteratorImpl<MapEntry>(this);
	}

	@Override
	public ListIterator<MapEntry> listIterator(int index) {
		ListIterator<MapEntry> iterator = listIterator();
		if(index>=0&&index<=size()){
			for(int z=0;z<index;z++){
				iterator.next();
			}
		}
		return iterator;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}
	
	public boolean removeAllFromItems() {
		Object[] array = toArray(new Object[size()]);
		for(Object item : array){
			if(!remove(item)){
				return false;
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_ITEMS, item, null);
		}
		return true;
	}
	
	public TableList withSort(String field, SortingDirection direction, EntityValueFactory cellValueCreator) {
		EntityComparator<MapEntry> comparator = comparator();
		comparator.withColumn(field);
		comparator.withDirection(direction);
		comparator.withCellCreator(cellValueCreator);
		refreshSort();
		return this;
	}
	
	@Override
	public TableList withComparator(Comparator<MapEntry> comparator) {
		super.withComparator(comparator);
		refreshSort();
		return this;
	
	}
	
	public TableList withSort(String field, SortingDirection direction) {
		EntityComparator<MapEntry> comparator = comparator();
		comparator.withColumn(field);
		comparator.withDirection(direction);
		refreshSort();
		return this;
	}

	public void refreshSort(){
//		ArrayList<Object> oldValue = list;
//		
//		list = getItems(true);
//		int size = oldValue.size();
//		Object[] array = oldValue.toArray(new Object[size]);
//		for(int i=0;i<size;i++){
//			list.add(array[i]);
//		}
	}

	public void setSort(String field) {
		EntityComparator<MapEntry> comparator = comparator();
		if(comparator!=null){
			comparator.withColumn(field);
			refreshSort();
		}
	}
	
	public SortingDirection changeDirection(){
		return comparator().changeDirection();
	}
	
	public Object[] getSortedIndex(){
		EntityComparator<MapEntry> comparator = comparator();
		if(comparator==null){
			return null;
		}
		IdMapEncoder map = comparator.getMap();
		Iterator<MapEntry> iterator = iterator();
		SendableEntityCreator creator = null; 
		if(iterator.hasNext()){
			creator = map.getCreatorClass(iterator.next());
		}
		String column = comparator.getColumn();
		if(creator != null &&  column != null){
			Object[] returnValues=new Object[super.size()];
			EntityValueFactory cellCreator = comparator.getCellCreator();
			if(comparator.getDirection()==SortingDirection.ASC){
				int pos=0;
				for(Iterator<MapEntry> i = iterator();i.hasNext();){
					Object item = i.next();
					returnValues[pos++] = cellCreator.getCellValue(item, creator, column);
				}
			}else{
				int pos=super.size()-1;
				for(Iterator<MapEntry> i = iterator();i.hasNext();){
					Object item = i.next();
					returnValues[pos--] = cellCreator.getCellValue(item, creator, column);
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
}
