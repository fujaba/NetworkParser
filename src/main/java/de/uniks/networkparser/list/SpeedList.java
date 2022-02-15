package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.uniks.networkparser.interfaces.BaseItem;

/**
 * The Class SpeedList.
 *
 * @author Stefan
 * @param <V> the value type
 */
public class SpeedList<V> extends AbstractArray<V> implements List<V>, Iterable<V> {
	
	/**
	 * Instantiates a new speed list.
	 */
	public SpeedList() {
		withFlag(SimpleList.ALLOWDUPLICATE);
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SpeedList<V>();
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	public Iterator<V> iterator() {
		return new Iterator<V>() {
			private int cursor;

			@Override
			public boolean hasNext() {
				return cursor < size;
			}

			@SuppressWarnings("unchecked")
			@Override
			public V next() {
				if (size < MINHASHINGSIZE) {
					return (V) elements[cursor++];
				}
				return (V) ((Object[]) elements[0])[cursor++];
			}
		};
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean contains(Object o) {
		if (size < MINHASHINGSIZE) {
			for (int pos = 0; pos < this.size; pos++) {
				if (o == elements[pos++]) {
					return true;
				}
			}
			return false;
		}
		return getPosition(o, 0, false) >= 0;
	}

	/**
	 * Adds the.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	@Override
	public boolean add(V e) {
		if (size < MINHASHINGSIZE - 10) {
			if (elements == null) {
				elements = new Object[42];
			} else if (size >= elements.length) {
				int newSize = size + size + 5;
				elements = arrayCopy(elements, newSize);
				this.index = 0;
			}
			elements[size] = e;
			this.size++;
			return true;
		}
		return super.add(e);
	}

	/**
	 * Removes the.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o) >= 0;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null || values.length < 1) {
			return false;
		}
		int newSize = size + values.length;
		grow(newSize);
		for (Object value : values) {
			if (value == null) {
				continue;
			}
			this.addKey(this.size, value, newSize);
		}
		return size > newSize - values.length;
	}

	/**
	 * Adds the all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	@Override
	public boolean addAll(Collection<? extends V> c) {
		if (c == null) {
			return false;
		}
		boolean modified = false;
		for (V e : c) {
			if (add(e)) {
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Adds the all.
	 *
	 * @param index the index
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean addAll(int index, Collection<? extends V> values) {
		if (values == null) {
			return false;
		}
		boolean allAdded = true;
		int newSize = size + values.size();
		grow(newSize);
		for (Iterator<? extends V> i = values.iterator(); i.hasNext();) {
			if (addKey(index++, i.next(), newSize) < 0) {
				allAdded = false;
			}
		}
		return allAdded;
	}

	/**
	 * Sets the.
	 *
	 * @param index the index
	 * @param element the element
	 * @return the v
	 */
	@Override
	public V set(int index, V element) {
		if (index < 0 || index > size) {
			return null;
		}
		setValue(index, element, SMALL_KEY);
		return element;
	}

	/**
	 * Adds the.
	 *
	 * @param index the index
	 * @param element the element
	 */
	@Override
	public void add(int index, V element) {
		int pos = hasKey(element);
		if (pos >= 0) {
			grow(size + 1);
			addKey(index, element, size + 1);
		}
	}

	/**
	 * Removes the.
	 *
	 * @param index the index
	 * @return the v
	 */
	@SuppressWarnings("unchecked")
	@Override
	public V remove(int index) {
		if (index < 0 || index > size) {
			return null;
		}
		return (V) removeByIndex(index, SMALL_KEY, this.index);
	}

	/**
	 * List iterator.
	 *
	 * @return the list iterator
	 */
	@Override
	public ListIterator<V> listIterator() {
		return new SimpleIterator<V>(this);
	}

	/**
	 * List iterator.
	 *
	 * @param index the index
	 * @return the list iterator
	 */
	@Override
	public ListIterator<V> listIterator(int index) {
		return new SimpleIterator<V>(this, index);
	}

	/**
	 * Sub list.
	 *
	 * @param fromIndex the from index
	 * @param toIndex the to index
	 * @return the speed list
	 */
	@SuppressWarnings("unchecked")
	public SpeedList<V> subList(int fromIndex, int toIndex) {
		return (SpeedList<V>) super.subList(fromIndex, toIndex);
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		this.elements = null;
		this.size = 0;
		this.index = 0;
	}
}
