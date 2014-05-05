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
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.ArrayEntryList;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.BaseEntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public abstract class AbstractMapEntry<K, V> implements Entry<K, V>, SendableEntityCreator, SendableEntityCreatorNoIndex, BaseEntity{
	public static final String PROPERTY_KEY = "key";
	public static final String PROPERTY_VALUE = "value";
	private final String[] properties = new String[] { PROPERTY_KEY, PROPERTY_VALUE };
	protected K key;
	private V value;

	public AbstractMapEntry<K, V> with(K key, V value) {
		this.key = key;
		this.value = value;
		return this;
	}

	@Override
	public K getKey() {
		return key;
	}
	
	public String getKeyString() {
		if(key instanceof String){
			return ""+key;
		}
		throw new RuntimeException("Key is not a String <"+key+">");
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
	
	public Object setKey(K value) {
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
	
	public AbstractMapEntry<K, V> withKey(K key) {
		this.key = key;
		return this;
	}

	public AbstractMapEntry<K, V> withValue(V value) {
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
		if(entity instanceof MapEntry){
			MapEntry entry = (MapEntry) entity;
			if (PROPERTY_KEY.equalsIgnoreCase(attribute)) {
				entry.setKey(""+value);
				return true;
			} else if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
				if (value instanceof Entry<?, ?>) {
					Object map = entry.getValue();
					if (map == null) {
						map = new ArrayEntryList();
					}
					if (map instanceof ArrayEntryList) {
						((ArrayEntryList) map).add(value);
					}
					entry.setValue(map);
				} else {
					entry.setValue(value);
				}
				
				return true;
			}
		}
		return false;
	}

	@Override
	public BaseEntityList getNewArray() {
		return new ArrayEntryList<K>();
	}

	@Override
	public BaseEntity getNewObject() {
		return new MapEntry<K>();
	}
}
