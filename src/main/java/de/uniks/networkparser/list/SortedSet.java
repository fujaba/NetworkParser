package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.util.Comparator;

import de.uniks.networkparser.interfaces.BaseItem;

public class SortedSet<V> extends SimpleSet<V> {
	protected Comparator<V> cpr;

	public SortedSet(boolean comparator) {
		if (comparator) {
			comparator();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Comparator<Object> comparator() {
		if (this.cpr == null) {
			withComparator(
					new EntityComparator<V>().withColumn(EntityComparator.VALUES).withDirection(SortingDirection.ASC));
		}
		return (Comparator<Object>) cpr;
	}

	@Override
	public boolean isComparator() {
		return true;
	}

	public SortedSet<V> withComparator(Comparator<V> comparator) {
		this.cpr = comparator;
		return this;
	}

	public SortedSet<V> withComparator(String column) {
		this.cpr = new EntityComparator<V>().withColumn(column).withDirection(SortingDirection.ASC);
		return this;
	}

	/**
	 * Returns a view of the portion of this map whose keys are greater than (or
	 * equal to, if {@code inclusive} is true) {@code fromKey}.
	 *
	 * @param fromElement low endpoint of the keys in the returned map
	 * @param inclusive   {@code true} if the low endpoint is to be included in the
	 *                    returned view
	 * @param             <ST> the ContainerClass
	 *
	 *
	 * @return a view of the portion of this map whose keys are greater than (or
	 *         equal to, if {@code inclusive} is true) {@code fromKey}
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends SimpleSet<V>> ST tailSet(V fromElement, boolean inclusive) {
		if (!isComparator()) {
			return null;
		}
		BaseItem newList = getNewList(false);
		// PRE WHILE
		int pos = 0;
		for (; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), fromElement);
			if (compare == 0) {
				if (inclusive) {
					copyEntity(newList, pos++);
				}
				break;
			} else if (compare > 0) {
				copyEntity(newList, pos++);
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
	 * Returns a view of the portion of this map whose keys are less than (or equal
	 * to, if {@code inclusive} is true) {@code toKey}. The returned map is backed
	 * by this map, so changes in the returned map are reflected in this map, and
	 * vice-versa. The returned map supports all optional map operations that this
	 * map supports.
	 *
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an attempt
	 * to insert a key outside its range.
	 *
	 * @param toElement high endpoint of the keys in the returned map
	 * @param inclusive {@code true} if the high endpoint is to be included in the
	 *                  returned view
	 * @param           <ST> the ContainerClass
	 *
	 * @return result a list with less item then the key
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends SimpleSet<V>> ST headSet(V toElement, boolean inclusive) {
		if (!isComparator()) {
			return null;
		}
		BaseItem newList = getNewList(false);
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

	public V higher(V toElement) {
		if (!isComparator()) {
			return null;
		}
		for (int pos = 0; pos < size(); pos++) {
			V item = get(pos);
			int compare = comparator().compare(item, toElement);
			if (compare > 0) {
				return item;
			}
		}
		return null;
	}
}
