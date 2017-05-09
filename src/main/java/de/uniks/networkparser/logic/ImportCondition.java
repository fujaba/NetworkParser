package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#import value}} 
 */
public class ImportCondition implements ParserCondition {
	public static final String TAG="import";
	private ObjectCondition importExpression;
	
	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		if(variables instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) variables;
			//&& importExpression.update(variables)
			if (importExpression != null) {
					if(importExpression instanceof ChainCondition) {
						ChainCondition cc = (ChainCondition) importExpression;
						SimpleList<ObjectCondition> templates = cc.getTemplates();
						CharacterBuffer buffer=new CharacterBuffer();
						for(ObjectCondition item : templates) {
							if(item instanceof VariableCondition) {
								VariableCondition vc = (VariableCondition) item;
								Object result = vc.getValue(variables);
								if(result != null) {
									buffer.with(result.toString());
								}
							} else {
								buffer.with(item.toString());
							}
						}
						creator.setValue(variables, "headers", buffer.toString(), SendableEntityCreator.NEW);
					} else if (importExpression instanceof VariableCondition){
						VariableCondition vc = (VariableCondition) importExpression;
						Object result = vc.getValue(variables);
						if(result != null) {
							creator.setValue(variables, "headers", result.toString(), SendableEntityCreator.NEW);
						}
					} else {
						creator.setValue(variables, "headers", importExpression.toString(), SendableEntityCreator.NEW);
					}
				} else {
					creator.setValue(variables, "headers", importExpression.toString(), SendableEntityCreator.NEW);
				}
			}
		return null;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof LocalisationInterface) {
			getValue((LocalisationInterface) value);
		}
		return importExpression != null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		buffer.skip();
		ObjectCondition result = null;
		ObjectCondition expression;
		while(buffer.getCurrentChar() != SPLITEND) {
			expression = parser.parsing(buffer, customTemplate, true);
//			if(expression instanceof VariableCondition) {
//				((VariableCondition)expression).withExpression(true);
//			}
			if(result == null) {
				result = expression;
			}else if(result instanceof ChainCondition) {
				((ChainCondition)result).with(expression);
			} else {
				ChainCondition chainCondition = new ChainCondition();
				chainCondition.with(result);
				if(expression instanceof ChainCondition) {
					ChainCondition cc = (ChainCondition) expression;
					chainCondition.with(cc.getTemplates());
				}else {
					chainCondition.with(expression);
				}
				result = chainCondition;
			}
		}
		this.setExpression(result);
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
		if(this.importExpression != null) {
			return "{{#import "+this.importExpression.toString() +"}}";
		}
		return "{{#import}}";
	}
}
