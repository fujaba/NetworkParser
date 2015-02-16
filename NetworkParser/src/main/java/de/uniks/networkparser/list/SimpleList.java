package de.uniks.networkparser.list;

import java.util.List;

public class SimpleList<V> extends AbstractList<V> implements List<V> {
	public SimpleList<V> getNewInstance() {
		return new SimpleList<V>();
	}
	
	@Override
	public SimpleList<V> clone() {
		return getNewInstance().init(this);
	}
	
	public SimpleList<V> subList(int fromIndex, int toIndex) {
		SimpleList<V> newInstance = getNewInstance();
		if(fromIndex>toIndex && fromIndex>=size()){
			return newInstance;
		}
		if(toIndex >= size()) {
			toIndex = size() - 1;	
		}
		
		while(fromIndex<toIndex) {
			get(fromIndex++);
		}
		return newInstance;
	}

	@Override
	public boolean remove(Object o) {
		return super.removeByObject(o)>=0;
	}
}
