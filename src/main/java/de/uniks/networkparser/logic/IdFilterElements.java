package de.uniks.networkparser.logic;

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
import java.util.EventObject;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class IdFilterElements extends SimpleList<Object> implements UpdateListener {
	private Condition<EventObject> condition;


	public IdFilterElements(Condition<EventObject> condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.value(clazzConditon);
	}

	@Override
	public boolean update(String typ, BaseItem source, PropertyChangeEvent event) {
		if(condition!=null) {
			if(condition.check(event)) {
				return add(event.getNewValue());
			}
		}
		return false;
	}
}
