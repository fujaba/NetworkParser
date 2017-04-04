package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class StringCondition implements ObjectCondition{
	private CharSequence value;

	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		return this.value != null;
	}
	public StringCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}
	public CharSequence getValue() {
		return value;
	}
	
	public static StringCondition create(CharSequence sequence) {
		return new StringCondition().withValue(sequence);
	}
}
