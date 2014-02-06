package de.uniks.networkparser.logic;

public abstract class ConditionMap implements Condition {
	@Override
	public boolean matches(ValuesSimple values) {
		if(values instanceof ValuesMap){
			return matches((ValuesMap) values);
		}
		return false;
	}
	
	public abstract boolean matches(ValuesMap values);
}
