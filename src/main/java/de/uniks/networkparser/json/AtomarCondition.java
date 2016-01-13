package de.uniks.networkparser.json;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleValuesMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.logic.SimpleConditionMap;

public class AtomarCondition extends SimpleConditionMap{
	private UpdateListener filter;

	public AtomarCondition(UpdateListener listener) {
		this.filter = listener;
	}

	@Override
	public boolean check(SimpleValuesMap values) {
		return filter.update(IdMap.SENDUPDATE, null, values.getEntity(), values.getProperty(), null, values.getValue());
	}

}
