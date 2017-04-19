package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class VariableCondition implements ParserCondition{
	private CharSequence value;
    private boolean expression;

	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			Object object = variables.getValue(variables, this.value.toString());
			return  object != null && !object.equals("");
		}
		if(value instanceof LocalisationInterface) {
			LocalisationInterface variables = (LocalisationInterface) value;
			String object = variables.get(this.value.toString());
			return  object != null && !object.equals("");
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
	public CharSequence getValue(LocalisationInterface variables) {
		if(variables != null && this.value != null) {
			return variables.get(this.value);
		}
		return null;
	}

    public VariableCondition withExpression(boolean value) {
        this.expression = value;
        return this;
    }
    
    public static VariableCondition create(CharSequence sequence, boolean expression) {
        return new VariableCondition().withValue(sequence).withExpression(expression);
	}
	
	@Override
	public ParserCondition create(CharacterBuffer buffer) {
		return create(buffer);
	}

    @Override
    public boolean isExpression() {
        return expression;
    }
	@Override
	public String getKey() {
		return null;
	}
	
	@Override
	public String toString() {
		return "{{"+this.value+"}}";
	}

}
