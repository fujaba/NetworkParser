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
import java.util.LinkedHashMap;
import java.util.Set;

import de.uniks.networkparser.interfaces.BidiMap;

public class BidiLinkedHashMap implements BidiMap<String, Object> {
	protected LinkedHashMap<String, Object> keyValue = new LinkedHashMap<String, Object>();
	protected LinkedHashMap<Object, String> valueKey = new LinkedHashMap<Object, String>();

	@Override
	public int size() {
		return keyValue.size();
	}

	@Override
	public void clear() {
		keyValue.clear();
		valueKey.clear();
	}

	@Override
	public Collection<Object> values() {
		return keyValue.values();
	}

	@Override
	public Set<String> keySet() {
		return keyValue.keySet();
	}

	@Override
	public boolean containKey(String key) {
		return keyValue.containsKey(key);
	}

	@Override
	public boolean containValue(Object value) {
		return valueKey.containsKey(value);
	}

	@Override
	public BidiLinkedHashMap without(String key, Object value) {
		this.keyValue.remove(key);
		this.valueKey.remove(value);
		return this;
	}

	public BidiLinkedHashMap with(String key, Object value) {
		this.keyValue.put(key, value);
		this.valueKey.put(value, key);
		return this;
	}

	@Override
	public Object getValueItem(Object key) {
		return keyValue.get(key);
	}

	@Override
	public String getKey(Object key) {
		return valueKey.get(key);
	}
}
