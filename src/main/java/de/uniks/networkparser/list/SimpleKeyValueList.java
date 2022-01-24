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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SimpleKeyValueList<K, V> extends AbstractArray<K> implements Map<K, V>, Iterable<Entry<K, V>> {
	private Comparator<Object> cpr;

	public SimpleKeyValueList() {
		super();
		withFlag(MAP);
	}

	/**
	 * Set a Value to Entity With this Method it is possible to set a Value of a Set
	 * by using a [Number] or [L] for Last
	 *
	 * @param key   the Key to add
	 * @param value the Value to add
	 * @return Itself
	 */
	@SuppressWarnings("unchecked")
	public SimpleKeyValueList<K, V> setValueItem(Object key, Object value) {
		int pos = indexOf(key);
		if (pos >= 0) {
			this.setValue(pos, (V) value);
			return this;
		}
		if (key instanceof String == false) {
			return this;
		}
		String keyString = "" + key;

		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < keyString.length(); len++) {
			char temp = keyString.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < keyString.length(); end++) {
					temp = keyString.charAt(end);
					if (keyString.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == keyString.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == keyString.length()) {
			id = -1;
		}

		Object child = get(keyString.substring(0, len));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						AbstractList<Object> list = (AbstractList<Object>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							if (value == null) {
								list.remove(id);
							} else {
								list.set(id, value);
							}
						}
					}
				} else {
					if (value == null) {
						remove(keyString.substring(0, len));
					} else {
						put((K) keyString.substring(0, len), (V) value);
					}
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							((SimpleKeyValueList<K, ?>) list.get(id)).setValueItem((K) keyString.substring(end + 1),
									value);
						}
					}
				} else {
					if (child instanceof Map<?, ?>) {
						((SimpleKeyValueList<K, ?>) child).setValueItem((K) keyString.substring(end + 1), value);
					} else {
						put((K) keyString.substring(0, len), (V) value);
					}
				}
			}
		} else {
			put((K) keyString.substring(0, len), (V) value);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public V setValue(int pos, V value) {
		return (V) super.setValue(pos, value, SMALL_VALUE);
	}

	public void copyEntity(SimpleKeyValueList<K, V> target, int pos) {
		if (target != null)
			target.withKeyValue(this.get(pos), this.getValueByIndex(pos));
	}

	@Override
	public Set<K> keySet() {
		SimpleSet<K> item = new SimpleSet<K>();
		if (isComplex(size) && this.elements != null) {
			item.init((Object[]) this.elements[SMALL_KEY], size, this.index);
		} else if (this.elements != null) {
			item.init(this.elements, size, this.index);
		}
		return item;
	}

	public Iterator<K> keyIterator() {
		return keySet().iterator();
	}

	/**
	 * Get the boolean value associated with an index. The string values "true" and
	 * "false" are converted to boolean.
	 *
	 * @param key The Value
	 * @return The truth.
	 *
	 * @throws SimpleException If there is no value for the index or if the value is
	 *                         not convertible to boolean.
	 */
	public boolean getBoolean(K key) throws SimpleException {
		Object value = get(key);

		if (Boolean.FALSE.equals(value) || (value instanceof String && "false".equalsIgnoreCase((String) value))) {
			return false;
		} else if (Boolean.TRUE.equals(value) || (value instanceof String && "true".equalsIgnoreCase((String) value))) {
			return true;
		}
		throw new SimpleException("SimpleKeyValueList is not a boolean.", this);
	}

	/**
	 * Get the double value associated with an index.
	 *
	 * @param key the Value
	 * @return the value.
	 * @throws SimpleException If the key is not found or if the value cannot be
	 *                         converted to a number.
	 */
	public double getDouble(K key) throws SimpleException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new SimpleException("SimpleKeyValueList is not a number.", this);
		}
	}

	/**
	 * Get the double value associated with an index.
	 *
	 * @param key          the Value
	 * @param defaultValue DefaultValue
	 * @return the value.
	 */
	public double getDouble(K key, double defaultValue) {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble((String) object);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Get the int value associated with an index.
	 *
	 * @param key The Value
	 * @return The value.
	 * @throws SimpleException If the key is not found or if the value is not a
	 *                         number.
	 */
	public int getInt(K key) throws SimpleException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).intValue() : Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new SimpleException("SimpleKeyValueList is not a number.", this);
		}
	}

	public int getInt(K key, int defaultValue) {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).intValue() : Integer.parseInt((String) object);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * Get the long value associated with an index.
	 *
	 * @param key The Value
	 * @return The value.
	 * @throws SimpleException If the key is not found or if the value cannot be
	 *                         converted to a number.
	 */
	public long getLong(K key) throws SimpleException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).longValue() : Long.parseLong((String) object);
		} catch (Exception e) {
			throw new SimpleException("SimpleKeyValueList is not a number.", this);
		}
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key The Value
	 * @return A string value.
	 */
	public String getString(K key) {
		Object object = get(key);
		if (object == null) {
			return "";
		}
		return object.toString();
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key          The index must be between 0 and length() - 1.
	 * @param defaultValue The defaultValue
	 *
	 * @return A string value.
	 */
	public String getString(K key, String defaultValue) {
		if (key == null) {
			return defaultValue;
		}
		int pos = indexOf(key);
		if (pos < 0) {
			return defaultValue;
		}
		V value = getValueByIndex(pos);
		return value == null ? defaultValue : value.toString();
	}

	/**
	 * Increment a property of a Entity. If there is no such property, create one
	 * with a value of 1. If there is such a property, and if it is an Integer,
	 * Long, Double, or Float, then add one to it.
	 *
	 * @param key A key string.
	 * @return this.
	 */
	public SimpleKeyValueList<K, V> increment(K key) {
		Object value = this.get(key);
		if (value == null) {
			setValueItem(key, 1);
			return this;
		}
		if (value instanceof Integer) {
			setValueItem(key, ((Integer) value).intValue() + 1);
			return this;
		}
		if (value instanceof Long) {
			setValueItem(key, ((Long) value).longValue() + 1);
			return this;
		}
		if (value instanceof Double) {
			setValueItem(key, ((Double) value).doubleValue() + 1);
			return this;
		}
		if (value instanceof Float) {
			setValueItem(key, ((Float) value).floatValue() + 1);
			return this;
		}
		if (value instanceof String) {
			try {
				Double newValue = Double.parseDouble((String) value);
				if (newValue.intValue() == newValue) {
					setValueItem(key, "" + (newValue.intValue() + 1));
				} else {
					setValueItem(key, "" + (newValue + 1));
				}
			} catch (Exception e) {
			}
		}
		return this;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new SimpleKeyValueList<K, V>();
	}

	@Override
	public SimpleKeyValueList<K, V> withList(Collection<?> values) {
		super.withList(values);
		return this;
	}

	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		if (values.length % 2 == 0) {
			for (int i = 0; i < values.length; i += 2) {
				withKeyValue(values[i], values[i + 1]);
			}
			return true;
		}
		super.add(values);
		return true;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleKeyValueList<K, V>> ST with(K key, V value) {
		add(key, value);
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleKeyValueList<K, V>> ST withGroup(V value, K... keys) {
		if (keys != null && value != null) {
			for (Object key : keys) {
				add(key, value);
			}
		}
		return (ST) this;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleKeyValueList<K, V>> ST withMultIndex(K[] keys, V value) {
		if (keys == null) {
			return (ST) this;
		}
		for (K key : keys) {
			add(key, value);
		}
		return (ST) this;
	}

	public boolean add(K key, V value) {
		int pos = hasKey(key);
		if (pos >= 0) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}

	public boolean add(int pos, K key, V value) {
		if (hasKey(key) >= 0) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}

	@Override
	public V put(K key, V value) {
		int pos = hasKeyAndPos(key);
		if (pos < 0) {
			return null;
		}
		if (pos + this.index >= size) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		} else if (isComparator() && pos == 0 && comparator().compare(getKeyByIndex(0), key) >= 0) {
			super.addKeyValue(pos, key, value);
		} else {
			super.setValue(pos + this.index, value, SMALL_VALUE);
		}
		return value;
	}

	@Override
	public boolean containsKey(Object key) {
		return super.contains(key);
	}

	public int getPositionValue(Object o) {
		return getPosition(o, SMALL_VALUE, false);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			return false;
		}
		if (isComplex(size)) {
			return getPositionValue(value) >= 0;
		}
		Object[] items = (Object[]) elements[SMALL_VALUE];
		for (int i = 0; i < size; i++)
			if (value.equals(items[i]))
				return true;
		return false;
	}

	public int indexOfValue(Object value) {
		if (elements == null || value == null) {
			return -1;
		}
		if ((this.flag & BIDI) != BIDI || size <= MINHASHINGSIZE) {
			return search((Object[]) elements[SMALL_VALUE], value);
		}
		return getPositionValue(value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public K getKeyByIndex(int index) {
		return (K) super.getKeyByIndex(index);
	}

	@SuppressWarnings("unchecked")
	public V getValueByIndex(int index) {
		return (V) super.getByIndex(SMALL_VALUE, index + this.index, size);
	}

	@Override
	protected Object removeByIndex(int index, int offset, int oldIndex) {
		if (index < 0) {
			return null;
		}
		super.removeItem(index, SMALL_KEY, oldIndex);
		return super.removeByIndex(index, SMALL_VALUE, oldIndex);
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		int index = this.indexOf(key);
		return (V) removeByIndex(index, SMALL_KEY, this.index);
	}

	@SuppressWarnings("unchecked")
	public V removePos(int pos) {
		return (V) removeByIndex(pos, SMALL_KEY, index);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		if (values == null) {
			return;
		}
		for (java.util.Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	public SimpleKeyValueList<K, V> withMap(Map<?, ?> value) {
		if (value == null) {
			return this;
		}
		for (java.util.Map.Entry<?, ?> entry : value.entrySet()) {
			withKeyValue(entry.getKey(), entry.getValue());
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		int pos = indexOf(key);
		if (pos >= 0) {
			return (V) getByIndex(SMALL_VALUE, pos + this.index, size);
		}
		return null;
	}

	@Override
	public Collection<V> values() {
		SimpleList<V> item = new SimpleList<V>();
		if (elements == null) {
			return item;
		}
		item.init((Object[]) elements[SMALL_VALUE], size, this.index);
		return item;
	}

	public Object[] valuesArrayIntern() {
		if (elements == null) {
			return new Object[0];
		}
		return (Object[]) elements[SMALL_VALUE];
	}

	public SimpleKeyValueList<K, V> withKeyValue(Object key, Object value) {
		int pos = hasKeyAndPos(key);
		if (pos < 0) {
			return this;
		}
		if (pos == size || getByIndex(SMALL_KEY, pos, size) != key) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		} else {
			super.setValue(pos, value, SMALL_VALUE);
		}
		return this;
	}

	public SimpleKeyValueList<K, V> addToKeyValue(Object key, Number value) {
		int pos = hasKeyAndPos(key);
		if (pos < 0) {
			return this;
		}
		if (pos == size || getByIndex(SMALL_KEY, pos, size) != key) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		} else {
			Object oldValue = getByIndex(SMALL_VALUE, pos, size);
			if (oldValue instanceof Integer) {
				super.setValue(pos, ((Integer) oldValue) + (Integer) value, SMALL_VALUE);
			}
		}
		return this;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new SimpleEntrySet<K, V>(this);
	}

	public K getKey(V value) {
		int pos = indexOfValue(value);
		if (pos < 0) {
			return null;
		}
		return getKeyByIndex(pos);
	}

	/**
	 * Init The Colleciton with short String
	 * 
	 * @param keyValue  The init KeyValue String Key:Value,Key:Value ...
	 * @param types Class from Value, Class from keys
	 * @return This Component
	 */
	public SimpleKeyValueList<K, V> withKeyValueString(String keyValue, Class<?>... types) {
		if(keyValue == null || keyValue.length()<1) {
			return this;
		}
		
		int pos = 0, start;
		
		Object key, value;
		
		Class<?> keyType = String.class;
		Class<?> valueType= String.class;
		if(types != null && types.length>1) {
			valueType = types[0];
			if(types.length>1) {
				keyType = types[1];
			}
		}
		char item;
		do {
			start = pos;
			/* Get String As Key */
			while (pos < keyValue.length()) {
				item = keyValue.charAt(pos);
				if (item == ':') {
					break;
				}
				pos++;
			}
			key = keyValue.substring(start, pos).trim();
			pos++;
			/* Get Integer As Value */
			start = pos;
			while (pos < keyValue.length()) {
				item = keyValue.charAt(pos);
				if (item == ',') {
					break;
				}
				pos++;
			}
			value = keyValue.substring(start, pos).trim();
			pos++;
			if (valueType == Integer.class) {
				value = Integer.valueOf("" + value);
			}
			if (keyType == Integer.class) {
				key = Integer.valueOf("" + key);
			}
			withKeyValue(key, value);
		} while (pos < keyValue.length());
		return this;
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new SimpleIteratorSet<K, V>(this);
	}

	@Override
	public void replaceAllValues(Object key, String search, String replace) {
		if (key == null) {
			return;
		}
		for (int i = 0; i < this.size(); i++) {
			Object value = getValueByIndex(i);
			if (value instanceof AbstractArray<?>) {
				((AbstractArray<?>) value).replaceAllValues(key, search, replace);
			} else {
				Object itemKey = getKeyByIndex(i);
				if (key.equals(itemKey)) {
					if (search == null) {
						this.setValue(i, replace, SMALL_VALUE);
					} else if (value instanceof String) {
						String stringValue = (String) value;
						if (stringValue.indexOf(search) >= 0) {
							stringValue = stringValue.replaceAll(search, replace);
							this.setValue(i, stringValue, SMALL_VALUE);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectCondition) {
			ObjectCondition condition = (ObjectCondition) obj;
			return condition.update(this);
		}
		return super.equals(obj);
	}

	public SimpleKeyValueList<K, V> withComparator(Comparator<Object> comparator) {
		this.cpr = comparator;
		return this;
	}

	@Override
	public boolean isComparator() {
		return (this.cpr != null);
	}

	@Override
	public Comparator<Object> comparator() {
		return cpr;
	}

	public Object[][] toTable() {
		Object[][] table = new Object[size()][2];
		for (int i = 0; i < size(); i++) {
			table[i][0] = this.getKeyByIndex(i);
			table[i][1] = this.getValueByIndex(i);
		}
		return table;
	}
}
