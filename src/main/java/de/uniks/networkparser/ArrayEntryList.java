package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BidiMap;

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

public class ArrayEntryList extends AbstractKeyValueList<String, Object> implements BidiMap<String, Object>{
	@Override
	public String toString() {
		return "ArrayEntryList with "+size()+" Elements";
	}
	
	@Override
	public ArrayEntryList withAllowDuplicate(boolean value) {
		super.withAllowDuplicate(value);
		return this;
	}

	public ArrayEntryList with(String key, Object value) {
		this.put(key, value);
		return this;
	}

	@Override
	public ArrayEntryList with(Object... values){
		if(values != null){
			for(Object value : values){
				if(value instanceof AbstractEntity){
					addEntity((AbstractEntity<?, ?>) value);
				}
			}
		}
		return this;
	}
	
	@Override
	public ArrayEntryList getNewInstance() {
		return new ArrayEntryList();
	}

	@Override
	public Object remove(Object key) {
		return removeItemByObject(""+key);
	}

	// Methods for BidiMap
	@Override
	public boolean containKey(String key) {
		return super.contains(key);
	}
	

	@Override
	public boolean containValue(Object value) {
		return super.containsValue(value);
	}

	@Override
	public ArrayEntryList without(String key, Object value) {
		super.removeItemByObject(key);
		return this;
	}
}