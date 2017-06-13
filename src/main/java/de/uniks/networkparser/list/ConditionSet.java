package de.uniks.networkparser.list;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.logic.ChainCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class ConditionSet extends SimpleSet<ObjectCondition>{

	@Override
	public boolean add(ObjectCondition newValue) {
		if(newValue instanceof ChainCondition) {
			ChainCondition cc= (ChainCondition) newValue;
			return super.addAll(cc.getList());
		}
		return super.add(newValue);
	}
	
	public CharacterBuffer getAllValue(LocalisationInterface variables) {
		CharacterBuffer buffer=new CharacterBuffer();
		for(ObjectCondition item : this) {
			if(item instanceof VariableCondition) {
				VariableCondition vc = (VariableCondition) item;
				Object result = vc.getValue(variables);
				if(result != null) {
					buffer.with(result.toString());
				}
			} else {
				buffer.with(item.toString());
			}
		}
		return buffer;
	}
}
