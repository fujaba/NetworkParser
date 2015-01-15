package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.event.SimpleMapEntry;
import de.uniks.networkparser.interfaces.FactoryEntity;

public abstract class AbstractKeyValueList<K, V> extends AbstractList<K>
		implements Map<K, V>, Iterable<K> {

	public AbstractKeyValueList() {
		this.items.withEntitySize(2);
	}

	public AbstractKeyValueList<K, V> withMap(Map<?, ?> map) {
		if (map != null) {
			for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
				java.util.Map.Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
				Object item = mapEntry.getValue();
				Object key = mapEntry.getKey();
				if (item != null) {
					this.withValue(key, item);
				}
			}
		}
		return this;
	}

	/**
	 * Put a key/value pair in the Entity. If the value is null, then the key
	 * will be removed from the Entity if it is present.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, EntityList, Entity, Long or
	 *            String object.
	 * @return this.
	 */
	@Override
	public V put(K key, V value) {
		if(!isAllowEmptyValue() && value == null) {
			return null;
		}
		if (!isAllowDuplicate()) {
			int pos = getPositionKey(key);
			if (pos >= 0) {
				if (this.hashTableValues != null) {
					this.hashTableValues[pos] = value;
					pos = transformIndex(pos, key, this.hashTableKeys,
							this.keys);
				}
				return this.values.set(pos, value);
			}
		}

		addEntity(key, value);

		return value;
	}

	public void add(int index, K key, V value) {
		if (!contains(key)) {
			addKey(index, key);
			addValue(index, value);
			K beforeValue = null;
			if (index > 0) {
				beforeValue = get(index - 1);
				fireProperty(null, key, beforeValue, value);
			}
		}
	}

	public boolean addEntity(K key, V value) {
		if (key == null)
			return false;
		if (cpr != null) {
			for (int i = 0; i < size(); i++) {
				int result = comparator().compare(get(i), key);
				if (result >= 0) {
					if (!isAllowDuplicate() && get(i) == key) {
						return false;
					}
					addKey(i, key);
					addValue(i, value);
					K beforeElement = null;
					if (i > 0) {
						beforeElement = this.keys.get(i - 1);
					}
					fireProperty(null, key, beforeElement, value);
					return true;
				}
			}
		}

		if (!isAllowDuplicate()) {
			if (this.contains(key)) {
				return false;
			}
		}

		if (addKey(-1, key) < 0) {
			return false;
		}
		addValue(-1, value);
		K beforeElement = null;
		if (size() > 1) {
			beforeElement = this.keys.get(size() - 1);
		}
		fireProperty(null, key, beforeElement, value);
		return true;
	}

	@SuppressWarnings("unchecked")
	protected boolean addEntity(AbstractEntity<?, ?> item) {
		return addEntity((K) item.getKey(), (V) item.getValue());
	}

	/**
	 * Add a new KeyValue Item.
	 *
	 * @param key
	 *            The new Key
	 * @param value
	 *            The new Vlaue
	 * @return AbstractKeyValueList Instance
	 */
	@SuppressWarnings("unchecked")
	public AbstractKeyValueList<K, V> withValue(Object key, Object value) {
		if (!isAllowDuplicate()) {
			setValueItem(key, value);
			return this;
		}
		addEntity((K) key, (V) value);
		return this;
	}

	public boolean add(K newKey, V newValue) {
		return addEntity(newKey, newValue);
	}

	/**
	 * Determine if the Entity contains a specific key.
	 *
	 * @param key
	 *            A key string.
	 * @return true if the key exists in the Entity.
	 */
	@Override
	public boolean containsKey(Object key) {
		return super.contains(key);
	}

	public boolean has(K key) {
		return super.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.getPositionValue(value) >= 0;
	}

	public int getPositionValue(Object o) {
		if (this.hashTableValues != null) {
			int hashKey = hashKey(o.hashCode(), hashTableValues.length);
			while (true) {
				Object value = hashTableValues[hashKey];
				if (value == null)
					return -1;
				if (value.equals(o))
					return hashKey;
				hashKey = (hashKey + entitySize) % hashTableValues.length;
			}
		}

		// search from the end as in models we frequently ask for elements that
		// have just been added to the end
		int pos = this.values.size() - 1;
		for (ListIterator<V> i = values.listIterator(values.size()); i
				.hasPrevious();) {
			if (i.previous().equals(o)) {
				return pos;
			}
			pos--;
		}
		return -1;
	}

	/**
	 * Get an enumeration of the keys of the Entity.
	 *
	 * @return An iterator of the keys.
	 */
	public Iterator<K> keys() {
		return keySet().iterator();
	}

	/**
	 * @return the keySet of all Keys
	 */
	@Override
	public HashSet<K> keySet() {
		return new HashSet<K>(this.keys);
	}

	/**
	 * Increment a property of a Entity. If there is no such property, create
	 * one with a value of 1. If there is such a property, and if it is an
	 * Integer, Long, Double, or Float, then add one to it.
	 *
	 * @param key
	 *            A key string.
	 * @return this.
	 * @throws RuntimeException
	 *             If there is already a property with this name that is not an
	 *             Integer, Long, Double, or Float.
	 */
	public AbstractKeyValueList<K, V> increment(K key) throws RuntimeException {
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
				setValueItem(key, "" + (getInt(getIndex(key)) + 1));
				return this;
			} catch (Exception e) {
			}
			try {
				setValueItem(key, "" + (getDouble(getIndex(key)) + 1));
				return this;
			} catch (Exception e) {
				throw new RuntimeException("Unable to increment ["
						+ EntityUtil.quote("" + key) + "].");
			}
		}
		throw new RuntimeException("Unable to increment ["
				+ EntityUtil.quote("" + key) + "].");
	}

	/**
	 * Get the Key of a Value.
	 *
	 * @param value
	 *            The Value
	 * @return The Key
	 */
	public K getKey(V value) {
		int index = transformIndex(getPositionValue(value), value,
				this.hashTableValues, this.values);
		if (index >= 0) {
			return this.keys.get(index);
		}
		return null;
	}

	/**
	 * Get the Value of a Key.
	 *
	 * @param key
	 *            The Key
	 * @return The Value
	 */
	public V getValue(K key) {
		int index = transformIndex(getPositionKey(key), key,
				this.hashTableKeys, this.keys);
		if (index >= 0) {
			return this.values.get(index);
		}
		return null;
	}

	public Object getValueItem(Object key) {
		int pos = getIndex(key);
		if (pos >= 0) {
			return this.values.get(pos);
		}
		if (!(key instanceof String)) {
			return null;
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
					if (child instanceof AbstractList<?>) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return list.get(id);
						}
					}
				} else {
					return child;
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractList) {
						if (end == len + 2) {
							// Get List
							if (this instanceof FactoryEntity) {
								AbstractList<?> result = (AbstractList<?>) ((FactoryEntity) this)
										.getNewArray();
								AbstractList<?> items = (AbstractList<?>) child;
								for (int z = 0; z < items.size(); z++) {
									result.with(((AbstractKeyValueList<?, ?>) items
											.get(z)).getValueItem(keyString
											.substring(end + 1)));
								}
								return result;
							}
						}
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return ((AbstractKeyValueList<?, ?>) list.get(id))
									.getValueItem(keyString.substring(end + 1));
						}
					}
				} else {
					return ((AbstractKeyValueList<?, ?>) child)
							.getValueItem(keyString.substring(end + 1));
				}
			}
		}
		return null;
	}

	/**
	 * Set a Value to Entity With this Method it is possible to set a Value of a
	 * Set by using a [Number] or [L] for Last
	 *
	 * @param key
	 *            the Key to add
	 * @param value
	 *            the Value to add
	 * @return Itself
	 */
	@SuppressWarnings("unchecked")
	public AbstractKeyValueList<K, V> setValueItem(Object key, Object value) {
		int pos = getIndex(key);
		if (pos >= 0) {
			V oldValue = this.values.get(pos);
			int position = getPositionValue(oldValue);
			this.values.set(pos, (V) value);
			if (this.hashTableValues != null && position >= 0) {
				this.hashTableValues[position] = value;
			}
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
					if (child instanceof AbstractList) {
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
					if (child instanceof AbstractList) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							((AbstractKeyValueList<K, ?>) list.get(id))
									.setValueItem(
											(K) keyString.substring(end + 1),
											value);
						}
					}
				} else {
					((AbstractKeyValueList<K, ?>) child).setValueItem(
							(K) keyString.substring(end + 1), value);
				}
			}
		} else {
			put((K) keyString.substring(0, len), (V) value);
		}
		return this;
	}

	@Override
	public V get(Object key) {
		int pos = getIndex(key);
		if (pos >= 0) {
			return this.values.get(pos);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		with(values);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		HashSet<Entry<K, V>> result = new HashSet<Map.Entry<K, V>>();
		for (int i = 0; i < size(); i++) {
			result.add(new SimpleMapEntry<K, V>().withKeyItem(this.getKey(i))
					.withValueItem(this.getValue(i)));
		}
		return result;
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(K key) throws RuntimeException {
		return getString(getIndex(key));
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param key
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            The defaultValue
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(K key, String defaultValue) {
		int pos = getIndex(key);
		if (pos < 0) {
			return defaultValue;
		}
		return getString(pos);
	}

	/**
	 * Get the boolean value associated with a key.
	 *
	 * @param key
	 *            A key.
	 * @return The truth.
	 * @throws RuntimeException
	 *             if the value is not a Boolean or the String "true" or
	 *             "false".
	 */
	public boolean getBoolean(K key) throws RuntimeException {
		return getBoolean(getIndex(key));
	}

	/**
	 * Get the double value associated with a key.
	 *
	 * @param key
	 *            A key.
	 * @return The numeric value.
	 * @throws RuntimeException
	 *             if the key is not found or if the value is not a Number
	 *             object and cannot be converted to a number.
	 */
	public double getDouble(K key) throws RuntimeException {
		return getDouble(getIndex(key));
	}

	/**
	 * Get the int value associated with a key.
	 *
	 * @param key
	 *            A key.
	 * @return The integer value.
	 * @throws RuntimeException
	 *             if the key is not found or if the value cannot be converted
	 *             to an integer.
	 */
	public int getInt(K key) throws RuntimeException {
		return getInt(getIndex(key));
	}

	public Object getValue(int index) throws RuntimeException {
		if (index < 0 || index > this.keys.size()) {
			return null;
		}
		return this.values.get(index);
	}

	@Override
	public Collection<V> values() {
		return values;
	}

	@Override
	protected Object getItem(int index) {
		return getValue(index);
	}

	public void copyEntity(AbstractKeyValueList<K, V> target, int pos) {
		target.add(this.get(pos), this.values.get(pos));
	}

	protected V removeItem(Object key) {
		int index = removeItemByObject(key);
		V graphNode = this.values.get(index);
		this.values.remove(index);
		return graphNode;
	}

	@Override
	public V remove(Object key) {
		return removeItem(key);
	}

	@Override
	protected K removeItemByIndex(int index) {
		K result = super.removeItemByIndex(index);
		if (result != null) {
			this.values.remove(index);
		}
		return result;
	}

	protected int addValue(int pos, V value) {
		if (pos == -1) {
			this.values.add(value);
			pos = this.values.size();
			this.hashTableAddKey(value, pos);
			return pos;
		}
		this.values.add(pos, value);
		return -1;
	}
	
	public AbstractKeyValueList<K, V> withAllowEmpty(Boolean value) {
		this.items.withAllowEmptyValue(value);
		return this;
	}
}
