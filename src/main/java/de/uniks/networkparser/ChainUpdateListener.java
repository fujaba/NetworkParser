package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class ChainUpdateListener implements UpdateListener{
	private SimpleList<UpdateListener> list = new SimpleList<UpdateListener>();

	@Override
	public boolean update(String typ, PropertyChangeEvent event) {
		boolean result=true;
		for(int i=0;i<list.size();i++) {
			if(!list.get(i).update(typ, event)) {
				result = false;
			}
		}
		return result;
	}

	public ChainUpdateListener with(UpdateListener... values) {
		if(values ==null) {
			return this;
		}
		for(UpdateListener item : values) {
			list.with(item);	
		}
		return this;
	}
}
