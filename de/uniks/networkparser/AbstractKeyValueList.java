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
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.interfaces.FactoryEntity;

public abstract class AbstractKeyValueList<K, V> extends AbstractList<AbstractKeyValueEntry<K, V>> implements Map<K, V> {
	public AbstractKeyValueList<K, V> with(Map<?, ?> map) {
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
		if(!isAllowDuplicate()){			
			for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
				AbstractKeyValueEntry<K, V> item = i.next();
				if(item.getKey().equals(key)){
					item.withValue(value);
					return item.getValue();
				}
			}
		}
		AbstractKeyValueEntry<K, V> newObject = getNewEntity();
		newObject.withKey(key);
		newObject.withValue(value);
		super.add(newObject);
		return value;
	}
	
	public AbstractKeyValueList<K, V> withValue(Object key, Object value) {
		 if(!isAllowDuplicate()){			
			setValue(key, value);
			return this;
		}
		super.add(getNewEntity().withValue(key, value));
		return this;
	}
	
	public abstract AbstractKeyValueEntry<K, V> getNewEntity();
	
	/**
	 * Determine if the Entity contains a specific key.
	 * 
	 * @param key
	 *            A key string.
	 * @return true if the key exists in the Entity.
	 */
	@Override
	public boolean containsKey(Object key) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getValue().equals(value)){
				return true;
			}
		}
		return false;
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
		HashSet<K> list = new HashSet<K>();
		for(Iterator<AbstractKeyValueEntry<K, V>> i = values.listIterator();i.hasNext();){
			list.add(i.next().getKey());
		}
		return list;
	}
	

	/**
	 * Not Good because the values copy to new List
	 * 
	 * @return Collection of Values
	 */
	@Override
	public Collection<V> values() {
		ArrayList<V> list = new ArrayList<V>();
		for(Iterator<AbstractKeyValueEntry<K, V>> i = values.listIterator();i.hasNext();){
			list.add(i.next().getValue());
		}
		return list;
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
	public AbstractKeyValueList<K, V> increment(K key) throws RuntimeException{
		Object value = this.get(key);
		if (value == null) {
			setValue(key, 1);
			return this;
		}
		if (value instanceof Integer) {
			setValue(key, ((Integer) value).intValue() + 1);
			return this;
		}
		if (value instanceof Long) {
			setValue(key, ((Long) value).longValue() + 1);
			return this;
		}
		if (value instanceof Double) {
			setValue(key, ((Double) value).doubleValue() + 1);
			return this;
		}
		if (value instanceof Float) {
			setValue(key, ((Float) value).floatValue() + 1);
			return this;
		}
		if (value instanceof String) {
			try {
				setValue(key, ""+(getInt(getKeyIndex(key)) + 1));
				return this;
			} catch (Exception e) {
			}
			try {
				setValue(key, ""+(getDouble(getKeyIndex(key)) + 1));
				return this;
			} catch (Exception e) {
				throw new RuntimeException("Unable to increment [" + EntityUtil.quote(""+key) + "].");
			}
		}
		throw new RuntimeException("Unable to increment [" + EntityUtil.quote(""+key) + "].");			
	}
	
	public K getKey(V obj) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getValue().equals(obj)){
				return item.getKey();
			}
		}
		return null;
	}
	
	public Object getValue(Object key) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				return item.getValue();
			}
		}
		if(!(key instanceof String)){
			return null;
		}
		String keyString = ""+key;
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
							if(this instanceof FactoryEntity){
								AbstractEntityList<?> result = (AbstractEntityList<?>) ((FactoryEntity)this).getNewArray();
								AbstractList<?> items = (AbstractList<?>) child;
								for (int z = 0; z < items.size(); z++) {
									result.with(((AbstractKeyValueList<?,?>) items.get(z)).getValue(keyString
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
							return ((AbstractKeyValueList<?,?>) list.get(id)).getValue(keyString
									.substring(end + 1));
						}
					}
				} else {
					return ((AbstractKeyValueList<?, ?>) child).getValue(keyString.substring(end + 1));
				}
			}
		}
		return null;
	}

	/**
	 * Set a Value to Entity 
	 * With this Method it is possible to set a Value of a Set by using a [Number] or [L] for Last
	 * @param keyString
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public AbstractKeyValueList<K, V> setValue(Object key, Object value) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				item.withToValue(value);
				return this;
			}
		}
		
		String keyString = ""+key;
		
		
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
					if (child instanceof AbstractEntityList) {
						AbstractEntityList<Object> list = (AbstractEntityList<Object>) child;
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
						put((K)keyString.substring(0, len), (V)value);
					}
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractEntityList) {
						AbstractEntityList<?> list = (AbstractEntityList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							((AbstractKeyValueList<K,?>) list.get(id)).setValue(
									(K)keyString.substring(end + 1), value);
						}
					}
				} else {
					((AbstractKeyValueList<K,?>) child).setValue((K)keyString.substring(end + 1), value);
				}
			}
		} else {
			put((K)keyString.substring(0, len), (V)value);
		}
		return this;
	}
	
	
	@Override
	public V get(Object key) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				return item.getValue();
			}
		}
		return null;
	}
	
	public int getKeyIndex(Object key) {
		int pos=0;
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				return pos;
			}
			pos++;
		}
		return -1;
	}

	/**
	 * Get the object value associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return An object value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public AbstractKeyValueEntry<K, V> getMapEntity(int index) throws RuntimeException {
		AbstractKeyValueEntry<K, V> object = this.values.get(index);
		if (object == null) {
			throw new RuntimeException("EntityList[" + index + "] not found.");
		}
		return object;
	}
	

	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *            The name to be removed.
	 * @return The value that was associated with the name, or null if there was
	 *         no value.
	 */
	public boolean removeKey(K key) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				i.remove();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> values) {
		with(values);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new HashSet<Entry<K, V>>(values);
	}
	@Override
	public V remove(Object key) {
    	int index = getKeyIndex(key);
    	AbstractKeyValueEntry<K, V> result=null;
    	if(index>=0){
    		 result = values.remove(index);
    		 K beforeValue = null;
         	if(index>0){
     			beforeValue = values.get(index - 1).getKey();
     		}
         	fireProperty(result, null, beforeValue);
    	}
    	if(result!=null){
    		return result.getValue();
    	}
    	return null;
	}
	
    /**
     * Locate the Entity in the List
     * @param value Entity
     * @return the position of the Entity or -1
     */
    public int getIndexByKey(K value){
    	int pos=0;
    	for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
    		if(i.next().getKey().equals(value)){
    			return pos;
    		}
    		pos++;
    	}
    	return -1;
    }
	
	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(K key) throws RuntimeException {
		return getString(getIndexByKey(key));
	}
	
	/**
	 * Get the string associated with an index.
	 * 
	 * @param index
	 *            The index must be between 0 and length() - 1.
	 * @return A string value.
	 * @throws RuntimeException
	 *             If there is no value for the index.
	 */
	public String getString(K key, String defaultValue) {
		int pos = getIndexByKey(key);
		if(pos<0){
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
	public boolean getBoolean(K key) throws RuntimeException{
		return getBoolean(getIndexByKey(key));
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
	public double getDouble(K key) throws RuntimeException{
		return getDouble(getIndexByKey(key));
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
	public int getInt(K key) throws RuntimeException{
		return getInt(getIndexByKey(key));
	}

	@Override
	public Object getEntity(int index) throws RuntimeException {
		Object item = super.getEntity(index);
		if(item instanceof AbstractKeyValueEntry<?,?>){
			return ((AbstractKeyValueEntry<?,?>)item).getValue();
		}
		return null;
	}
}
