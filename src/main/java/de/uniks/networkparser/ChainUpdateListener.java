package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class ChainUpdateListener implements UpdateListener{

	private SimpleList<UpdateListener> list = new SimpleList<UpdateListener>();
	
	@Override
	public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
			Object newValue) {
		boolean result=true;
		for(int i=0;i<list.size();i++) {
			if(!list.get(i).update(typ, source, target, property, oldValue, newValue)) {
				result = false;
			}
		}
		return result;
	}

	public ChainUpdateListener with(UpdateListener... values) {
		list.with(values);
		return this;
	}
}
