package de.uniks.networkparser.list;

import java.util.Comparator;

import de.uniks.networkparser.sort.EntityComparator;
import de.uniks.networkparser.sort.SortingDirection;

public class SortedList<V> extends SimpleList<V> {
	protected Comparator<V> cpr;
	public Comparator<V> comparator() {
		if (this.cpr == null) {
			withComparator(new EntityComparator<V>().withColumn(
					EntityComparator.LIST).withDirection(SortingDirection.ASC));
		}
		return cpr;
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
}
