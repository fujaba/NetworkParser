package de.uniks.networkparser.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseList;
import de.uniks.networkparser.interfaces.FactoryEntity;

public abstract class AbstractList<V> extends AbstractArray implements BaseList {
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
		return (V) super.getKeyByIndex(index);
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
		target.add(get(pos));
	}

	public AbstractList<V> subSet(V fromElement, V toElement) {
		AbstractList<V> newList = getNewInstance();
		// PRE WHILE
		int pos = 0;
		int size = size();
		while (pos < size) {
			V item = get(pos);
			if (comparator().compare(item, fromElement) >= 0) {
				copyEntity(newList, pos++);
				break;
			}
		}
		// MUST COPY
		while (pos < size) {
			V item = get(pos);
			if (comparator().compare(item, toElement) >= 0) {
				break;
			}
			copyEntity(newList, pos++);
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
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST tailSet(V fromElement, boolean inclusive) { 
		AbstractList<V> newList = getNewInstance();

		// PRE WHILE
		int pos = 0;
		for (; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), fromElement);
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
	 * @return result a list with less item then the key
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends AbstractList<V>> ST headSet(V toElement, boolean inclusive) {
		AbstractList<V> newList = getNewInstance();

		// MUST COPY
		for (int pos = 0; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), toElement);
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
	
	public Object getValueItem(Object key) {
		int pos = indexOf(key);
		if (pos >= 0) {
			return this.getValueByIndex(pos);
		}
		if (!(key instanceof String)) {
			return null;
		}
		String keyString = "" + key;
		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < keyString.length(); len++) {
			char temp = keyString.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < keyString.length(); end++) {
					temp = keyString.charAt(end);
					if (keyString.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == keyString.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == keyString.length()) {
			id = -1;
		}

		Object child = get(indexOf(keyString.substring(0, len)));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractList<?>) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return list.get(id);
						}
					}
				} else {
					return child;
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						if (end == len + 2) {
							// Get List
							if (this instanceof FactoryEntity) {
								AbstractList<?> result = (AbstractList<?>) ((FactoryEntity) this)
										.getNewArray();
								AbstractList<?> items = (AbstractList<?>) child;
								for (int z = 0; z < items.size(); z++) {
									result.withAll(((AbstractList<?>) items
											.get(z)).getValueItem(keyString
											.substring(end + 1)));
								}
								return result;
							}
						}
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return ((SimpleKeyValueList<?, ?>) list.get(id))
									.getValueItem(keyString.substring(end + 1));
						}
					}
				} else {
					return ((SimpleKeyValueList<?, ?>) child)
							.getValueItem(keyString.substring(end + 1));
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public V remove(int index) {
		return (V)super.removeByIndex(index, SMALL_KEY);
	}
	
	public int removeItemByObject(V value) {
		return super.removeByObject(value);
	}

	public int lastIndexOf(Object o) {
		return super.lastindexOf(o);
	}

	public void add(int index, V element) {
		super.addKey(index, element);
	}

	public boolean addAll(int index, Collection<? extends V> c) {
		return super.addAllKeys(index, c);
	}


	public V set(int index, V element) {
		super.setValue(index, element, SMALL_KEY);
		return element;
	}
}
