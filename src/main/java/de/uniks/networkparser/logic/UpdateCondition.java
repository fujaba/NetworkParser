package de.uniks.networkparser.logic;

import de.uniks.networkparser.ValuesMap;

public class UpdateCondition extends ConditionMap {
	@Override
	public boolean check(ValuesMap values) {
		return values.getMap().getKey(values.getEntity()) == null;
	}
}
