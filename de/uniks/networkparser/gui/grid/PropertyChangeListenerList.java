package de.uniks.networkparser.gui.grid;
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

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.EntityList;
import de.uniks.networkparser.interfaces.BaseItem;

public class PropertyChangeListenerList extends EntityList<PropertyChangeListener> {
	@Override
	public BaseItem getNewObject() {
		return null;
	}
	
	@Override
	public EntityList<PropertyChangeListener> getNewArray() {
		return new PropertyChangeListenerList();
	}
	
	@Override
	public String toString() {
		return "ArrayEntryList with "+size()+" Elements";
	}
}
