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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Entry<K, V>> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		this.map.clear();
	}
}
