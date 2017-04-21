package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public class VariableCondition implements ParserCondition{
	private CharSequence value;
    private boolean expression;

	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		if(value instanceof LocalisationInterface) {
			LocalisationInterface variables = (LocalisationInterface) value;
			Object object = getValue(variables);
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
	
	public Object getValue(LocalisationInterface value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			Object object = variables.getValue(variables, this.value.toString());
			return object;
		}
		if(value != null && this.value != null) {
			return value.getText(this.value, null, null);
		}
		if(this.value == null) {
			return null;
		}
		if(this.value.equals(value)) {
			return value;
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
	public VariableCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		this.value = buffer.nextToken(true, ' ', '}');
		return this;
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
