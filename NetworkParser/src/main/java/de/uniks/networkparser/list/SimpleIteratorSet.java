package de.uniks.networkparser.list;

import java.util.ListIterator;
import java.util.Map.Entry;

public class SimpleIteratorSet<K,V> implements ListIterator<Entry<K, V>>{
	private SimpleKeyValueList<K, V> list;
	private SimpleEntity<K,V> currentEntry;
	private int cursor;

	public SimpleIteratorSet(SimpleKeyValueList<K, V> list) {
		this.list = list;
		this.currentEntry = new SimpleEntity<K, V>();
	}

	@Override
	public boolean hasNext() {
		return cursor<this.list.size();
	}

	@Override
	public Entry<K, V> next() {
		cursor++;
		
		this.currentEntry.setKey(this.list.getKey(cursor));
		this.currentEntry.setValue(this.list.getValue(cursor));
		return this.currentEntry;
	}

	@Override
	public boolean hasPrevious() {
		return cursor>0;
	}

	@Override
	public Entry<K, V> previous() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nextIndex() {
		return cursor + 1;
	}

	@Override
	public int previousIndex() {
		return cursor - 1;
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
