package de.uniks.networkparser.list;

import java.util.Set;

public class SimpleSet<V> extends AbstractList<V> implements Set<V> {
	@Override
	public AbstractList<V> getNewList(boolean keyValue) {
		return new SimpleSet<V>();
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
}
