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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BaseEntityList;
import de.uniks.networkparser.interfaces.StringItem;

public abstract class Entity implements BaseEntity, StringItem {
	/**
	 * The map where the Entity's properties are kept.
	 */
	protected ArrayEntryList<String> map = new ArrayEntryList<String>().withAllowDuplicate(false);
	private boolean visible = true;

	public Iterator<MapEntry<String>> iterator(){
		return map.iterator();
	}
	
	public Entity withValues(Map<?, ?> map) {
		if (map != null) {
			for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
				java.util.Map.Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
				Object item = mapEntry.getValue();
				if (item != null) {
					this.put(EntityUtil.wrap(mapEntry.getKey(), this)
							.toString(), EntityUtil.wrap(item, this));
				}
			}
		}
		return this;
	}

	/**
	 * Accumulate values under a key. It is similar to the put method except
	 * that if there is already an object stored under the key then a EntityList
	 * is stored under the key to hold all of the accumulated values. If there
	 * is already a EntityList, then the new value is appended to it. In
	 * contrast, the put method replaces the previous value.
	 * 
	 * If only one value is accumulated that is not a EntityList, then the
	 * result will be the same as using put. But if multiple values are
	 * accumulated, then the result will be like append.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object to be accumulated under the key.
	 * @return this.
	 */
	public Entity addToList(String key, Object value) {
		EntityUtil.testValidity(value);
		Object object = this.get(key);
		if (object == null) {
			this.put(key, value instanceof EntityList ? getNewArray()
					.add(value) : value);
		} else if (object instanceof EntityList) {
			((EntityList<?>) object).add(value);
		} else {
			this.put(key, getNewArray().with(object).with(value));
		}
		return this;
	}
	
	public Entity insert(int pos, String key, Object value){
		EntityUtil.testValidity(value);
		Object object = this.get(key);
		if (object != null) {
			this.put(key, value);
		}else{
			if(pos<0){pos=0;}
			if(pos>map.size()){pos=map.size();}
			
			Object[] list = map.entrySet().toArray();
			map.clear();
			int z=0;
			for(;z<list.length;z++){
				if(z==pos){
					map.put(key, value);
				}
				
				if(list[z] instanceof Entry<?, ?>){
					Object itemKey = ((Entry<?, ?>) list[z]).getKey();
					Object itemValue = ((Entry<?, ?>) list[z]).getValue();
					if(itemKey instanceof String){
						map.put(""+itemKey, itemValue);
					}
				}
			}
			if(z==pos){
				map.put(key, value);
			}
		}
		return this;
	}


	/**
	 * Get the value object associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The object associated with the key.
	 */
	public Object get(String key) {
		if (key == null) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * Get the boolean value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The truth.
	 * @throws RuntimeException
	 *             if the value is not a Boolean or the String "true" or
	 *             "false".
	 */
	public boolean getBoolean(String key) throws RuntimeException{
		Object object = this.get(key);
		if (object == null
				|| object.equals(Boolean.FALSE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("false"))) {
			return false;
		} else if (object.equals(Boolean.TRUE)
				|| (object instanceof String && ((String) object)
						.equalsIgnoreCase("true"))) {
			return true;
		}
		throw new RuntimeException("Entity[" + EntityUtil.quote(key)
				+ "] is not a Boolean.");
	}

	/**
	 * Get the double value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The numeric value.
	 * @throws RuntimeException
	 *             if the key is not found or if the value is not a Number
	 *             object and cannot be converted to a number.
	 */
	public double getDouble(String key) throws RuntimeException{
		Object object = this.get(key);
		try {
			return object instanceof Number ? ((Number) object).doubleValue()
					: Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new RuntimeException("Entity[" + EntityUtil.quote(key)
					+ "] is not a number.");
		}
	}

	/**
	 * Get the int value associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return The integer value.
	 * @throws RuntimeException
	 *             if the key is not found or if the value cannot be converted
	 *             to an integer.
	 */
	public int getInt(String key) throws RuntimeException{
		Object object = this.get(key);
		try {
			return object instanceof Number ? ((Number) object).intValue()
					: Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new RuntimeException("Entity[" + EntityUtil.quote(key)
					+ "] is not an int.");
		}
	}

	/**
	 * Get an array of field names from a Entity.
	 * 
	 * @return An array of field names, or null if there are no names.
	 */
	public static String[] getNames(Entity jo) {
		int length = jo.size();
		if (length == 0) {
			return null;
		}
		Iterator<String> keys = jo.keys();
		String[] names = new String[length];
		int i = 0;
		while (keys.hasNext()) {
			names[i] = keys.next();
			i += 1;
		}
		return names;
	}

	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 */
	public String getString(String key) {
		Object object = this.get(key);
		if (object instanceof String) {
			return (String) object;
		} else if (object instanceof Entity) {
			return object.toString();
		}
		return null;
	}

	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @param defaultValue
	 *            A defaultValue string.
	 * @return A string which is the value or defaultValue
	 */
	public String getString(String key, String defaultValue) {
		String object = getString(key);
		if (object!=null) {
			return object;
		}
		return defaultValue;
	}

	/**
	 * Determine if the Entity contains a specific key.
	 * 
	 * @param key
	 *            A key string.
	 * @return true if the key exists in the Entity.
	 */
	public boolean has(String key) {
		return map.containsKey(key);
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
	public Entity increment(String key) throws RuntimeException{
		Object value = this.get(key);
		if (value == null) {
			this.put(key, 1);
			return this;
		}
		if (value instanceof Integer) {
			this.put(key, ((Integer) value).intValue() + 1);
			return this;
		}
		if (value instanceof Long) {
			this.put(key, ((Long) value).longValue() + 1);
			return this;
		}
		if (value instanceof Double) {
			this.put(key, ((Double) value).doubleValue() + 1);
			return this;
		}
		if (value instanceof Float) {
			this.put(key, ((Float) value).floatValue() + 1);
			return this;
		}
		if (value instanceof String) {
			try {
				this.put(key, ""+(getInt(key) + 1));
				return this;
			} catch (Exception e) {
			}
			try {
				this.put(key, ""+(getDouble(key) + 1));
				return this;
			} catch (Exception e) {
				throw new RuntimeException("Unable to increment [" + EntityUtil.quote(key) + "].");
			}
		}
		throw new RuntimeException("Unable to increment [" + EntityUtil.quote(key) + "].");			
	}

	/**
	 * Get an enumeration of the keys of the Entity.
	 * 
	 * @return An iterator of the keys.
	 */
	public Iterator<String> keys() {
		return map.keySet().iterator();
	}

	/**
	 * Get the number of keys stored in the Entity.
	 * 
	 * @return The number of keys in the Entity.
	 */
	public int size() {
		if (this.map == null) {
			return 0;
		}
		return this.map.size();
	}

	@Override
	public abstract EntityList<Object> getNewArray();
	
	/**
	 * Produce a EntityList containing the names of the elements of this Entity.
	 * 
	 * @return A EntityList containing the key strings, or null if the Entity is
	 *         empty.
	 */
	public EntityList<Object> names() {
		EntityList<Object> ja = getNewArray();
		Iterator<String> keys = this.keys();
		while (keys.hasNext()) {
			ja.add(keys.next());
		}
		return ja.size() == 0 ? null : ja;
	}

	/**
	 * Put a key/boolean pair in the Entity.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A boolean which is the value.
	 * @return this.
	 */
	public Entity put(String key, boolean value) {
		this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
		return this;
	}

	/**
	 * Put a key/value pair in the Entity, where the value will be a EntityList
	 * which is produced from a Collection.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A Collection value.
	 * @return this.
	 */
	public Entity put(String key, Collection<?> value) {
		put(key, (Object) getNewArray().withValues(value));
		return this;
	}

	/**
	 * Put a key/double pair in the Entity.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A double which is the value.
	 * @return this.
	 */
	public Entity put(String key, double value) {
		this.put(key, new Double(value));
		return this;
	}

	/**
	 * Put a key/int pair in the Entity.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            An int which is the value.
	 * @return this.
	 */
	public Entity put(String key, int value) {
		this.put(key, Integer.valueOf(value));
		return this;
	}

	/**
	 * Put a key/long pair in the Entity.
	 * 
	 * @param key
	 *            A key string.
	 * @param value
	 *            A long which is the value.
	 * @return this.
	 */
	public Entity put(String key, long value) {
		this.put(key, Long.valueOf(value));
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
	public void put(String key, Object value) {
		if (key == null) {
			throw new RuntimeException("Null key.");
		}
		if (value != null) {
			EntityUtil.testValidity(value);
			map.put(key, value);
		} else {
			this.remove(key);
		}
	}
	
	

	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *            The name to be removed.
	 * @return The value that was associated with the name, or null if there was
	 *         no value.
	 */
	public Object remove(String key) {
		return map.remove(key);
	}

	public Object getValue(String key) {
		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < key.length(); len++) {
			char temp = key.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < key.length(); end++) {
					temp = key.charAt(end);
					if (key.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == key.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == key.length()) {
			id = -1;
		}

		Object child = get(key.substring(0, len));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof EntityList) {
						EntityList<?> list = (EntityList<?>) child;
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
					if (child instanceof EntityList) {
						if (end == len + 2) {
							// Get List
							BaseEntityList result = getNewArray();
							BaseEntityList items = (BaseEntityList) child;
							for (int z = 0; z < items.size(); z++) {
								result.add(((Entity) items.get(z)).getValue(key
										.substring(end + 1)));
							}
							return result;
						}

						EntityList<?> list = (EntityList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return ((Entity) list.get(id)).getValue(key
									.substring(end + 1));
						}
					}
				} else {
					return ((Entity) child).getValue(key.substring(end + 1));
				}
			}
		}
		return null;
	}

	/**
	 * Set a Value to Entity 
	 * With this Method it is possible to set a Value of a Set by using a [Number] or [L] for Last
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public Entity setValue(String key, Object value) {
		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < key.length(); len++) {
			char temp = key.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < key.length(); end++) {
					temp = key.charAt(end);
					if (key.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == key.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == key.length()) {
			id = -1;
		}

		Object child = get(key.substring(0, len));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof EntityList) {
						EntityList<Object> list = (EntityList<Object>) child;
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
						remove(key.substring(0, len));
					} else {
						put(key.substring(0, len), value);
					}
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof EntityList) {
						EntityList<?> list = (EntityList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							((Entity) list.get(id)).setValue(
									key.substring(end + 1), value);
						}
					}
				} else {
					((Entity) child).setValue(key.substring(end + 1), value);
				}
			}
		} else {
			put(key.substring(0, len), value);
		}
		return this;
	}

	@Override
	public Entity withVisible(boolean value) {
		this.visible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}
}
