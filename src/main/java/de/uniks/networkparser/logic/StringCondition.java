package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;

public class StringCondition implements ParserCondition {
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
	public CharSequence getValue(LocalisationInterface variables) {
		return value;
	}
	
	public static StringCondition create(CharSequence sequence) {
		return new StringCondition().withValue(sequence);
	}

	@Override
	public ParserCondition create(CharacterBuffer buffer) {
		return create(buffer);
	}

    @Override
    public boolean isExpression() {
        if(value == null) {
            return false;
        }
        CharacterBuffer item = CharacterBuffer.create(value);
        return item.equalsIgnoreCase("true");
    }
	@Override
	public String getKey() {
		return null;
	}
}
