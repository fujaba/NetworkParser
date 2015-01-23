package de.uniks.networkparser.listold;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.listold.not.SimpleInterface;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class SimpleSmallList<V> {
	/**
	 * Add a Element to the List
	 * 
	 * @param newValue to add a Value
	 * @return boolean if success add the Value
	 */
	public boolean addEntity(V newValue) {
		if (newValue == null)
			return false;
		if (cpr != null) {
			for (int i = 0; i < size(); i++) {
				int result = comparator().compare(get(i), newValue);
				if (result >= 0) {
					if (!isAllowDuplicate() && get(i) == newValue) {
						return false;
					}
					addKey(i, newValue);
					V beforeElement = null;
					if (i > 0) {
						beforeElement = this.get(i - 1);
					}
					fireProperty(null, newValue, beforeElement, null);
					return true;
				}
			}
		}

		if (!isAllowDuplicate()) {
			if (this.contains(newValue)) {
				return false;
			}
		}

		if (addKey(-1, newValue) >= 0) {
			V beforeElement = null;
			if (size() > 1) {
				beforeElement = this.get(size() - 1);
			}
			fireProperty(null, newValue, beforeElement, null);
			return true;
		}
		return false;
	}

	protected void fireProperty(Object object, V newValue, V beforeElement,
			Object object2) {
	}

	/**
	 * Add a Key to internal List and Array if nesessary
	 *
	 * @param newValue
	 *            the new Value
	 * @param pos
	 *            the new Position -1 = End
	 */
	protected int addKey(int pos, V newValue) {
		if (pos == -1) {
			this.add(newValue);
			return size(); 
		}
		this.add(pos, newValue);
		return pos;
	}
	
	public int removeItemByObject(Object key) {
		int index = getPositionKey(key);
		if (index < 0) {
			return -1;
		}
		removeByIndex(index);
		return index;
	}
	
	
	public int transformIndex(int index, Object value) {
		return index;
	}


    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementKey[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementKey[i]))
                    return i;
        }
        return -1;
    }
    
	@Override
	public Object set(int index, V element) {
		Object oldValue = elementKey[index];
        elementKey[index] = element;
        return oldValue;
	}

	public void add(int index, V element) {
		int i = size()+1;
		while(i>index) {
			elementKey[i] = elementKey[i - 1]; 	
		}
        elementKey[index] = element;
        size++;
    }
	
	@SuppressWarnings("unchecked")
	public SimpleSmallList<V> with(Object... elements) {
		if(elements == null) {
			return this;
		}
		for(Object item : elements) {
			add((V)item);
		}
        return this;
    }
    
	@Override
	public Iterator<V> iterator() {
		return new SimpleIterator<V>(this);
	}
	
	public ListIterator<V> iteratorList(int index) {
		return new SimpleIterator<V>(this, index);
	}

	public ListIterator<V> iteratorReverse() {
		return new SimpleIterator<V>(this, size());
	}
	
	private Object grow(int minCapacity, Object[] elements) {
		Object[] result;
		if(minCapacity > elements.length * SimpleSmallList.MAXUSEDLIST) {
			// bigger
			result = new Object[elements.length*2];
		} else if (minCapacity < elements.length * SimpleSmallList.MINUSEDLIST) {
			// smaller
			result = new Object[minCapacity*2];
		}else{
			return elements;
		}
		for(int i=0;i<minCapacity;i++) {
			result[i] = elements[i];
		}
		return result;
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(int index) {
		if(index<0) {
			index = size + 1 - index;
		}
		if(index>=0 && index<size){
			return (V) elementKey[index];
		}
		return null;
	}

	
	@Override
	public boolean remove(Object o) {
		return removeItemByObject(o)>=0;
	}

	@SuppressWarnings("unchecked")
	public V removeByIndex(int index) {
		V oldValue = (V) elementKey[index];
		while(index<size) {
			elementKey[index] = elementKey[++index];
		}
		size--;
		elementKey[index] = null;
		return oldValue;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
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
	
	  /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements in common with the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
    	if(c==null){
    		return false;
    	}
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

	public Object clone() {
		return new SimpleSmallList<V>(this);
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		int len = this.size;
		if(a.length<len) {
			len = a.length;
		}
		for(int i=0;i<len;i++) {
			a[i] = (T) elementKey[i];
		}
		return a;
	}

	public Collection<V> subList(int fromIndex, int toIndex) {
		SimpleSmallList<V> list = new SimpleSmallList<V>(this);
		while(fromIndex<=toIndex) {
			list.with(elementKey[fromIndex]);
		}
		return list;
	}

	public int lastIndexOf(Object o) {
	  if (o == null) {
            for (int i = size - 1; i >= 0; i--)
                if (elementKey[i]==null)
                    return i;
        } else {
            for (int i = size - 1; i >= 0 ; i--)
                if (o.equals(elementKey[i]))
                    return i;
        }
        return -1;
	}
}
