package de.uniks.networkparser.list;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An optimized version of AbstractList.ListItr
 */
public class SimpleIterator<E> implements ListIterator<E> {
	private int cursor;	   // index of next element to return
	private int lastRet; // index of last element returned; -1 if no such
	private AbstractArray<E> list;

	public SimpleIterator(AbstractArray<E> list) {
		this.with(list, 0);
	}
	
	@SuppressWarnings("unchecked")
	public SimpleIterator(Object collection) {
		if(collection instanceof AbstractArray<?>) {
			this.list = (AbstractArray<E>) collection;	
		} else if (collection instanceof List<?>) {
			this.list = new SimpleList<E>();
			this.list.withList((List<?>)collection);
		}
		this.cursor = 0;
		this.lastRet = -1;
	}


	public SimpleIterator(AbstractArray<E> list, int index) {
		this.with(list, index);
	}

	public SimpleIterator<E> with(AbstractArray<E> newList, int cursor)
	{
	   this.cursor = cursor;
	   this.lastRet = -1;
	   this.list = newList;
	   return this;
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
			list.setValue(lastRet, e, AbstractArray.SMALL_KEY);
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	public void add(E e) {
		try {
			int size = list.size();
			int pos = list.hasKey(e);
			if(pos>=0) {
				list.grow(size + 1);
				list.addKey(cursor, e, size + 1);
				cursor++;
				lastRet = -1;
			}
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	@Override
	public boolean hasNext() {
	   return cursor<list.size;
	}

	@Override
	public E next() {
		if (cursor >= list.size)
			throw new ConcurrentModificationException();
		lastRet  = cursor;
		cursor = cursor + 1;
		return (E) list.get(lastRet);
	}

	@Override
	public void remove() {
		if (lastRet < 0)
			throw new IllegalStateException();
		try {
			list.removeByIndex(lastRet, AbstractArray.SMALL_KEY, list.index);
			cursor = lastRet;
			lastRet = -1;
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}
	
	@SuppressWarnings("unchecked")
	public SimpleIterator<E> with(AbstractArray<?> newList)
	{
	   this.cursor = 0;
	   this.lastRet = -1;
	   this.list = (AbstractArray<E>) newList;
	   return this;
	}
}
