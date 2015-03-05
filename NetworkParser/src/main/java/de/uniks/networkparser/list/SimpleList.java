package de.uniks.networkparser.list;

import java.util.List;

public class SimpleList<V> extends AbstractList<V> implements List<V> {
	public SimpleList<V> getNewInstance() {
		return new SimpleList<V>();
	}
	
	@Override
	public SimpleList<V> clone() {
		return getNewInstance().init(this);
	}
	
	public SimpleList<V> subList(int fromIndex, int toIndex) {
		return (SimpleList<V>) super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
}
