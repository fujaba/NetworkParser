package de.uniks.networkparser.list;

import java.util.List;

public class SimpleList<V> extends AbstractList<V> implements List<V> {
	public SimpleList<V> getNewArray() {
		return new SimpleList<V>();
	}
	public SimpleList<V> getNewInstance() {
		return new SimpleList<V>();
	}
}
