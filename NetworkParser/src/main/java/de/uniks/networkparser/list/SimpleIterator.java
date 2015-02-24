package de.uniks.networkparser.list;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * An optimized version of AbstractList.ListItr
 */
public class SimpleIterator<E> implements ListIterator<E> {
	private int cursor;       // index of next element to return
	private int lastRet = -1; // index of last element returned; -1 if no such
	private AbstractArray<E> list;

	public SimpleIterator(AbstractArray<E> list) {
		this.list = list;
	}

	public SimpleIterator(AbstractArray<E> list, int index) {
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
			list.setValue(lastRet, e, AbstractArray.SMALL_KEY);
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	public void add(E e) {
		try {
			int size = list.size();
			int pos = list.hasKey(e, size);
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
		return cursor<list.size();
	}

	@Override
	public E next() {
		int i = cursor;
		if (i >= list.size())
			throw new ConcurrentModificationException();
		cursor = i + 1;
		return (E) list.get(lastRet = i);
	}

	@Override
	public void remove() {
		if (lastRet < 0)
			throw new IllegalStateException();
		try {
			list.removeByIndex(lastRet, AbstractArray.SMALL_KEY);
			cursor = lastRet;
			lastRet = -1;
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}
}

