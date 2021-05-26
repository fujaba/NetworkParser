package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;

/**
 * For Template for Import a Class
 * 
 * @author Stefan FeatureCondition for ModelFilter
 *
 *         Format {{#import value}}
 */
public class ImportCondition implements ParserCondition {
	public static final String TAG = "import";
	private ObjectCondition importExpression;

	@Override
	public String getKey() {
		return TAG;
	}

	public void parseImport(String className, SimpleList<String> imports) {
		if(className == null) {
			return;
		}
		int genericType = className.indexOf("<");
		if (genericType > 0) {
			/* Try to rekursiv add */
			parseImport(className.substring(genericType + 1, className.lastIndexOf(">")), imports);
			className = className.substring(0, genericType);
		}
		String[] strings = className.split(",");
		for (String importName : strings) {
			imports.with(importName);
		}
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		if (variables instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) variables;
			SimpleList<String> imports = new SimpleList<String>();
			if (importExpression instanceof ChainCondition) {
				ChainCondition cc = (ChainCondition) importExpression;
				ConditionSet templates = cc.getList();
				CharacterBuffer buffer = templates.getAllValue(variables);
				parseImport(buffer.toString(), imports);
			} else if (importExpression instanceof VariableCondition) {
				VariableCondition vc = (VariableCondition) importExpression;
				Object buffer = vc.getValue(variables);
				if (buffer != null) {
					parseImport(buffer.toString(), imports);
				}
			} else if (importExpression != null) {
				parseImport(importExpression.toString(), imports);
			}
			if (imports.size() > 0) {
				creator.setValue(variables, "headers", imports, SendableEntityCreator.NEW);
			}
		}
		return null;
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof LocalisationInterface) {
			getValue((LocalisationInterface) value);
		}
		return importExpression != null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		if(buffer == null) {
			return;
		}
		buffer.skip();
		ObjectCondition result = null;
		ObjectCondition expression;
		while (buffer.getCurrentChar() != SPLITEND) {
			int position = buffer.position();
			expression = parser.parsing(buffer, customTemplate, false, true, "}");
			if (result == null) {
				result = expression;
			} else if (result instanceof ChainCondition) {
				((ChainCondition) result).with(expression);
				if (position == buffer.position()) {
					buffer.skip();
				}
			} else {
				ChainCondition chainCondition = new ChainCondition();
				chainCondition.with(result);
				if (expression instanceof ChainCondition) {
					ChainCondition cc = (ChainCondition) expression;
					chainCondition.with(cc.getList());
				} else {
					chainCondition.with(expression);
				}
				result = chainCondition;
			}
		}
		this.setExpression(result);
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
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
		if (this.importExpression != null) {
			return "{{#import " + this.importExpression.toString() + "}}";
		}
		return "{{#import}}";
	}
}
