package de.uniks.networkparser.json;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ValuesMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.ConditionMap;

public class AtomarCondition extends ConditionMap{
	private UpdateListener filter;

	public AtomarCondition(UpdateListener listener) {
		this.filter = listener;
	}

	@Override
	public boolean check(ValuesMap values) {
		return filter.update(IdMap.SENDUPDATE, null, values.getEntity(), values.getProperty(), null, values.getValue());
	}

}
