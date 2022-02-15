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

/**
 * The Class SimpleMapEntry.
 *
 * @author Stefan
 * @param <K> the key type
 * @param <V> the value type
 */
public class SimpleMapEntry<K, V> extends SimpleEntity<K, V> {
	
	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleMapEntry<K, V>();
	}

	/**
	 * With key item.
	 *
	 * @param key the key
	 * @return the simple map entry
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SimpleMapEntry<K, V> withKeyItem(Object key) {
		withKey((K) key);
		return this;
	}

	/**
	 * With value item.
	 *
	 * @param value the value
	 * @return the simple map entry
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SimpleMapEntry<K, V> withValueItem(Object value) {
		this.withValue((V) value);
		return this;
	}
}
