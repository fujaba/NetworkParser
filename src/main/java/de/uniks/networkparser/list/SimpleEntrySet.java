package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The Class SimpleEntrySet.
 *
 * @author Stefan
 * @param <K> the key type
 * @param <V> the value type
 */
public class SimpleEntrySet<K, V> implements Set<Entry<K, V>> {
	private SimpleKeyValueList<K, V> map;

	/**
	 * Instantiates a new simple entry set.
	 *
	 * @param value the value
	 */
	public SimpleEntrySet(SimpleKeyValueList<K, V> value) {
		this.map = value;
	}

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public Map<K, V> getMap() {
		return map;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		if (map == null) {
			return 0;
		}
		return this.map.size();
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	@Override
	public boolean isEmpty() {
		if (map == null) {
			return true;
		}
		return this.map.isEmpty();
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean contains(Object o) {
		if (map == null) {
			return false;
		}
		return this.map.contains(o);
	}

	/**
	 * Iterator.
	 *
	 * @return the iterator
	 */
	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new SimpleIteratorSet<K, V>(map);
	}

	/**
	 * To array.
	 *
	 * @return the object[]
	 */
	@Override
	public Object[] toArray() {
		if (map == null) {
			return null;
		}
		/* Change to DebugInfo */
		int size = this.map.size();
		Object[] info = new Object[this.map.size()];
		for (int i = 0; i < size; i++) {
			ObjectMapEntry mapEntry = new ObjectMapEntry();
			mapEntry.withKey(this.map.getKeyByIndex(i));
			mapEntry.withValue(this.map.getValueByIndex(i));
			info[i] = mapEntry;
		}
		return info;
	}

	/**
	 * To array.
	 *
	 * @param <T> the generic type
	 * @param a the a
	 * @return the t[]
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		if (map == null) {
			return null;
		}
		return this.map.toArray(a);
	}

	/**
	 * Adds the.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	@Override
	public boolean add(Entry<K, V> e) {
		if (map == null) {
			return false;
		}
		return this.map.add(e.getKey(), e.getValue());
	}

	/**
	 * Removes the.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean remove(Object o) {
		if (map == null) {
			return false;
		}
		return this.map.removeByObject(o) >= 0;
	}

	/**
	 * Contains all.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		if (map == null) {
			return false;
		}
		return this.map.containsAll(c);
	}

	/**
	 * Adds the all.
	 *
	 * @param collection the collection
	 * @return true, if successful
	 */
	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> collection) {
		boolean result = true;
		if (collection == null) {
			return result;
		}
		for (Entry<K, V> item : collection) {
			result = result && this.map.add(item.getKey(), item.getValue());
		}
		return result;
	}

	/**
	 * Retain all.
	 *
	 * @param collection the collection
	 * @return true, if successful
	 */
	@Override
	public boolean retainAll(Collection<?> collection) {
		if (map == null) {
			return true;
		}
		return this.map.retainAll(collection);
	}

	/**
	 * Removes the all.
	 *
	 * @param collection the collection
	 * @return true, if successful
	 */
	@Override
	public boolean removeAll(Collection<?> collection) {
		if (map == null) {
			return false;
		}
		return this.map.removeAll(collection);
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		if (map == null) {
			return;
		}
		this.map.clear();
	}
}
