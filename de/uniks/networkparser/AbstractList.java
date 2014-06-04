package de.uniks.networkparser;	

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;
/**
 * The Class EntityList.
 */
public abstract class AbstractList<V> implements BaseItem {
    protected List<V> values = new ArrayList<V>();
	private boolean allowDuplicate = initAllowDuplicate();
	protected Comparator<V> cpr;
	
	protected boolean initAllowDuplicate(){
		return true;
	}
	
	public Comparator<V> comparator() {
		if(this.cpr==null){
			withComparator(new EntityComparator<V>().withColumn(EntityComparator.LIST).withDirection(SortingDirection.ASC));
		}
		return cpr;
	}
	
	public boolean isComparator(){
		return (this.cpr!=null);
	}
	
	public AbstractList<V> withComparator(Comparator<V> comparator){
		this.cpr = comparator;
		return this;
	}

	public AbstractList<V> withComparator(String column){
		this.cpr = new EntityComparator<V>().withColumn(column).withDirection(SortingDirection.ASC);
		return this;
	}

	public boolean add(V newValue) {
		if(cpr!=null){
			for (int i = 0; i < size(); i++) {
				int result = compare(get(i), newValue);
				if (result >= 0) {
					this.values.add(i, newValue);
					V beforeElement = null;
					if(i>0){
						beforeElement = this.values.get(i-1);
					}
					fireProperty(null, newValue, beforeElement);
					return true;
				}
			}
		}
		boolean result = this.values.add(newValue);
		if(result){
			V beforeElement = null;
			if(size() > 1){
				beforeElement = this.values.get(size() - 1);
			}
			fireProperty(null, newValue, beforeElement);
		}
		return result;
	}
	
	/**
	 * Get the object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return An object value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public V get(int index) throws RuntimeException {
		V object = this.values.get(index);
		if (object == null) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		return object;
	}
	
	
	protected void fireProperty(Object oldValue, Object newValue, Object beforeValue){
		
	}
	
	public int compare(V o1, V o2){
		return comparator().compare(o1, o2);
	}
	
	
	public abstract AbstractList<V> getNewInstance();

	
	public AbstractList<V> subSet(V fromElement, V toElement) {
		AbstractList<V> newList = getNewInstance();
		
		// PRE WHILE
		Iterator<V> iterator = iterator();
		while(iterator.hasNext()){
			V item = iterator.next();
			if(compare(item, fromElement)>=0){
				newList.add(item);
				break;
			}
		}
		
		// MUST COPY
		while(iterator.hasNext()){
			V item = iterator.next();
			if(compare(item, toElement)>=0){
				break;
			}
			newList.add(item);
		}
		return newList;
	}
	
	public List<V> subList(int fromIndex, int toIndex) {
		return this.values.subList(fromIndex, toIndex);
	}

    /**
     * Returns a view of the portion of this map whose keys are less than (or
     * equal to, if {@code inclusive} is true) {@code toKey}.  The returned
     * map is backed by this map, so changes in the returned map are reflected
     * in this map, and vice-versa.  The returned map supports all optional
     * map operations that this map supports.
     *
     * <p>The returned map will throw an {@code IllegalArgumentException}
     * on an attempt to insert a key outside its range.
     *
     * @param toKey high endpoint of the keys in the returned map
     * @param inclusive {@code true} if the high endpoint
     *        is to be included in the returned view
	*/
	public AbstractList<V> headSet(V toElement, boolean inclusive) {
		Iterator<V> iterator = iterator();
		AbstractList<V> newList = getNewInstance();

		// MUST COPY
		while(iterator.hasNext()){
			V item = iterator.next();
			int compare = compare(item, toElement);
			if(compare==0){
				if(inclusive){
					newList.add(item);
				}
				break;
			}else if(compare>0){
				newList.add(item);
				break;
			}
		}
		return newList;
	}

	/**
     * Returns a view of the portion of this map whose keys are greater than (or
     * equal to, if {@code inclusive} is true) {@code fromKey}.
     *
     * @param fromKey low endpoint of the keys in the returned map
     * @param inclusive {@code true} if the low endpoint
     *        is to be included in the returned view
     * @return a view of the portion of this map whose keys are greater than
     *         (or equal to, if {@code inclusive} is true) {@code fromKey}
     *         
     */
	public AbstractList<V> tailSet(V fromElement, boolean inclusive) {
		Iterator<V> iterator = iterator();
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		while(iterator.hasNext()){
			V item = iterator.next();
			int compare = compare(item, fromElement);
			if(compare==0){
				if(inclusive){
					newList.add(item);
				}
				break;
			}else if(compare>0){
				newList.add(item);
				break;
			}
		}
	
		// MUST COPY
		while(iterator.hasNext()){
			V item = iterator.next();
			newList.add(item);
		}
		return newList;
	}

	/**
	 * @return the First Element of the List
	 */
	public V first() {
		return this.values.get(0);
	}

	/**
	 * @return the Last Element of the List
	 */
	public Object last() {
		return this.getEntity(this.size()-1);
	}
	
	/**
	 * @param index of value
	 * @return the entity
	 */
	public Object getEntity(int index){
		return this.values.get(index);
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The truth.
	 * @throws RuntimeException
	 *             If there is no value for the index or if the value is not
	 *             convertible to boolean.
	 */
	public boolean getBoolean(int index) throws RuntimeException {
		Object object = getEntity(index);
		if (object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (object.equals(Boolean.TRUE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new RuntimeException("EntityList[" + index
				+ "] is not a boolean.");
	}

	/**
	 * Get the double value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public double getDouble(int index) throws RuntimeException {
		Object object = getEntity(index);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	/**
	 * Get the int value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value is not a number.
	 */
	public int getInt(int index) throws RuntimeException {
		Object object = getEntity(index);
		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	/**
	 * Get the long value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
	 */
	public long getLong(int index) throws RuntimeException {
		Object object = getEntity(index);
		try {
			return object instanceof Number ? ((Number) object).longValue()
					: Long.parseLong((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(int index) throws RuntimeException {
		return getEntity(index).toString();
	}

	/**
	 * Put or replace an object value in the EntityList. If the index is greater
	 * than the length of the EntityList, then null elements will be added as
	 * necessary to pad it out.
	 * 
	 * @param index
	 *            The subscript.
	 * @param value
	 *            The value to put into the array. The value should be a
	 *            Boolean, Double, Integer, EntityList, Entity, Long, or String
	 *            object.
	 * @return this.
	 * @throws RuntimeException
	 *             If the index is negative or if the the value is an invalid
	 *             number.
	 */
	public AbstractList<V> put(int index, V value) throws RuntimeException {
		if (index < 0) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		if (index < size()) {
			
			V oldValue = null;
			if(index>0){
				oldValue = this.values.get(index - 1);
			}
			this.values.set(index, value);
			fireProperty(oldValue, value, null);
		} else {
			add(value);
		}
		return this;
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	/**
	 * Set the Flag for Duplicate Entities
	 * @param allowDuplicate
	 * @return the List
	 */
	public AbstractList<V> withAllowDuplicate(boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
		return this;
	}
	
	/**
	 * Remove an index and close the hole.
	 * 
	 * @param index
	 *            The index of the element to be removed.
	 * @return The value that was associated with the index, or null if there
	 *         was no value.
	 */
	public V remove(int index) {
		V oldValue = get(index);
		V beforeValue = null;
		if(index>0){
			beforeValue = get(index - 1);
		}
		this.values.remove(index);
		fireProperty(oldValue, null, beforeValue);
		return oldValue;
	}

    /**
     * Locate the Entity in the List
     * @param value Entity
     * @return the position of the Entity or -1
     */
    public int getIndex(Object value){
    	int pos=0;
    	for(Iterator<V> i = iterator();i.hasNext();){
    		if(i.next().equals(value)){
    			return pos;
    		}
    		pos++;
    	}
    	return -1;
    }
        
    public AbstractList<V> withList(List<V> reference){
        this.values = reference;
        return this;
    }
	
	public AbstractList<V> withReference(AbstractList<V> reference){
		this.cpr = reference.comparator();
		this.allowDuplicate = reference.isAllowDuplicate();
		return this;
	}
	
	/**
	 * If the List is Empty
	 * 
	 * @return boolean of size
	 */
	public boolean isEmpty() {
        return values.size() < 1;
    }

    public boolean contains(Object o) {
        return values.contains(o);
    }

    public Iterator<V> iterator() {
        return values.iterator();
    }

    public Object[] toArray() {
        return values.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return values.toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
        return values.containsAll(c);
    }

    public boolean addAll(int index, Collection<? extends V> c) {
    	
    	for(Iterator<? extends V> i = c.iterator();i.hasNext();){
    		V item = i.next();
    		add(index++, item);
    	}
    	return true;
//        return values.addAll(index, c);
    }
    
	public boolean addAll(Iterator<? extends V> list){
		while(list.hasNext()){
			V item = list.next();
			if(item!=null){
				if(!add(item)){
					return false;
				}	
			}
		}
		return true;
	}
	
	public boolean addAll(Collection<? extends V> list){
		return addAll(list.iterator());
	}
	
    public boolean removeAll(Collection<?> c) {
        return removeAll(c.iterator());
    }

    public boolean retainAll(Collection<?> c) {
        return values.retainAll(c);
    }
    
    public boolean removeAll(Iterator<?> i) {
    	Object oldValue=null;
		while(i.hasNext()){
			Object item = i.next();
			if(item!=null){
				i.remove();
				fireProperty(item, null, oldValue);
			}
			oldValue = item;
		}
		return true;
	}

    public void clear() {
    	removeAll(iterator());
    }

    public V set(int index, V element) {
        return values.set(index, element);
    }

    /**
     * Add a Element after the Element from the second Parameter
     * @param element
     * @param beforeElement
     * @return the List
     */
    public AbstractList<V> with(V element, V beforeElement) {
    	int index = getIndex(beforeElement);
    	add(index, element);
    	return this;
    }
    
	@SuppressWarnings("unchecked")
   public <ST extends AbstractList<V>> ST with(Collection<?> values) {
		for(Iterator<?> i = values.iterator();i.hasNext();){
			with( i.next() );
		}
		return (ST)this;
	}
	
	public abstract AbstractList<V> with(Object... values);
    
    public void add(int index, V element) {
        values.add(index, element);
        V beforeValue = null;
        if(index>0){
        	beforeValue = get(index - 1);
        	fireProperty(null, element, beforeValue);
        }
    }

    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    public ListIterator<V> listIterator() {
        return values.listIterator();
    }

    public ListIterator<V> listIterator(int index) {
        return values.listIterator(index);
	}
	
	public int size() {
		return this.values.size();
	}
}