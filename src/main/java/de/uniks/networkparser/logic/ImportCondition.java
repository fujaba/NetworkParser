package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#import value}} 
 */
public class ImportCondition implements ParserCondition {
	private static final char SPLITEND='}';
	private static final char SPLITSTART='{';
	public static final String TAG="import";
	private String importName;
	
	private ObjectCondition importExpression;
	
	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		return null;
	}

	
	@Override
	public boolean update(Object value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			if (importExpression != null && importExpression.update(value)) {
				creator.setValue(value, "headers", ((SendableEntityCreator) value).getValue(creator, importName), SendableEntityCreator.NEW);
			} else {
				creator.setValue(value, "headers", importName, SendableEntityCreator.NEW);
			}
		}
		return importName != null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		buffer.skip();
		char expressionStart = buffer.getCurrentChar();
		if (expressionStart == SPLITSTART) {
			ObjectCondition expression = parser.parsing(buffer, customTemplate, true);
			this.setExpression(expression);
			if (expression instanceof VariableCondition) {
				this.setImportName(expression.toString().substring(2, expression.toString().indexOf("}")));
			} else {
				this.setImportName(expression.toString());
			}
		} else {
			this.setImportName(expressionStart + buffer.nextToken(false, SPLITEND).toString());
		}
		buffer.skipChar(SPLITEND);
	}

	private boolean setImportName(String value) {
		if(value != this.importName) {
			this.importName = value;
			return true;
		}
		return false;
	}

	private boolean setExpression(ObjectCondition value) {
		if (value != this.importExpression) {
			this.importExpression = value;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isExpression() {
		return true;
	}
	
	@Override
	public ImportCondition getSendableInstance(boolean prototyp) {
		return new ImportCondition();
	}
	
	@Override
	public String toString() {
		return "{{#import "+importName+"}}";
	}
}
