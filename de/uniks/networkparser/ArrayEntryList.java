package de.uniks.networkparser;
import java.util.Collection;
import java.util.Iterator;






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
import de.uniks.networkparser.event.MapEntry;

public class ArrayEntryList extends AbstractKeyValueList<String, Object> {
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
	public ArrayEntryList with(
			Collection<?> values) {
		for(Iterator<?> i = values.iterator();i.hasNext();){
			Object item = i.next();
			if(item instanceof MapEntry){
				add((MapEntry) item);
			}
		}
		return this;
	}
	
	@Override
	public ArrayEntryList with(Object... values){
		if(values != null){
			for(Object value : values){
				if(value instanceof MapEntry){
					add((MapEntry) value);
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
	public AbstractKeyValueEntry<String, Object> getNewEntity() {
		return new MapEntry();
	}
}
