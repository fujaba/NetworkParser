package de.uniks.networkparser.list;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.Comparator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class SortedList<V> extends SimpleList<V> {
	protected Comparator<V> cpr;
	
	@SuppressWarnings("unchecked")
	@Override
	public Comparator<Object> comparator() {
		if (this.cpr == null) {
			withComparator(new EntityComparator<V>().withColumn(
					EntityComparator.LIST).withDirection(SortingDirection.ASC));
		}
		return (Comparator<Object>) cpr;
	}
	
	public boolean isComparator() {
		return (this.cpr != null);
	}
	
	public SortedList<V> withComparator(Comparator<V> comparator) {
		this.cpr = comparator;
		return this;
	}

	public SortedList<V> withComparator(String column) {
		this.cpr = new EntityComparator<V>().withColumn(column).withDirection(
				SortingDirection.ASC);
		return this;
	}
	
	
	/**
	 * Returns a view of the portion of this map whose keys are greater than (or
	 * equal to, if {@code inclusive} is true) {@code fromKey}.
	 *
	 * @param fromElement
	 *            low endpoint of the keys in the returned map
	 * @param inclusive
	 *            {@code true} if the low endpoint is to be included in the
	 *            returned view
	 * @param <ST> the ContainerClass
	 * 
	 *             
	 * @return a view of the portion of this map whose keys are greater than (or
	 *         equal to, if {@code inclusive} is true) {@code fromKey}
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends SimpleList<V>> ST tailSet(V fromElement, boolean inclusive) {
		if(!isComparator()) {
			return null;
		}
		BaseItem newList = getNewList(false);
		// PRE WHILE
		int pos = 0;
		for (; pos < size(); pos++) {
			int compare = comparator().compare(get(pos), fromElement);
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

		// MUST COPY
		while (pos < size()) {
			copyEntity(newList, pos++);
		}
		return (ST) newList;
	}
	
	/**
	 * Returns a view of the portion of this map whose keys are less than (or
	 * equal to, if {@code inclusive} is true) {@code toKey}. The returned map
	 * is backed by this map, so changes in the returned map are reflected in
	 * this map, and vice-versa. The returned map supports all optional map
	 * operations that this map supports.
	 *
	 * <p>
	 * The returned map will throw an {@code IllegalArgumentException} on an
	 * attempt to insert a key outside its range.
	 *
	 * @param toElement
	 *            high endpoint of the keys in the returned map
	 * @param inclusive
	 *            {@code true} if the high endpoint is to be included in the
	 *            returned view
	 * @param <ST> the ContainerClass 
	 * 
	 * @return result a list with less item then the key
	 *
	 */
	@SuppressWarnings("unchecked")
	public <ST extends SimpleList<V>> ST headSet(V toElement, boolean inclusive) {
		if(!isComparator()) {
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
		if(!isComparator()) {
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
