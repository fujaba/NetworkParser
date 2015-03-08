package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

public abstract class AbstractList<V> extends AbstractArray<V> implements Iterable<V>   {
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
	
	public int removeItemByObject(V value) {
		return super.removeByObject(value);
	}

	public int lastIndexOf(Object o) {
		return super.lastindexOf(o);
	}
	
	public void add(int index, V element) {
		int pos = hasKey(element, size);
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
		return (V)removeByIndex(index, SMALL_KEY);
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
