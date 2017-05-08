package de.uniks.networkparser.logic;

import java.util.Collection;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#import value}} 
 */
public class ForeachCondition implements ParserCondition {
	private static final String ITEM="item";
	public static final String TAG="foreach";
	private ObjectCondition expression;
	private ObjectCondition loop;
	@Override
	public String
	
	getKey() {
		return TAG;
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		return null;
	}
	
	/**
	 * @param value		Set the new Expression
	 * @return 			IfCondition Instance
	 */
	public ForeachCondition withExpression(ObjectCondition value) {
		this.expression = value;
		return this;
	}


	
	@Override
	public boolean update(Object value) {
		if (expression != null && loop != null) {
			if(expression instanceof ParserCondition && 
					value instanceof LocalisationInterface){
				ParserCondition parser = (ParserCondition) expression;
				Object object = parser.getValue((LocalisationInterface)value);
//				expression.
//				creator.getValue(value, expression);
//				
				LocalisationInterface variablen = (LocalisationInterface) value;
//				Object object = creator.getValue(variablen);
				if(object instanceof Collection<?>) {
					Collection<?> collection = (Collection<?>) object;
					for(Object item : collection) {
						variablen.put(ITEM, item);
//						value
						loop.update(value);
						variablen.put(ITEM, null);
					}
				}
			}
		}
		return true;
	}

	@Override
	public ForeachCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
//		this.expression = StringCondition.create(buffer.nextToken(false, SPLITEND));
		buffer.skipChar(SPACE);
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true);
		this.expression = expression;
		
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
		
		// Add Children
		expression = parser.parsing(buffer, customTemplate, false, "endfor");
		withLoopCondition(expression);
		buffer.skipChar(SPLITEND);
		return this;
	}

	@Override
	public boolean isExpression() {
		return true;
	}

	public ForeachCondition withLoopCondition(ObjectCondition value) {
		this.loop = value;
		return this;
	}

	public ObjectCondition getLoopCondition() {
		return loop;
	}
	
	@Override
	public ForeachCondition getSendableInstance(boolean prototyp) {
		return new ForeachCondition();
	}
}
