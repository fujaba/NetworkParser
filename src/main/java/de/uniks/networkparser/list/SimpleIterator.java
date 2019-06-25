package de.uniks.networkparser.list;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An optimized version of AbstractList.ListItr
 * 
 * @author Stefan Lindel
 */
public class SimpleIterator<E> implements ListIterator<E> {
	/** index of next element to return */
	private int cursor;
	/** index of last element returned; -1 if no such */
	private int lastRet;
	private AbstractArray<E> list;
	private int checkPointer = -1;

	public SimpleIterator(AbstractArray<E> list) {
		this.with(list, 0);
	}

	public SimpleIterator(AbstractArray<E> list, boolean checkPointer) {
		this.cursor = 0;
		this.lastRet = -1;
		this.list = list;
		if (list != null && checkPointer) {
			this.checkPointer = this.list.size();
		}
	}

	@SuppressWarnings("unchecked")
	public SimpleIterator(Object collection) {
		if (collection instanceof AbstractArray<?>) {
			this.list = (AbstractArray<E>) collection;
		} else if (collection instanceof List<?>) {
			this.list = new SimpleList<E>();
			this.list.withList((List<?>) collection);
		}
		this.cursor = 0;
		this.lastRet = -1;
	}

	public SimpleIterator(AbstractArray<E> list, int index) {
		this.with(list, index);
	}

	public SimpleIterator<E> with(AbstractArray<E> newList, int cursor) {
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
		if (list == null) {
			return;
		}
		int size = list.size();
		int pos = list.hasKey(e);
		if (pos >= 0) {
			list.grow(size + 1);
			list.addKey(cursor, e, size + 1);
			cursor++;
			lastRet = -1;
			if (this.checkPointer >= 0) {
				this.checkPointer = list.size();
			}
		}
	}

	@Override
	public boolean hasNext() {
		return cursor < list.size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if (list == null) {
			return null;
		}
		int size = list.size();
		if (cursor >= size) {
			throw new ConcurrentModificationException();
		}
		if (this.checkPointer >= 0 && this.checkPointer != size) {
			throw new ConcurrentModificationException();
		}
		lastRet = cursor;
		cursor = cursor + 1;
		if (list.index == 0) {
			Object[] elements;
			if (list.isComplex(size)) {
				elements = (Object[]) list.elements[0];
			} else {
				elements = list.elements;
			}
			return (E) elements[lastRet];
		}
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
			if (this.checkPointer >= 0) {
				this.checkPointer = list.size();
			}
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	@SuppressWarnings("unchecked")
	public SimpleIterator<E> with(AbstractArray<?> newList) {
		this.cursor = 0;
		this.lastRet = -1;
		this.list = (AbstractArray<E>) newList;
		return this;
	}

	public int position() {
		return cursor;
	}

	public SimpleIterator<E> withCheckPointer(boolean checkPointer) {
		if (checkPointer && this.list != null) {
			this.checkPointer = this.list.size();
		} else {
			this.checkPointer = -1;
		}
		return this;
	}

	public E current() {
		if (lastRet >= 0) {
			return (E) list.get(lastRet);
		}
		return null;
	}
}
