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
import de.uniks.networkparser.interfaces.BidiMap;

public class ArrayEntityList<K, V> extends AbstractKeyValueList<K, V> implements BidiMap<K, V>{
	@Override
	public ArrayEntityList<K, V> getNewInstance() {
		return new ArrayEntityList<K, V>();
	}

	@Override
	public AbstractList<K> with(Object... values) {
		if (values==null) {
			return this;
		}
		if (values.length%2== 0) {
			for (int i=0;i<values.length;i+=2) {
				this.withValue(values[i], values[i + 1]);
			}
		} else {
			for (int i=0;i<values.length;i++) {
				if (values[i] instanceof Map<?, ?>) {
					this.withMap((Map<?, ?>)values[i]);
				} else if (values[i] instanceof AbstractEntity) {
					addEntity((AbstractEntity<?, ?>) values[i]);
				}
			}
		}
		return this;
	}

	// Methods for BidiMap
	@Override
	public boolean containKey(K key) {
		return super.contains(key);
	}

	@Override
	public boolean containValue(V value) {
		return super.containsValue(value);
	}

	@Override
	public BidiMap<K, V> without(K key, V value) {
		super.removeItemByObject(key);
		return this;
	}

	@Override
	public BidiMap<K, V> with(K key, V value) {
		this.put(key, value);
		return this;
	}
	
	protected void hashTableAddValues(Object newValue, int pos) {
		this.hashTableValues = hashTableAdd(this.hashTableValues, this.values,
				newValue, pos);
	}
}
