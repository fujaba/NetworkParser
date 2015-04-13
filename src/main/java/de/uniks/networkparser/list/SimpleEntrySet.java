package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleEntrySet<K, V> implements Set<Entry<K, V>>{
	private SimpleKeyValueList<K, V> map;

	public SimpleEntrySet(SimpleKeyValueList<K, V> value) {
		this.map = value;
	}

	public Map<K, V> getMap() {
		return map;
	}
	
	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.map.contains(o);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new SimpleIteratorSet<K,V>(map);
	}

	@Override
	public Object[] toArray() {
		return this.map.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.map.toArray(a);
	}

	@Override
	public boolean add(Entry<K, V> e) {
		return this.map.add(e.getKey(), e.getValue());
	}

	@Override
	public boolean remove(Object o) {
		return this.map.removeByObject(o)>=0;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.map.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> collection) {
		boolean result=true;
		for(Entry<K, V> item : collection) {
			result = result && this.map.add(item.getKey(), item.getValue());
		}
		return result;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return this.map.retainAll(collection);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return this.map.removeAll(collection);
	}

	@Override
	public void clear() {
		this.map.clear();
	}
}
