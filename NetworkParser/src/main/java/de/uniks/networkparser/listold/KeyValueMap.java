package de.uniks.networkparser.interfaces;

import java.util.Map;

public interface KeyValueMap<K, V> extends Map<K, V>, Iterable<K> {
	public boolean addEntity(K key, V value);
	public int addValue(int pos, V value);
}
