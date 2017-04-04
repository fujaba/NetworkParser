package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.TemplateCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

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
		if(value instanceof TemplateCondition) {
			TemplateCondition tc = (TemplateCondition) value;
			if(tc.isExpression()) {
				return tc.update(variables);
			} else {
				result.with(tc.getValue(variables));	
			}
		}
		return true;
	}
	
	public CharacterBuffer getResult() {
		return result;
	}
}
