package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseList;

public abstract class AbstractList<V> extends AbstractArray<V> implements BaseList {
	public abstract AbstractList<V> getNewInstance();
    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified collection, and adds
     * each object returned by the iterator to this collection, in turn.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> unless <tt>add</tt> is
     * overridden (assuming the specified collection is non-empty).
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     *
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
	 * @param value
	 *            the new Value
	 * @return  this boolean if success
	 */
	public boolean add(V value) {
		int pos = hasKey(value, size);
		if(pos>=0) {
			grow(size + 1);
			addKey(pos, value, size);
			return true;
		}
		return false;
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
	
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST with(V... values) {
		super.withAll(values);
		return (ST) this;
	}
	
	@Override
	public AbstractList<V> withAll(Object... values) {
		super.withAll(values);
		return this;
	}
	
	@Override
	public AbstractList<V> withList(Collection<?> values) {
		super.withList(values);
		return this;
	}

	public void copyEntity(AbstractList<V> target, int pos) {
		if(target != null)
			target.add(get(pos));
	}

	public AbstractList<V> subSet(V fromElement, V toElement) {
		AbstractList<V> newList = getNewInstance();
		int end = indexOf(toElement);
		// MUST COPY
		for(int pos = indexOf(fromElement);pos<end;pos++){
			copyEntity(newList, pos);
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
	 * @param <ST> the ContainerClass
	 * 
	 *             
	 * @return a view of the portion of this map whose keys are greater than (or
	 *         equal to, if {@code inclusive} is true) {@code fromKey}
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST tailSet(V fromElement, boolean inclusive) { 
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		int pos = 0;
		for (; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), fromElement); // FIXME we do not always have a comparator. AZ
			if (compare == 0) {
				if (inclusive) {
					copyEntity(newList, pos);
				}
				break;
			} else if (compare > 0) {
				copyEntity(newList, pos);
				break;
			}
		}

		// MUST COPY
		while (pos < size()) {
			copyEntity(newList, pos++);
		}
		return (ST) newList;
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
	 * @param <ST> the ContainerClass 
	 * 
	 * @return result a list with less item then the key
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST headSet(V toElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// MUST COPY
		for (int pos = 0; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), toElement); // FIXME we do not always have a comparator. AZ
			if (compare == 0) {
				if (inclusive) {
					copyEntity(newList, pos);
				}
				break;
			} else if (compare > 0) {
				copyEntity(newList, pos);
				break;
			}
		}
		return (ST) newList;
	}
	
	public int removeItemByObject(V value) {
		return super.removeByObject(value);
	}

	public int lastIndexOf(Object o) {
		return super.lastindexOf(o);
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
}
