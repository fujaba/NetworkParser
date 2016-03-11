package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class ChainUpdateListener implements UpdateListener{
	private SimpleList<Object> list = new SimpleList<Object>();

	@Override
	public boolean update(Object evt) {
		if(evt instanceof PropertyChangeEvent) {
			return updatePCE((PropertyChangeEvent) evt);
		}
		return false;
	}
	public boolean updatePCE(PropertyChangeEvent evt) {
		boolean result=false;
		for(Iterator<Object> i = list.iterator();i.hasNext();) {
			Object listener = i.next();
			if(listener instanceof UpdateListener) {
				result = result & ((UpdateListener)listener).update(evt);
			} else if(listener instanceof PropertyChangeListener) {
				((PropertyChangeListener)listener).propertyChange(evt);
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
	public ChainUpdateListener with(PropertyChangeListener... values) {
		if(values ==null) {
			return this;
		}
		for(PropertyChangeListener item : values) {
			list.with(item);	
		}
		return this;
	}
}
