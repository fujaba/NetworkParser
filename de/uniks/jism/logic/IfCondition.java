package de.uniks.jism.logic;

import de.uniks.jism.Buffer;
import de.uniks.jism.IdMap;

public class IfCondition implements Condition{

	private Condition expression;
	private Condition trueCondition;
	private Condition falseCondition;

	public IfCondition(Condition expression){
		this.expression = expression;
	}
	
	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		if(expression.matches(map, entity, property, value, isMany, deep)){
			if(trueCondition!=null){
				return trueCondition.matches(map, entity, property, value, isMany, deep);
			}
		}else{
			if(falseCondition!=null){
				return falseCondition.matches(map, entity, property, value, isMany, deep);
			}
		}
		return false;
	}

	@Override
	public boolean matches(Buffer buffer) {
		if(expression.matches(buffer)){
			if(trueCondition!=null){
				return trueCondition.matches(buffer);
			}
		}else{
			if(falseCondition!=null){
				return falseCondition.matches(buffer);
			}
		}
		return false;
	}
	
	public IfCondition withTrueCondition(Condition value){
		this.trueCondition = value;
		return this;
	}
	
	public IfCondition withFalseCondition(Condition value){
		this.falseCondition = value;
		return this;
	}
}
