package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class VariableCondition implements ObjectCondition{
	private CharSequence value;

	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		if(this.value == null) {
			return value == null;
		}
		return this.value.equals(value);
	}

	public VariableCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}
	public CharSequence getValue() {
		return value;
	}
	
	public static VariableCondition create(CharSequence sequence) {
		return new VariableCondition().withValue(sequence);
	}
	
}
