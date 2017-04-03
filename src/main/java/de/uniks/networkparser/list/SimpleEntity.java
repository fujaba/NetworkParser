package de.uniks.networkparser.list;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
 * @param <K>
 *			Key-Element
 * @param <V>
 *			Value Element
 */
public class SimpleEntity<K, V> implements BaseItem, Entry<K, V>,

		SendableEntityCreator, SendableEntityCreatorNoIndex {
	/** Constant for KEY. */
	public static final String PROPERTY_KEY = "key";
	/** Constant for VALUE. */
	public static final String PROPERTY_VALUE = "value";
	/** Propertys for Values. */
	private final String[] properties = new String[] {PROPERTY_KEY,
			PROPERTY_VALUE };
	/** The Variable of Key. */
	private K key;
	/** The Variable of Value. */
	private V value;

	@SuppressWarnings("unchecked")
	public <ST extends SimpleEntity<K, V>> ST with(K key, V value) {
		this.key = key;
		this.value = value;
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public SimpleEntity<K, V> withKeyItem(Object key) {
		withKey((K) key);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SimpleEntity<K, V> withValueItem(Object value) {
		withValue((V) value);
		return this;
	}

	/**
	 * add the Values of the map to AbstractKeyValueEntry&lt;K, V&gt;
	 *
	 * @param collection
	 *			a map of key-values
	 * @return Itself
	 */
	public SimpleEntity<K, V> with(Map<Object, Object> collection) {
		if (collection != null) {
			Iterator<Entry<Object, Object>> i = collection.entrySet()
					.iterator();
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

	@Override
	public K getKey() {
		return key;
	}

	public String getKeyString() {
		if (key instanceof String) {
			return "" + key;
		}
		throw new RuntimeException("Key is not a String <" + key + ">");
	}

	@Override
	public V getValue() {
		return value;
	}

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

	public K setKey(K value) {
		this.key = value;
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
		return eq(key, e.getKey()) && eq(value, e.getValue());
	}

	@Override
	public int hashCode() {
		return ((key == null) ? 0 : key.hashCode())
				^ ((value == null) ? 0 : value.hashCode());
	}

	@Override
	public String toString() {
		return key + "=" + value;
	}

	public SimpleEntity<K, V> withKey(K key) {
		this.key = key;
		return this;
	}

	public SimpleEntity<K, V> withValue(V value) {
		this.value = value;
		return this;
	}

	private static boolean eq(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

	@Override
	public String[] getProperties() {
		return properties;
	}

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

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
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
					mapValue.withKeyValue(((Entry<?,?>) value).getKey(), ((Entry<?,?>) value).getValue());
					entry.withValueItem(map);
				} else {
					entry.withValueItem(value);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleEntity<K, V>();
	}

	@Override
	public BaseItem with(Object... values) {
		if(values == null) {
			return this;
		}
		if(values.length==2) {
			withKeyItem(values[0]);
			withValueItem(values[1]);
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleEntity<K, V>();
	}

	public Object getValue(Object key) {
		if(PROPERTY_KEY.equals(key)) {
			return this.key;
		}
		if(PROPERTY_VALUE.equals(key)) {
			return value;
		}
		return null;
	}

	@Override
	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}
	
	@Override
	public int size() {
		if(key != null) {
			return 1;
		}
		return 0;
	}
}
