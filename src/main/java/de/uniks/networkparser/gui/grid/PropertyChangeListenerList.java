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
import java.util.List;

import de.uniks.networkparser.AbstractEntityList;
import de.uniks.networkparser.AbstractList;

public class PropertyChangeListenerList extends
		AbstractEntityList<PropertyChangeListener> implements
		List<PropertyChangeListener> {
	@Override
	public String toString() {
		return "ArrayEntryList with " + size() + " Elements";
	}

	@Override
	public PropertyChangeListenerList with(Object... values) {
		if (values != null) {
			for (Object value : values) {
				if (value instanceof PropertyChangeListener) {
					add((PropertyChangeListener) value);
				}
			}
		}
		return this;
	}

	@Override
	public AbstractList<PropertyChangeListener> getNewInstance() {
		return new PropertyChangeListenerList();
	}

	@Override
	public boolean remove(Object value) {
		return removeItemByObject((PropertyChangeListener) value) >= 0;
	}

	@Override
	public boolean add(PropertyChangeListener e) {
		return addEntity(e);
	}
}
