package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleIteratorSet<K, V> implements ListIterator<Entry<K, V>> {
	private SimpleKeyValueList<K, V> list;
	private SimpleEntity<K, V> currentEntry;
	private int cursor = -1;

	public SimpleIteratorSet(SimpleKeyValueList<K, V> list) {
		this.list = list;
		this.currentEntry = new SimpleEntity<K, V>();
	}

	@SuppressWarnings("unchecked")
	public SimpleIteratorSet(Object collection) {
		if (collection instanceof SimpleKeyValueList<?, ?>) {
			this.list = (SimpleKeyValueList<K, V>) collection;
		} else if (collection instanceof Map<?, ?>) {
			this.list = new SimpleKeyValueList<K, V>();
			this.list.withMap((Map<?, ?>) collection);
		}
		this.currentEntry = new SimpleEntity<K, V>();
	}

	@Override
	public boolean hasNext() {
		return cursor < (this.list.size() - 1);
	}

	public void reset() {
		this.cursor = -1;
	}

	@Override
	public Entry<K, V> next() {
		if (hasNext() == false) {
			return null;
		}
		cursor++;

		this.currentEntry.setKey(this.list.getKeyByIndex(cursor));
		this.currentEntry.setValue(this.list.getValueByIndex(cursor));
		return this.currentEntry;
	}

	@Override
	public boolean hasPrevious() {
		return cursor > 0;
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
