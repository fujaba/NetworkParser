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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.listold.SimpleBigList;
import de.uniks.networkparser.listold.SimpleSmallList;

/**
 * The Class EntityList.
 */

public abstract class AbstractList<V> implements BaseItem {
	protected SimpleSmallList<V> items;

	protected void newBigList(){
		this.items = new SimpleBigList<V>(items);
	}
	
	protected void newSmallList(){
		this.items = new SimpleSmallList<V>(items);
	}
	
	protected void resize() {
		// if no Items is Set
		if(items == null) {
			newSmallList();
			return;
		}
		// EnsureCapacity
		if(items.usedSize()>=SimpleBigList.MINHASHINGSIZE && items.minSize()==0){
			// It is a small List must be copy to new one
			newBigList();
			return;
		}
		if (items.usedSize() > items.realSize() * SimpleSmallList.MAXUSEDLIST) {
			// double hashTable size
			if(items.calcNewSize(items.size()) > SimpleBigList.MINHASHINGSIZE ){
				newBigList();
			}else{
				newSmallList();
			}
			return;
		}
		
		if (items.usedSize() < items.realSize() * SimpleSmallList.MINUSEDLIST) {
			// shrink hashTable size to a loadThreshold of 33%
			if(items.calcNewSize(items.size()) > SimpleBigList.MINHASHINGSIZE ){
				newBigList();
			}else{
				newSmallList();
			}
			return;
		}
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
		if(items==null){
			return null;
		}
		V object = this.items.get(index);
		if (object == null) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		return object;
	}

	public abstract AbstractList<V> getNewInstance();

	public boolean add(V element) {
		resize();
		return items.add(element);
	}
	
	public AbstractList<V> subSetItems(V fromElement, V toElement) {
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		int pos = 0;
		int size = size();
		while (pos < size) {
			if (comparator().compare(get(pos), fromElement) >= 0) {
				newList.add(get(pos));
				break;
			}
			pos++;
		}

		// MUST COPY
		while (pos < size) {
			if (comparator().compare(get(pos), toElement) >= 0) {
				break;
			}
			newList.add(get(pos++));
		}
		return newList;
	}

	/**
	 * Returns a view of the portion of this list between the specified
	 * fromIndex, inclusive, and toIndex, exclusive. (If fromIndex and toIndex
	 * are equal, the returned list is empty.) The returned list is backed by
	 * this list, so non-structural changes in the returned list are reflected
	 * in this list, and vice-versa. The returned list supports all of the
	 * optional list operations supported by this list.
	 *
	 * This method eliminates the need for explicit range operations (of the
	 * sort that commonly exist for arrays). Any operation that expects a list
	 * can be used as a range operation by passing a subList view instead of a
	 * whole list. For example, the following idiom removes a range of elements
	 * from a list:
	 *
	 * @param fromIndex
	 *            low endpoint (inclusive) of the subList
	 * @param toIndex
	 *            high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 */
	public List<V> subList(int fromIndex, int toIndex) {
		if(items==null){
			return new ArraySimpleList<V>();
		}
		return new ArraySimpleList<V>(this.items.subList(fromIndex, toIndex));
	}

	/**
	 * Returns a view of the portion of this map whose keys are less than (or
	 * equal to, if {@code inclusive} is true) {@code toKey}. The returned map
	 * is backed by this map, so changes in the returned map are reflected in
	 * this map, and vice-versa. The returned map supports all optional map
	 * operations that this map supports.
	 *
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an
	 * attempt to insert a key outside its range.
	 *
	 * @param toElement
	 *            high endpoint of the keys in the returned map
	 * @param inclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @return result a list with less item then the key
	 *
	 */
	public AbstractList<V> headSet(V toElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// MUST COPY
		for (int pos = 0; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), toElement);
			if (compare == 0) {
				if (inclusive) {
					newList.add(get(pos));
				}
				break;
			} else if (compare > 0) {
				newList.add(get(pos));
				break;
			}
		}
		return newList;
	}

	/**
	 * Returns a view of the portion of this map whose keys are greater than (or
	 * equal to, if {@code inclusive} is true) {@code fromKey}.
	 *
	 * @param fromElement
	 *            low endpoint of the keys in the returned map
	 * @param inclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @return a view of the portion of this map whose keys are greater than (or
	 *         equal to, if {@code inclusive} is true) {@code fromKey}
	 *
	 */
	public AbstractList<V> tailSet(V fromElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		int pos = 0;
		for (; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), fromElement);
			if (compare == 0) {
				if (inclusive) {
					newList.add(get(pos));
				}
				break;
			} else if (compare > 0) {
				newList.add(get(pos));
				break;
			}
		}

		// MUST COPY
		while (pos < size()) {
			newList.add(get(pos++));
		}
		return newList;
	}
	
	/** @return the First Element of the List */
	public V first() {
		if(items==null){
			return null;
		}
		return this.items.first();
	}

	/** @return the Last Element of the List */
	public V last() {
		if(items==null){
			return null;
		}
		return this.items.last();
	}


	/**
	 * @param index
	 *            of value
	 * @return the entity
	 */
	public Object getKey(int index) {
		if (index < 0 || this.items == null || index > this.items.size()) {
			return null;
		}
		return this.items.get(index);
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
		if (index == -1) {
			return false;
		}
		Object object = getItem(index);
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
		Object object = getItem(index);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}

	protected Object getItem(int index) {
		return getKey(index);
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
		Object object = getItem(index);
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
		Object object = getItem(index);
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
		return getItem(index).toString();
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
			if (index > 0) {
				resize();
				oldValue = this.items.get(index - 1);
				int position = items.getPositionKey(oldValue);
				if (position >= 0) {
					// Replace in List
					this.items.set(position, value);
				}
			}
		} else {
			add(value);
		}
		return this;
	}

	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowDuplicate() {
		if(items == null) {
			return false;
		}
		return this.items.isAllowDuplicate();
	}
	
	/**
	 * Is Allow Duplicate Entity in the List
	 *
	 * @return boolean if the List allow duplicate Entities
	 */
	public boolean isAllowEmptyValue() {
		if(items == null) {
			return false;
		}
		return this.items.isAllowEmptyValue();
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST withAllowDuplicate(
			boolean allowDuplicate) {
		if(items == null){
			return (ST) this;
		}
		this.items.withAllowDuplicate(allowDuplicate);
		return (ST) this;
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
		return removeItemByIndex(index);
	}

	protected V removeItemByIndex(int index) {
		if (index < 0 || items == null) {
			return null;
		}
		return items.removeByIndex(index);
	}

	
	public int removeItemByObject(Object key) {
		if(items==null){
			return -1;
		}
		return items.removeItemByObject(key);
	}

	/**
	 * Locate the Entity in the List
	 *
	 * @param key
	 *            Entity
	 * @return the position of the Entity or -1
	 */
	public int getIndex(Object key) {
		if(items == null){
			return -1;
		}
		return items.transformIndex(items.getPositionKey(key), key);
	}
	
	public V getByObject(Object key) {
		int index = getIndex(key);
		if(index<0) {
			return null;
		}
		return get(index);
	}

	public AbstractList<V> withCopyList(SimpleSmallList<V> reference) {
		this.items = reference;
		return this;
	}

	/**
	 * If the List is Empty
	 *
	 * @return boolean of size
	 */
	public boolean isEmpty() {
		if(items == null) {
			return true;
		}
		return items.isEmpty();
	}

	public boolean contains(Object o) {
		if(items == null) {
			return false;
		}
		return items.getPositionKey(o) >= 0;
	}
	
	public Iterator<V> iterator() {
		if(items == null) {
			return null;
		}
		return items.iterator();
	}

	public Object[] toArray() {
		if(items == null) {
			return new Object[0];
		}
		return items.toArray();
	}

	public <T> T[] toArray(T[] a) {
		if(items == null) {
			return null;
		}
		return items.toArray(a);
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!this.contains(o))
				return false;
		}
		return true;
	}

	public boolean removeAll(Collection<?> c) {
		return removeAll(c.iterator());
	}

	@SuppressWarnings("unchecked")
	public boolean removeAll(Iterator<?> i) {
		while (i.hasNext()) {
			removeItemByObject((V) i.next());
		}
		return true;
	}

	public void clear() {
		removeAll(iterator());
	}

	@SuppressWarnings("unchecked")
	public V set(int index, V element) {
		if(items == null) {
			return null;
		}
		return (V) items.set(index, element);
	}

	/**
	 * Add a Element after the Element from the second Parameter
	 *
	 * @param element
	 *            element to add
	 * @param beforeElement
	 *            element before the element
	 * @return the List
	 */
	public AbstractList<V> withInsert(V element, V beforeElement) {
		if(items == null) {
			newSmallList();
		}
		int index = getIndex(beforeElement);
		this.items.add(index, element);
		return this;
	}

	public abstract AbstractList<V> with(Object... values);

	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST withoutList(Collection<?> values) {
		for (Iterator<?> i = values.iterator(); i.hasNext();) {
			without(i.next());
		}
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST without(Object... values) {
		if (values == null) {
			return null;
		}
		for (Object item : values) {

			removeItemByObject((V) item);
		}
		return (ST) this;
	}

	public boolean retainAll(Collection<?> c) {
		for (int i = 0; i < size();) {
			if (!c.contains(get(i))) {
				remove(i);
			} else {
				i++;
			}
		}
		return true;
	}

	@Override
	public AbstractList<V> clone() {
		return clone(getNewInstance());
	}
	
	
	@SuppressWarnings("unchecked")
	public AbstractList<V> clone(AbstractList<V> newInstance) {
		newInstance.withCopyList((SimpleSmallList<V>) this.items.clone());
		return newInstance;
	}

	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST withList(Collection<?> values) {
		for (Iterator<?> i = values.iterator(); i.hasNext();) {
			with(i.next());
		}
		return (ST) this;
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST getFirstItems(int size) {
		AbstractList<V> newInstance = getNewInstance();
		if(size<0) {
			size = this.size();
		}
		int i=0;
		while(i<size) {
			newInstance.with(get(i++));	
		}
		return (ST) newInstance;
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST getLastItems(int size) {
		AbstractList<V> newInstance = getNewInstance();
		int count = this.size();
		if(size<0) {
			size = count;
		}
		int i=count  - size;
		while(i<count) {
			newInstance.with(get(i++));	
		}
		return (ST) newInstance;
	}

	public int indexOf(Object o) {
		if(items == null){
			return -1;
		}
		return items.indexOf(o);
	}
	public int lastIndexOf(Object o) {
		return items.lastIndexOf(o);
	}

	public ListIterator<V> listIterator() {
		if(items == null){
			//FIXME CREATE OWN ITERATOR
			return null;
		}
		return items.iteratorList(0);
	}

	public ListIterator<V> listIterator(int index) {
		if(items == null){
			//FIXME CREATE OWN ITERATOR
			return null;
		}
		return items.iteratorList(index);
	}

	public ListIterator<V> listIteratorReverse() {
		if(items == null){
			//FIXME CREATE OWN ITERATOR
			return null;
		}
		return items.iteratorList(items.size());
	}

	public int size() {
		if(items==null){
			return 0;
		}
		return this.items.size();
	}

	protected void fireProperty(Object oldElement, Object newElement,
			Object beforeElement, Object value) {
	}
	
	public Comparator<V> comparator() {
		return items.comparator();
	}

	public boolean isComparator() {
		if(this.items == null){
			return false;
		}
		return items.isComparator();
	}

	public AbstractList<V> withComparator(Comparator<V> comparator) {
		if(this.items == null){
			return this;
		}
		this.items.withComparator(comparator);
		return this;
	}
	public AbstractList<V> withComparator(String column) {
		if(this.items == null){
			return this;
		}
		this.items.withComparator(column);
		return this;
	}
	
	public boolean isVisible() {
		if(this.items == null){
			return false;
		}
		return this.items.isVisible();
	}
}
