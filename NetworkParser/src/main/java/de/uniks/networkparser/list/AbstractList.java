package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.uniks.networkparser.interfaces.BaseList;

public abstract class AbstractList<V> extends AbstractArray implements BaseList {
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
        boolean modified = false;
        for (V e : c)
            if (add(e))
                modified = true;
        return modified;
    }
    
	/**
	 * Add a Value to internal List and Array if nesessary
	 *
	 * @param newValue
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 * @return  this boolean if success
	 */
	public boolean add(V e) {
		int pos = checkKey(e);
		if(pos>=0) {
			addKey(pos, e);
		}
		return true;
	}
    
	public Iterator<V> iterator() {
		return new SimpleIterator<V>(this);
	}
	
	/** @return the First Element of the List */
	public V first() {
		if (this.size() > 0) {
			return this.get(0);
		}
		return null;
	}

	/** @return the Last Element of the List */
	public V last() {
		if (this.size() > 0) {
			return this.get(this.size() - 1);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public V get(int index) {
		return (V) super.getKey(index);
	}
	
    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array.  If the list fits in the
     * specified array, it is returned therein.  Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of
     * this list.
     *
     * <p>If the list fits in the specified array with room to spare
     * (i.e., the array has more elements than the list), the element in
     * the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of the
     * list <i>only</i> if the caller knows that the list does not contain
     * any null elements.)
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
    	Object[] elementData;
    	if(isBig()) {
    		elementData = (Object[]) elements[SMALL_KEY];
    	}else{
    		elementData = elements;
    	}
        if (a.length < size) {
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public ListIterator<V> listIterator() {
		return new SimpleIterator<V>(this);
	}

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements not present in the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
    	if(c==null){
    		return false;
    	}
        boolean modified = false;
        Iterator<V> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

	public ListIterator<V> listIterator(int index) {
		return new SimpleIterator<V>(this);
	}

	public ListIterator<V> iteratorReverse() {
		return new SimpleIterator<V>(this, size());
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST with(V... values) {
		super.withAll(values);
		return (ST) this;
	}
	
	public AbstractList<V> withAll(Object... values) {
		super.withAll(values);
		return this;
	}
	
	public AbstractList<V> withList(Collection<?> values) {
		super.withList(values);
		return this;
	}
	
	public AbstractList<V> withMap(Map<?, ?> value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	
	
	
	
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean addAll(int index, Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}


	public V set(int index, V element) {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(int index, V element) {
		// TODO Auto-generated method stub
		
	}

	public V remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<V> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	public int removeItemByObject(V value) {
		// TODO Auto-generated method stub
		return 0;
	}
	public V getByObject(String id2) {
		// TODO Auto-generated method stub
		return null;
	}

}
