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
		return cursor<(this.list.size() - 1);
	}

	@Override
	public Entry<K, V> next() {
		if(!hasNext()) {
			return null;
		}
		cursor++;
		
		this.currentEntry.setKey(this.list.getKeyByIndex(cursor));
		this.currentEntry.setValue(this.list.getValueByIndex(cursor));
		return this.currentEntry;
	}

	@Override
	public boolean hasPrevious() {
		return cursor>0;
	}

	@Override
	public Entry<K, V> previous() {
		cursor--;
		
		this.currentEntry.setKey(this.list.getKeyByIndex(cursor));
		this.currentEntry.setValue(this.list.getValueByIndex(cursor));
		return this.currentEntry;
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
		this.list.remove(cursor);
	}

	@Override
	public void set(Entry<K, V> e) {
		cursor = this.list.getPositionKey(e.getKey());

		this.currentEntry.setKey(e.getKey());
		this.currentEntry.setValue(e.getValue());
	}

	@Override
	public void add(Entry<K, V> e) {
		this.list.add(e.getKey(), e.getValue());
	}
}
