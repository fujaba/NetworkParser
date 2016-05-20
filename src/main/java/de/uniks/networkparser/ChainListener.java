package de.uniks.networkparser;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
