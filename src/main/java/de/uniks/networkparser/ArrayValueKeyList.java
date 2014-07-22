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

public class ArrayValueKeyList extends AbstractKeyValueList<Object, String> {
	@Override
	public String toString() {
		return "ArrayEntryList with "+size()+" Elements";
	}
	
	@Override
	public ArrayValueKeyList withAllowDuplicate(boolean value) {
		super.withAllowDuplicate(value);
		return this;
	}

	public ArrayValueKeyList with(Object key, String value) {
		this.put(key, value);
		return this;
	}

	@Override
	public ArrayValueKeyList with(Object... values){
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
	public ArrayValueKeyList getNewInstance() {
		return new ArrayValueKeyList();
	}

	@Override
	public String remove(Object key) {
		return ""+removeItemByObject(key);
	}
}
