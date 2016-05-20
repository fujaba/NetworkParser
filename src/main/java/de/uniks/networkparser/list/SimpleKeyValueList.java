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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.interfaces.BaseItem;

public class SimpleKeyValueList<K, V> extends AbstractArray<K> implements Map<K, V> {
	public SimpleKeyValueList() {
		super();
		withFlag(MAP);
	}

	/**
	 * Set a Value to Entity With this Method it is possible to set a Value of a
	 * Set by using a [Number] or [L] for Last
	 *
	 * @param key
	 *			the Key to add
	 * @param value
	 *			the Value to add
	 * @return Itself
	 */
	@SuppressWarnings("unchecked")
	public SimpleKeyValueList<K, V> setValueItem(Object key, Object value) {
		int pos = indexOf(key);
		if (pos >= 0) {
//			V oldValue = this.getValue(pos);
			this.setValue(pos, (V) value);
			return this;
		}
		if (!(key instanceof String)) {
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
							((SimpleKeyValueList<K, ?>) list.get(id))
									.setValueItem(
											(K) keyString.substring(end + 1),
											value);
						}
					}
				} else {
					((SimpleKeyValueList<K, ?>) child).setValueItem(
							(K) keyString.substring(end + 1), value);
				}
			}
		} else {
			put((K) keyString.substring(0, len), (V) value);
		}
		return this;
	}

	public void setValue(int pos, V value) {
		super.setValue(pos, value, SMALL_VALUE);
	}

	public void copyEntity(SimpleKeyValueList<K, V> target, int pos) {
		if(target != null)
			target.withKeyValue(this.get(pos), this.getValueByIndex(pos));
	}

	@Override
	public Set<K> keySet() {
		SimpleSet<K> item = new SimpleSet<K>();
		if(isComplex(size) && this.elements!=null) {
			item.init((Object[])this.elements[SMALL_KEY], size, this.index);
		}else if(this.elements!=null) {
			item.init(this.elements, size, this.index);
		}
		return item;
	}

	public Iterator<K> keyIterator() {
		return keySet().iterator();
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 *
	 * @param key					The Value
	 * @return 						The truth.
	 *
	 * @throws RuntimeException		If there is no value for the index or if the value is not convertible to boolean.
	 */
	public boolean getBoolean(K key) throws RuntimeException {
		Object value = get(key);

		if (Boolean.FALSE.equals(value)
				|| (value instanceof String && ((String) value)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (Boolean.TRUE.equals(value)
				|| (value instanceof String && ((String) value)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new RuntimeException("SimpleKeyValueList is not a boolean.");
	}

	/**
	 * Get the double value associated with an index.
	 *
	 * @param key	the Value
	 * @return		the value.
	 * @throws RuntimeException
	 *			 If the key is not found or if the value cannot be converted
	 *			 to a number.
	 */
	public double getDouble(K key) throws RuntimeException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("SimpleKeyValueList is not a number.");
		}
	}

	/**
	 * Get the int value associated with an index.
	 *
	 * @param key  The Value
	 * @return The value.
	 * @throws RuntimeException
	 *			 If the key is not found or if the value is not a number.
	 */
	public int getInt(K key) throws RuntimeException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new RuntimeException("SimpleKeyValueList is not a number.");
		}
	}

	/**
	 * Get the long value associated with an index.
	 *
	 * @param key  The Value
	 * @return The value.
	 * @throws RuntimeException
	 *			 If the key is not found or if the value cannot be converted
	 *			 to a number.
	 */
	public long getLong(K key) throws RuntimeException {
		Object object = get(key);
		try {
			return object instanceof Number ? ((Number) object).longValue()
					: Long.parseLong((String) object);
		} catch (Exception e) {
			throw new RuntimeException("SimpleKeyValueList is not a number.");
		}
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key  The Value
	 * @return A string value.
	 * @throws RuntimeException
	 *			 If there is no value for the index.
	 */
	public String getString(K key) throws RuntimeException {
		Object object = get(key);
		if(object==null){
			return "";
		}
		return object.toString();
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key					The index must be between 0 and length() - 1.
	 * @param defaultValue			The defaultValue
	 *
	 * @return 						A string value.
	 *
	 * @throws RuntimeException		If there is no value for the index.
	 */
	public String getString(K key, String defaultValue) {
		if(key==null){
			return defaultValue;
		}
		int pos = indexOf(key);
		if (pos < 0) {
			return defaultValue;
		}
		return getValueByIndex(pos).toString();
	}

	/**
	 * Increment a property of a Entity. If there is no such property, create
	 * one with a value of 1. If there is such a property, and if it is an
	 * Integer, Long, Double, or Float, then add one to it.
	 *
	 * @param key
	 *			A key string.
	 * @return this.
	 * @throws RuntimeException
	 *			 If there is already a property with this name that is not an
	 *			 Integer, Long, Double, or Float.
	 */
	public SimpleKeyValueList<K, V> increment(K key) throws RuntimeException {
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
				setValueItem(key, "" + (getInt(key) + 1));
				return this;
			} catch (Exception e) {
			}
			try {
				setValueItem(key, "" + (getDouble(key) + 1));
				return this;
			} catch (Exception e) {
				throw new RuntimeException("Unable to increment ["
						+ EntityUtil.quote("" + key) + "].");
			}
		}
		throw new RuntimeException("Unable to increment ["
				+ EntityUtil.quote("" + key) + "].");
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

	public SimpleKeyValueList<K, V> with(Object... values) {
		if(values == null) {
			return this;
		}
		if (values.length % 2 == 0) {
			for (int i = 0; i < values.length; i += 2) {
				withKeyValue(values[i], values[i + 1]);
			}
			return this;
		}
		super.with(values);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <ST extends SimpleKeyValueList<K, V>> ST with(K key, V value) {
		add(key, value);
		return (ST)this;
	}

	public boolean add(K key, V value) {
		int pos = hasKey(key);
		if(pos>=0) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}

	public boolean add(int pos, K key, V value) {
		if(hasKey(key)>=0) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}


	@Override
	public V put(K key, V value) {
		int pos = hasKeyAndPos(key);
		if(pos<0) {
			return null;
		}
		if(pos==size || getByIndex(SMALL_KEY, pos, size) != key) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		}else {
			super.setValue(pos, value, SMALL_VALUE);
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
		if(value==null){
			return false;
		}
		if(isComplex(size)) {
			return getPositionValue(value)>=0;
		}
		Object[] items = (Object[]) elements[SMALL_VALUE];
		for (int i = 0; i < size; i++)
			if (value.equals(items[i]))
				return true;
		return false;
	}

	public int indexOfValue(Object value){
		if(elements==null || value == null){
			return -1;
		}
		if((this.flag & BIDI)!=BIDI || size<=MINHASHINGSIZE) {
			return search((Object[]) elements[SMALL_VALUE], value);
		}
		   return getPositionValue(value);
	}

	@Override
	public Iterator<K> iterator() {
		return new SimpleIterator<K>(this);
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

	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		int index = indexOf(key);
		if (index < 0) {
			return null;
		}
		int oldIndex = this.index;
		removeItem(index, SMALL_KEY, oldIndex);
		return (V) removeByIndex(index, SMALL_VALUE, oldIndex);
	}

	@SuppressWarnings("unchecked")
	public V removePos(int pos) {
		if (pos < 0) {
			return null;
		}
		int oldIndex = this.index;
		removeItem(pos, SMALL_KEY, oldIndex);
		return (V) removeByIndex(pos, SMALL_VALUE, oldIndex);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		if(values==null) {
			return;
		}
		for (java.util.Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
	}

	public SimpleKeyValueList<K, V> withMap(Map<?, ?> value) {
		if(value==null) {
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
		if(pos>=0) {
			return (V) super.getByIndex(SMALL_VALUE, pos + this.index, size);
		}
		return null;
	}

	@Override
	public Collection<V> values() {
		SimpleList<V> item = new SimpleList<V>();
		if(elements == null) {
			return item;
		}
		item.init((Object[])elements[SMALL_VALUE], size, this.index);
		return item;
	}

	public Object[] valuesArrayIntern() {
		if(elements == null) {
			return new Object[0];
		}
		return (Object[]) elements[SMALL_VALUE];
	}

	public SimpleKeyValueList<K, V> withKeyValue(Object key, Object value) {
		int pos = hasKeyAndPos(key);
		if(pos<0) {
			return this;
		}
		if(pos==size || getByIndex(SMALL_KEY, pos, size) != key) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		}else {
			super.setValue(pos, value, SMALL_VALUE);
		}
		return this;
	}
	public SimpleKeyValueList<K, V> addToKeyValue(Object key, Number value) {
		int pos = hasKeyAndPos(key);
		if(pos<0) {
			return this;
		}
		if(pos==size || getByIndex(SMALL_KEY, pos, size) != key) {
			grow(size + 1);
			super.addKeyValue(pos, key, value);
		}else {
			Object oldValue = getByIndex(SMALL_VALUE, pos, size);
			if(oldValue instanceof Integer) {
				super.setValue(pos, ((Integer)oldValue) + (Integer)value, SMALL_VALUE);
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
		if(pos<0) {
			return null;
		}
		return getKeyByIndex(pos);
	}

	/**
	 *  Init The Colleciton with short String
	 * @param keyValue The init KeyValue String Key:Value,Key:Value ...
	 * @param valueType Class from Value
	 * @return This Component
	 */
	public SimpleKeyValueList<K, V> withKeyValueString(String keyValue, Class<?> valueType) {
		int pos=0, start;
		String key, value;
		char item;
		do{
			start = pos;
			// Get String As Key
			while(pos<keyValue.length()) {
				item = keyValue.charAt(pos);
				if(item == ':') {
					break;
				}
				pos++;
			}
			key = keyValue.substring(start, pos);
			pos++;
			// Get Integer As Value
			start=pos;
			while(pos<keyValue.length()) {
				item = keyValue.charAt(pos);
				if(item==',') {
					break;
				}
				pos++;
			}
			value = keyValue.substring(start, pos);
			pos++;
			if(valueType == Integer.class) {
				withKeyValue(key, Integer.valueOf(value));
			} else {
				withKeyValue(key, value);
			}
		}while(pos<keyValue.length());
		return this;
	}

	@Override
	public AbstractArray<K> without(Object... values) {
		if(values == null) {
			return this;
		}
		for(Object item : values) {
			remove(item);
		}
		return this;
	}
}
