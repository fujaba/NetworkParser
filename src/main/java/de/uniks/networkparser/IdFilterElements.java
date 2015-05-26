package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.Condition;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.logic.ValuesMap;
import de.uniks.networkparser.logic.ValuesSimple;

public class IdFilterElements extends SimpleList<Object> implements UpdateListener {

	private Condition<ValuesSimple> condition;
	
	public IdFilterElements(Condition<ValuesSimple> condition) {
		this.condition = condition;
	}
	public IdFilterElements(Class<?> clazzConditon) {
		this.condition = InstanceOf.value(clazzConditon);
	}
	
	@Override
	public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
			Object newValue) {
		if(condition!=null) {
			ValuesMap value = ValuesMap.with((IdMap)target, target, property, newValue, 0);
			if(condition.check(value)) {
				return add(newValue);
			}
		}
		return false;
	}
}
