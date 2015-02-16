package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BaseList;
import de.uniks.networkparser.interfaces.FactoryEntity;

public class SimpleKeyValueList<K, V> extends AbstractList<K> implements Map<K, V>, FactoryEntity, Iterable<K>, BaseList {

	@Override
	public byte initFlag() {
		return MAP;
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

	private void setValue(int pos, V value) {
		super.setValue(pos, value, SMALL_VALUE);
	}

	public void copyEntity(SimpleKeyValueList<K, V> target, int pos) {
		target.add(this.get(pos), this.getValue(pos));
	}
	
	@Override
	public Set<K> keySet() {
		if(isBig()) {
			return new SimpleSet<K>().init((Object[])this.elements[SMALL_KEY]);
		}
		return new SimpleSet<K>().init(this.elements);
	}
	
	public Iterator<K> keyIterator() {
		return keySet().iterator();
	}

	public SimpleKeyValueList<K, V> getNewInstance() {
		return new SimpleKeyValueList<K, V>();
	}
	
	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 *
	 * @param key
	 *            The Value 
	 * @return The truth.
	 * @throws RuntimeException
	 *             If there is no value for the index or if the value is not
	 *             convertible to boolean.
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
	 * @param key  The Value
	 * @return The value.
	 * @throws RuntimeException
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
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
	 *             If the key is not found or if the value is not a number.
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
	 *             If the key is not found or if the value cannot be converted
	 *             to a number.
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
	 *             If there is no value for the index.
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
	 * @param key
	 *            The index must be between 0 and length() - 1.
	 * @param defaultValue
	 *            The defaultValue
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(K key, String defaultValue) {
		if(key==null){
			return defaultValue;
		}
		int pos = indexOf(key);
		if (pos < 0) {
			return defaultValue;
		}
		return getValue(pos).toString();
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
	public BaseList getNewArray() {
		return new SimpleKeyValueList<K, V>();
	}
	
	public SimpleKeyValueList<K, V> withList(Collection<?> values) {
		super.withList(values);
		return this;
	}
	
	public SimpleKeyValueList<K, V> withAll(Object... values) {
		super.withAll(values);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends SimpleKeyValueList<K, V>> ST with(K key, V value) {
		add(key, value);
		return (ST)this;
	}
	
	public boolean add(K key, V value) {
		int pos = checkKey(key);
		if(pos>=0) {
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}
	
	public boolean add(int pos, K key, V value) {
		if(checkKey(key)>=0) {
			super.addKeyValue(pos, key, value);
			return true;
		}
		return false;
	}

	
	@Override
	public V put(K key, V value) {
		int pos = checkKey(key);
		if(pos>=0) {
			super.addKeyValue(pos, key, value);
			return value;
		}
		return null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return super.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		if(value==null){
			return false;
		}
		if(isBig()) {
    		return getPositionValue(value)>=0;
    	}
		Object[] items = (Object[]) elements[SMALL_VALUE];
		for (int i = 0; i < size; i++)
            if (value.equals(items[i]))
                return true;
		return false;
	}

	public SimpleKeyValueList<K, V> withList(Map<?, ?> map) {
		if (map != null) {
			for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
				java.util.Map.Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
				Object item = mapEntry.getValue();
				Object key = mapEntry.getKey();
				if (item != null) {
					this.withKeyValue(key, item);
				}
			}
		}
		return this;
	}

	@Override
	public Iterator<K> iterator() {
		return new SimpleIterator<K>(this);
	}
	
	//Methode for Type Casting
	@Override
	public SimpleKeyValueList<K, V> withAllowDuplicate(boolean allowDuplicate) {
		super.withAllowDuplicate(allowDuplicate);
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public K getKey(int index) {
		return (K) super.getKey(index);
	}


	@Override
	@SuppressWarnings("unchecked")
	public V getValue(int index) {
		return (V) super.getValue(index);
	}
	
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		int pos = super.removeByObject(key);
		if(pos>=0) {
			return (V) super.removeByIndex(pos, SMALL_VALUE);
		}
		return null;
	}
	
	@Override
	public BaseItem getNewObject() {
		return new SimpleEntity<K, V>();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		for (java.util.Map.Entry<? extends K, ? extends V> entry : values.entrySet()) {
			add(entry.getKey(), entry.getValue());
        }
	}
	
	public SimpleKeyValueList<K, V> withMap(Map<?, ?> value) {
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
			return (V) super.getValue(pos);
		}
		return null;
	}
	
	@Override
	public Collection<V> values() {
		return new SimpleList<V>().init((Object[])elements[SMALL_VALUE]);
	}

	public SimpleKeyValueList<K, V> withKeyValue(Object key, Object value) {
		int pos = checkKey(key);
		if(pos>=0) {
			super.addKeyValue(pos, key, value);
		}
		return this;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getKeyByObject(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
}
