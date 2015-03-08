package de.uniks.networkparser.list;

import java.util.List;

import de.uniks.networkparser.interfaces.BaseItem;

public class SimpleList<V> extends AbstractList<V> implements List<V> {
	
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleList<V>();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public SimpleList<V> clone() {
		return ((SimpleList<V>)getNewList(false)).init(this);
	}
	
	@SuppressWarnings("unchecked")
	public SimpleList<V> subList(int fromIndex, int toIndex) {
		return (SimpleList<V>) super.subList(fromIndex, toIndex);
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
}
