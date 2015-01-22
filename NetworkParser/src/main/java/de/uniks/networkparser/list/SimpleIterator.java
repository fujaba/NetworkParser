package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * An optimized version of AbstractList.ListItr
 */
public class SimpleIterator<E> implements ListIterator<E> {
	private int cursor;       // index of next element to return
	private int lastRet = -1; // index of last element returned; -1 if no such
	private AbstractList<E> list;

	public SimpleIterator(AbstractList<E> list) {
        this.list = list;
	}
	
	public SimpleIterator(AbstractList<E> list, int index) {
        this.cursor = index;
        this.list = list;
	}
    public boolean hasPrevious() {
        return cursor != 0;
    }

    public int nextIndex() {
        return cursor;
    }

    public int previousIndex() {
        return cursor - 1;
    }

    public E previous() {
        int i = cursor - 1;
        if (i < 0)
            throw new NoSuchElementException();
        
        cursor = i;
        return list.get(lastRet = i);
    }

    public void set(E e) {
        if (lastRet < 0)
            throw new IllegalStateException();
        try {
            list.set(lastRet, e);
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }

    public void add(E e) {
        try {
            int i = cursor;
            list.add(i, e);
            cursor = i + 1;
            lastRet = -1;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
    }
   
	@Override
	public boolean hasNext() {
		return cursor<list.size();
	}
		
	@Override
	public E next() {
		 int i = cursor;
        if (i >= list.size())
            throw new NoSuchElementException();
        if (i >= list.size())
            throw new ConcurrentModificationException();
        cursor = i + 1;
        return list.get(lastRet = i);
	}

	@Override
	public void remove() {
		if (lastRet < 0)
            throw new IllegalStateException();
        try {
            list.remove(lastRet);
            cursor = lastRet;
            lastRet = -1;
        } catch (IndexOutOfBoundsException ex) {
            throw new ConcurrentModificationException();
        }
	}
}

