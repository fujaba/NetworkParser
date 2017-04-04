package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.StringCondition;
import de.uniks.networkparser.logic.VariableCondition;

public class TemplateParser implements ObjectCondition{
	private SimpleKeyValueList<String, String> variables;
	private CharacterBuffer result = new CharacterBuffer();

	public TemplateParser withVariable(SimpleKeyValueList<String, String> list) {
		this.variables = list;
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition == false) {
			return false;
		}
		if(value instanceof StringCondition) {
			result.with(((StringCondition)value).getValue());
		}
		if(value instanceof VariableCondition) {
			VariableCondition variableCondition = (VariableCondition)value;
			
			result.with(variables.get(variableCondition.getValue()));
		}
		return true;
	}
	
	public CharacterBuffer getResult() {
		return result;
	}
}
