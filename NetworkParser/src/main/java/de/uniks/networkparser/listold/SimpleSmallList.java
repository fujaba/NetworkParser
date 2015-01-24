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
