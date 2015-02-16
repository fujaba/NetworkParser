package de.uniks.networkparser.list;

import java.util.Map;
import java.util.Map.Entry;

public class SimpleEntitySetItem<K, V> implements Entry<K, V>{
	private int position;
	private Map<K,V> map;
	
	
	public SimpleEntitySetItem(Map<K, V> value) {
		this.map = value;
	}

	@Override
	public K getKey() {
//		return map.get
		return null;
	}

	@Override
	public V getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V setValue(V value) {
		// TODO Auto-generated method stub
		return null;
	}

}
