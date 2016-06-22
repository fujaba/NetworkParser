package de.uniks.networkparser.list;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;

/**
 * The Class is for generic implementation of List and Sets
 * @author Stefan
 *
 * @param <V> generic Parameter for Simple-Collection
 */
public abstract class AbstractList<V> extends AbstractArray<V> implements Iterable<V> {
	/**
	 * <p>This implementation iterates over the specified collection, and adds
	 * each object returned by the iterator to this collection, in turn.
	 *
	 * <p>Note that this implementation will throw an
	 * <tt>UnsupportedOperationException</tt> unless <tt>add</tt> is
	 * overridden (assuming the specified collection is non-empty).
	 * @param c	List of Elements for adding
	 * @return success
	 * @see #add(Object)
	 */
	public boolean addAll(Collection<? extends V> c) {
		if(c==null){
			return false;
		}
		boolean modified = false;
		for (V e : c)
			if (add(e))
				modified = true;
		return modified;
	}

	/**
	 * Add a Value to internal List and Array if nesessary
	 *
	 * @param value		the new Value
	 * @return			this boolean if success
	 */
	public boolean add(V value) {
		int pos = hasKey(value);
		if(pos<0) {
			return false;
		}
		grow(size + 1);
		addKey(pos, value, size + 1);
		return true;
	}

	public ListIterator<V> listIterator() {
		return new SimpleIterator<V>(this);
	}

	public ListIterator<V> listIterator(int index) {
		return new SimpleIterator<V>(this, index);
	}

	public ListIterator<V> iteratorReverse() {
		return new SimpleIterator<V>(this, size());
	}

	@Override
	public AbstractList<V> withList(Collection<?> values) {
		super.withList(values);
		return this;
	}

	public void copyEntity(BaseItem target, int pos) {
		if(target != null)
			target.with(get(pos));
	}

	public BaseItem subSet(V fromElement, V toElement) {
		BaseItem newList = getNewList(false);
		int end = indexOf(toElement);
		// MUST COPY
		for(int pos = indexOf(fromElement);pos<end;pos++){
			copyEntity(newList, pos);
		}
		return newList;
	}

	/**
	 * Get the next bigger Value of a Set
	 * @param element Element for check
	 * @param sameElement boolen for switch return sameElement
	 * @return the element or higher Element
	 */
	public V ceiling(V element, boolean sameElement) {
		int pos = indexOf(element);
		if(pos < size) {
			return get(pos + 1);
		}
		if(sameElement) {
			return element;
		}
		return null;
	}

	public void add(int index, V element) {
		int pos = hasKey(element);
		if(pos>=0) {
			grow(size + 1);
			addKey(index, element,size + 1);
		}
	}

	public V set(int index, V element) {
		if(index<0 || index>size) {
			return null;
		}
		setValue(index, element, SMALL_KEY);
		return element;
	}

	@SuppressWarnings("unchecked")
	public V remove(int index) {
		if(index<0 || index>size) {
			return null;
		}
		return (V)removeByIndex(index, SMALL_KEY, this.index);
	}

	public boolean addAll(int index, Collection<? extends V> values) {
		if(values==null) {
			return false;
		}
		boolean allAdded=true;
		int newSize = size + values.size();
		grow(newSize);
		for(Iterator<? extends V> i = values.iterator();i.hasNext();){
			if(addKey(index++, i.next(), newSize)<0){
				allAdded=false;
			}
		}
		return allAdded;
	}

	@SuppressWarnings("unchecked")
	protected <ST extends AbstractList<V>> ST filterItems(ST filterCollection, Condition<?> newValue) {
		for(int i=0;i<size();i++) {
			V item = get(i);
			Condition<Object> filter = (Condition<Object>) newValue;
			if(filter.update(item)) {
				filterCollection.add(item);
			}
		}
		return (ST) filterCollection;
	}

	public Iterator<V> iterator() {
		return new SimpleIterator<V>(this).withCheckPointer(true);
	}
	public Iterator<V> iterator(boolean checkPointer) {
		return new SimpleIterator<V>(this).withCheckPointer(checkPointer);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Collection<?> == false) {
			return false;
		}
		Collection<?> collection = (Collection<?>)obj;
		if(collection.size() != this.size()) {
			return false;
		}
		for(Object item : this) {
			if(collection.contains(item) == false) {
				return false;
			}
		}
		return true;
	}
}
