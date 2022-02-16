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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

/**
 * SimpleEntity is a Simple KeyValue - Object.
 *
 * @author Stefan Lindel
 *
 * @param <K> Key-Element
 * @param <V> Value Element
 */
public class SimpleEntity<K, V> implements BaseItem, Entry<K, V>, SendableEntityCreator, SendableEntityCreatorNoIndex {
	/** Constant for KEY. */
	public static final String PROPERTY_KEY = "key";
	/** Constant for VALUE. */
	public static final String PROPERTY_VALUE = "value";

	/** The Variable of Key. */
	private K key;
	/** The Variable of Value. */
	private V value;

	/**
	 * With.
	 *
	 * @param <ST> the generic type
	 * @param key the key
	 * @param value the value
	 * @return the st
	 */
	@SuppressWarnings("unchecked")
	public <ST extends SimpleEntity<K, V>> ST with(K key, V value) {
		this.key = key;
		this.value = value;
		return (ST) this;
	}

	/**
	 * With key item.
	 *
	 * @param key the key
	 * @return the simple entity
	 */
	@SuppressWarnings("unchecked")
	public SimpleEntity<K, V> withKeyItem(Object key) {
		withKey((K) key);
		return this;
	}

	/**
	 * With value item.
	 *
	 * @param value the value
	 * @return the simple entity
	 */
	@SuppressWarnings("unchecked")
	public SimpleEntity<K, V> withValueItem(Object value) {
		withValue((V) value);
		return this;
	}

	/**
	 * add the Values of the map to AbstractKeyValueEntry&lt;K, V&gt;.
	 *
	 * @param collection a map of key-values
	 * @return Itself
	 */
	public SimpleEntity<K, V> with(Map<Object, Object> collection) {
		if (collection != null) {
			Iterator<Entry<Object, Object>> i = collection.entrySet().iterator();
			while (i.hasNext()) {
				Entry<Object, Object> e = i.next();
				Object value = e.getValue();
				if (value != null) {
					setKeyIntern(e.getKey());
					setValueIntern(e.getValue());
				}
			}
		}
		return this;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public K getKey() {
		return key;
	}

	/**
	 * Gets the key string.
	 *
	 * @return the key string
	 */
	public String getKeyString() {
		if (key instanceof String) {
			return "" + key;
		}
		return null;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Override
	public V getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value
	 * @return the v
	 */
	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

	@SuppressWarnings("unchecked")
	private void setValueIntern(Object value) {
		this.value = (V) value;
	}

	@SuppressWarnings("unchecked")
	private void setKeyIntern(Object key) {
		this.key = (K) key;
	}

	/**
	 * Sets the key.
	 *
	 * @param value the value
	 * @return the k
	 */
	public K setKey(K value) {
		this.key = value;
		return value;
	}

	/**
	 * Equals.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Map.Entry) ) {
			return false;
		}
		Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
		return eq(key, e.getKey()) && eq(value, e.getValue());
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return ((key == null) ? 0 : key.hashCode()) ^ ((value == null) ? 0 : value.hashCode());
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return key + "=" + value;
	}

	/**
	 * With key.
	 *
	 * @param key the key
	 * @return the simple entity
	 */
	public SimpleEntity<K, V> withKey(K key) {
		this.key = key;
		return this;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the simple entity
	 */
	public SimpleEntity<K, V> withValue(V value) {
		this.value = value;
		return this;
	}

	private static boolean eq(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_KEY, PROPERTY_VALUE};
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		Entry<?, ?> obj = ((Entry<?, ?>) entity);
		if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
			return obj.getKey();
		} else if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return obj.getValue();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof SimpleEntity) {
			SimpleEntity<?, ?> entry = (SimpleEntity<?, ?>) entity;
			if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
				entry.withKeyItem(value);
				return true;
			} else if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
				if (value instanceof Entry<?, ?>) {
					Object map = entry.getValue();
					if (map == null) {
						map = new SimpleKeyValueList<String, Object>();
					}
					SimpleKeyValueList<?, ?> mapValue = (SimpleKeyValueList<?, ?>) map;
					mapValue.withKeyValue(((Entry<?, ?>) value).getKey(), ((Entry<?, ?>) value).getValue());
					entry.withValueItem(map);
				} else {
					entry.withValueItem(value);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleEntity<K, V>();
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		if (values.length == 2) {
			withKeyItem(values[0]);
			withValueItem(values[1]);
		}
		return true;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleEntity<K, V>();
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(Object key) {
		if (PROPERTY_KEY.equals(key)) {
			return this.key;
		}
		if (PROPERTY_VALUE.equals(key)) {
			return value;
		}
		return null;
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		if (key != null) {
			return 1;
		}
		return 0;
	}
}
