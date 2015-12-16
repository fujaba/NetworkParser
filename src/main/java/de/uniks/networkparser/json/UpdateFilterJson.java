package de.uniks.networkparser.json;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.logic.UpdateCondition;
import de.uniks.networkparser.logic.ValuesSimple;

public class UpdateFilterJson extends Filter implements Condition<ValuesSimple> {
	public UpdateFilterJson() {
		withConvertable(new UpdateCondition());
		super.withPropertyRegard(this);
	}
	
	@Override
	public boolean check(ValuesSimple value) {
		return false;
	}
}
