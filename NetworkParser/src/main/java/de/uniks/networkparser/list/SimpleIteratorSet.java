package de.uniks.networkparser.list;

import java.util.ListIterator;
import java.util.Map.Entry;

public class SimpleIteratorSet<K,V> implements ListIterator<Entry<K, V>>{
	private SimpleKeyValueList<K, V> list;

	public SimpleIteratorSet(SimpleKeyValueList<K, V> list) {
		this.list = list;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Entry<K, V> next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Entry<K, V> previous() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nextIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int previousIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set(Entry<K, V> e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Entry<K, V> e) {
		// TODO Auto-generated method stub
		
	}
}
