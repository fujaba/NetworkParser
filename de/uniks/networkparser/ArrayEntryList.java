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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.event.MapEntry;

public class ArrayEntryList extends EntityList<MapEntry> {
	@Override
	public ArrayEntryList getNewArray() {
		return new ArrayEntryList();
	}
	
	@Override
	public MapEntry getNewObject() {
		return new MapEntry();
	}

	@Override
	public String toString() {
		return "ArrayEntryList with "+size()+" Elements";
	}
	
	@Override
	public ArrayEntryList withAllowDuplicate(boolean value) {
		super.withAllowDuplicate(value);
		return this;
	}

	public boolean containsKey(Object key) {
		for(Iterator<MapEntry> i = iterator();i.hasNext();){
			MapEntry item = i.next();
			if(item.getKey().equals(key)){
				return true;
			}
		}
		return false;
	}

	public boolean containsValue(Object value) {
		for(Iterator<MapEntry> i = iterator();i.hasNext();){
			MapEntry item = i.next();
			if(item.getValue().equals(value)){
				return true;
			}
		}
		return false;
	}

	public Object get(String key) {
		for(Iterator<MapEntry> i = iterator();i.hasNext();){
			MapEntry item = i.next();
			if(item.getKey().equals(key)){
				return item.getValue();
			}
		}
		return null;
	}

	public Object put(String key, Object value) {
		if(!isAllowDuplicate()){			
			for(Iterator<MapEntry> i = iterator();i.hasNext();){
				MapEntry item = i.next();
				if(item.getKey().equals(key)){
					item.withValue(value);
					return item.getValue();
				}
			}
		}
		MapEntry newObject = getNewObject();
		newObject.withKey(key);
		newObject.withValue(value);
		values.add(newObject);
		return value;
	}

	public void putAll(Map<String, Object> map) {
		for(Iterator<Entry<String, Object>> i = map.entrySet().iterator();i.hasNext();){
			Entry<String, Object> item = i.next();
			this.put(item.getKey(), item.getValue());
		}
	}

	public ArrayList<String> keySet() {
		ArrayList<String> list = new ArrayList<String>();
		for(Iterator<MapEntry> i = values.listIterator();i.hasNext();){
			list.add(i.next().getKeyString());
		}
		return list;
	}

	/**
	 * Not Good because the values copy to new List
	 * 
	 * @return Collection of Values
	 */
	public Collection<Object> values() {
		ArrayList<Object> list = new ArrayList<Object>();
		for(Iterator<MapEntry> i = values.listIterator();i.hasNext();){
			list.add(i.next().getValue());
		}
		return list;
	}

	public List<MapEntry> entrySet() {
		return values;
	}

	public String getKey(Object obj) {
		for(Iterator<MapEntry> i = iterator();i.hasNext();){
			MapEntry item = i.next();
			if(item.getValue().equals(obj)){
				return item.getKeyString();
			}
		}
		return null;
	}
	public Object getValue(String key) {
		return get(key);
	}
	
	public ArrayEntryList with(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public boolean removeKey(String key) {
		for(Iterator<MapEntry> i = iterator();i.hasNext();){
			MapEntry item = i.next();
			if(item.getKey().equals(key)){
				i.remove();
				return true;
			}
		}
		return false;
	}
	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}
}
