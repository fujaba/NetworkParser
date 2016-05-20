package de.uniks.networkparser.list;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleIteratorSet<K,V> implements ListIterator<Entry<K, V>>{
	private SimpleKeyValueList<K, V> list;
	private SimpleEntity<K,V> currentEntry;
	private int cursor = -1;

	public SimpleIteratorSet(SimpleKeyValueList<K, V> list) {
		this.list = list;
		this.currentEntry = new SimpleEntity<K, V>();
	}

	@SuppressWarnings("unchecked")
	public SimpleIteratorSet(Object collection) {
		if(collection instanceof SimpleKeyValueList<?,?>) {
			this.list = (SimpleKeyValueList<K, V>) collection;
		} else if (collection instanceof Map<?,?>) {
			this.list = new SimpleKeyValueList<K,V>();
			this.list.withMap((Map<?,?>)collection);
		}
		this.currentEntry = new SimpleEntity<K, V>();
	}

	@Override
	public boolean hasNext() {
		return cursor<(this.list.size() - 1);
	}
	public void reset() {
		this.cursor = -1;
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
		this.list.removePos(cursor);
	}

	@Override
	public void set(Entry<K, V> e) {
		cursor = this.list.getPositionKey(e.getKey(), false);

		this.currentEntry.setKey(e.getKey());
		this.currentEntry.setValue(e.getValue());
	}

	@Override
	public void add(Entry<K, V> e) {
		this.list.add(e.getKey(), e.getValue());
	}
}
