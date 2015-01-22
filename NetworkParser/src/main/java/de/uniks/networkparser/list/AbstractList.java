package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class AbstractList<V> extends AbstractArray {
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
		grow(size + 1);

//		elementKey[size++] = e;
		return true;
	}
    
	public Iterator<V> iterator() {
		return new SimpleIterator<V>(this);
	}

	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ListIterator<V> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public ListIterator<V> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}
	

	public boolean addAll(int index, Collection<? extends V> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public V get(int index) {
		// TODO Auto-generated method stub
		return null;
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
}
