package de.uniks.networkparser.listold;

import java.util.Set;

public interface SimpleInterface<V> extends Set<V>{
	public boolean addEntity(V newValue);
	public int removeItemByObject(Object key);
	public int getPositionKey(Object o);
	public Object set(int index, V element);
	public V get(int index);
	public void add(int index, V element);
}
