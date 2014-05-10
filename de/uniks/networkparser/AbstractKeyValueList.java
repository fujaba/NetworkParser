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

public abstract class AbstractKeyValueList<K, V> extends AbstractList<AbstractKeyValueEntry<K, V>> implements Map<K, V> {
	public AbstractKeyValueList<K, V> withValues(Map<?, ?> map) {
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
		values.add(newObject);
		return value;
	}
	
	public AbstractKeyValueList<K, V> withValue(Object key, Object value) {
		if(!isAllowDuplicate()){			
			setValue(key, value);
		}
		values.add(getNewEntity().withValue(key, value));
		return this;
	}
	
	public AbstractKeyValueList<K, V> setValue(Object key, Object value) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				item.withToValue(value);
				return this;
			}
		}
		return this;
	}
	
	public abstract AbstractKeyValueEntry<K, V> getNewEntity();
	
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
	
	public V getValue(K key) {
		for(Iterator<AbstractKeyValueEntry<K, V>> i = iterator();i.hasNext();){
			AbstractKeyValueEntry<K, V> item = i.next();
			if(item.getKey().equals(key)){
				return item.getValue();
			}
		}
		return null;
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

	@Override
	public int getInt(int index) throws RuntimeException {
		V object = get(index);
		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
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
	
	@Override
	public double getDouble(int index) throws RuntimeException {
		V object = get(index);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}
	
	@Override
	public boolean getBoolean(int index) throws RuntimeException {
		V object = get(index);
		if (object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (object.equals(Boolean.TRUE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new RuntimeException("EntityList[" + index
				+ "] is not a boolean.");	}
	
	@Override
	public long getLong(int index) throws RuntimeException {
		V object = get(index);
		try {
			return object instanceof Number ? ((Number) object).longValue()
					: Long.parseLong((String) object);
		} catch (Exception e) {
			throw new RuntimeException("EntityList[" + index
					+ "] is not a number.");
		}
	}
	
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
		withValues(values);
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

	
}
