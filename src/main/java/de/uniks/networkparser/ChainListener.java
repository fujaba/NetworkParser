package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class ChainListener implements UpdateListener{
	private boolean chain=true;
	private SimpleList<Object> list = new SimpleList<Object>();

	public ChainListener enableHook() {
		this.chain = false;
		return this;
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof PropertyChangeEvent) {
			return updatePCE((PropertyChangeEvent) evt);
		}
		return false;
	}

	public boolean updatePCE(PropertyChangeEvent evt) {
		for(Iterator<Object> i = list.iterator();i.hasNext();) {
			Object listener = i.next();
			if(listener instanceof UpdateListener) {
				if(((UpdateListener)listener).update(evt) == false) {
					if(chain) {
						return false;
					}
				}
			} else if(listener instanceof PropertyChangeListener) {
				((PropertyChangeListener)listener).propertyChange(evt);
			}
		}
		return true;
	}

	public ChainListener with(UpdateListener... values) {
		if(values ==null) {
			return this;
		}
		for(UpdateListener item : values) {
			list.with(item);	
		}
		return this;
	}
	public ChainListener with(PropertyChangeListener... values) {
		if(values ==null) {
			return this;
		}
		for(PropertyChangeListener item : values) {
			list.with(item);	
		}
		return this;
	}
}
