package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public class EqualsCondition implements ParserCondition {

	private ObjectCondition leftExpression;
	
	private ObjectCondition rightExpression;
	
	private CharSequence leftValue;
	
	private CharSequence rightValue;
	
	private boolean expression;
	
	@Override
	public boolean update(Object value) {
 		if (leftValue != null && rightValue != null) {
 			if (value != null && value instanceof LocalisationInterface) {
 				LocalisationInterface variables = (LocalisationInterface) value;
 				Object object = getValue(variables);
 				return object != null && object.equals(rightValue);
 			}
 		}
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			Object object = variables.getValue(variables, this.leftValue.toString());
			return object;
		}
		if(value != null && this.leftValue != null) {
			return value.getText(this.leftValue, null, null);
		}
		if(this.leftValue == null) {
			return null;
		}
		if(this.leftValue.equals(value)) {
			return value;
		}
		return null;
	}

	@Override
	public Equals create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		return null;
	}

	@Override
	public boolean isExpression() {
		return expression;
	}

	public EqualsCondition withExpression(boolean expression) {
		this.expression = expression;
		return this;
	}
	
	@Override
	public String getKey() {
		return null;
	}

	public EqualsCondition withLeftExpression(ObjectCondition leftExpression) {
		this.leftExpression = leftExpression;
		return this;
	}
	
	public EqualsCondition withRightExpression(ObjectCondition rightExpression) {
		this.rightExpression = rightExpression;
		return this;
	}
	
	public EqualsCondition withLeftValue(CharSequence leftValue) {
		this.leftValue = leftValue;
		return this;
	}
	
	public EqualsCondition withRightValue(CharSequence rightValue) {
		this.rightValue = rightValue;
		return this;
	}
	
}
