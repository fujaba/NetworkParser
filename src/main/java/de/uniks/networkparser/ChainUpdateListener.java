package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class ChainUpdateListener implements UpdateListener{
	private SimpleList<UpdateListener> list = new SimpleList<UpdateListener>();

	@Override
	public boolean update(String typ, BaseItem source, PropertyChangeEvent event) {
		boolean result=true;
		for(int i=0;i<list.size();i++) {
			if(!list.get(i).update(typ, source, event)) {
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
