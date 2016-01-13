package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleValuesMap;
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
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;

public class IdFilterElements extends SimpleList<Object> implements UpdateListener {
	private Condition<SimpleValues> condition;
	protected SimpleValuesMap filterMap;

	
	public IdFilterElements(Condition<SimpleValues> condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.value(clazzConditon);
	}
	
	@Override
	public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
			Object newValue) {
		if(condition!=null) {
			if(filterMap == null) {
				filterMap = new SimpleValuesMap().with((IdMap)target);
			}
			SimpleValuesMap value = filterMap.with(property).withValue(newValue);
			if(condition.check(value)) {
				return add(newValue);
			}
		}
		return false;
	}
}
