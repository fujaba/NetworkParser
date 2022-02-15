package de.uniks.networkparser.list;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An optimized version of AbstractList.ListItr
 *
 * @author Stefan Lindel
 * @param <E> ElementType
 */
public class SimpleIterator<E> implements ListIterator<E> {
	/** index of next element to return */
	private int cursor;
	/** index of last element returned; -1 if no such */
	private int lastRet;
	private AbstractArray<E> list;
	private int checkPointer = -1;

	/**
	 * Instantiates a new simple iterator.
	 *
	 * @param list the list
	 */
	public SimpleIterator(AbstractArray<E> list) {
		this.with(list, 0);
	}

	/**
	 * Instantiates a new simple iterator.
	 *
	 * @param list the list
	 * @param checkPointer the check pointer
	 */
	public SimpleIterator(AbstractArray<E> list, boolean checkPointer) {
		this.cursor = 0;
		this.lastRet = -1;
		this.list = list;
		if (list != null && checkPointer) {
			this.checkPointer = this.list.size();
		}
	}

	/**
	 * Instantiates a new simple iterator.
	 *
	 * @param collection the collection
	 */
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

	/**
	 * Instantiates a new simple iterator.
	 *
	 * @param list the list
	 * @param index the index
	 */
	public SimpleIterator(AbstractArray<E> list, int index) {
		this.with(list, index);
	}

	/**
	 * With.
	 *
	 * @param newList the new list
	 * @param cursor the cursor
	 * @return the simple iterator
	 */
	public SimpleIterator<E> with(AbstractArray<E> newList, int cursor) {
		this.cursor = cursor;
		this.lastRet = -1;
		this.list = newList;
		return this;
	}

	/**
	 * Checks for previous.
	 *
	 * @return true, if successful
	 */
	@Override
   public boolean hasPrevious() {
		return cursor != 0;
	}

	/**
	 * Next index.
	 *
	 * @return the int
	 */
	@Override
   public int nextIndex() {
		return cursor;
	}

	/**
	 * Previous index.
	 *
	 * @return the int
	 */
	@Override
   public int previousIndex() {
		return cursor - 1;
	}

	/**
	 * Previous.
	 *
	 * @return the e
	 */
	@Override
   public E previous() {
		int i = cursor - 1;
		if (i < 0)
			throw new NoSuchElementException();

		cursor = i;
		return list.get(lastRet = i);
	}

	/**
	 * Sets the.
	 *
	 * @param e the e
	 */
	@Override
   public void set(E e) {
		if (lastRet < 0)
			throw new IllegalStateException();
		try {
			list.setValue(lastRet, e, AbstractArray.SMALL_KEY);
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Adds the.
	 *
	 * @param e the e
	 */
	@Override
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

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasNext() {
		return cursor < list.size;
	}

	/**
	 * Next.
	 *
	 * @return the e
	 */
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
		return list.get(lastRet);
	}

	/**
	 * Removes the.
	 */
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

	/**
	 * With.
	 *
	 * @param newList the new list
	 * @return the simple iterator
	 */
	@SuppressWarnings("unchecked")
	public SimpleIterator<E> with(AbstractArray<?> newList) {
		this.cursor = 0;
		this.lastRet = -1;
		this.list = (AbstractArray<E>) newList;
		return this;
	}

	/**
	 * Position.
	 *
	 * @return the int
	 */
	public int position() {
		return cursor;
	}

	/**
	 * With check pointer.
	 *
	 * @param checkPointer the check pointer
	 * @return the simple iterator
	 */
	public SimpleIterator<E> withCheckPointer(boolean checkPointer) {
		if (checkPointer && this.list != null) {
			this.checkPointer = this.list.size();
		} else {
			this.checkPointer = -1;
		}
		return this;
	}

	/**
	 * Current.
	 *
	 * @return the e
	 */
	public E current() {
		if (lastRet >= 0) {
			return list.get(lastRet);
		}
		return null;
	}
}
