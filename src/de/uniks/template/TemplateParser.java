package de.uniks.template;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class TemplateParser implements ObjectCondition{
	private SimpleKeyValueList<String, String> variables;
	private CharacterBuffer result = new CharacterBuffer();
	private GraphMember member;
	private boolean expression=true;

	public TemplateParser withVariable(SimpleKeyValueList<String, String> list) {
		this.variables = list;
		return this;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition == false) {
			return false;
		}
		if(value instanceof ParserCondition) {
			ParserCondition tc = (ParserCondition) value;
			if(this.expression) {
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

	public TemplateParser withMember(GraphMember member) {
		this.member = member;
		return this;
	}
	
	public GraphMember getMember() {
		return member;
	}
	
	public TemplateParser withExpression(boolean value) {
		this.expression = value;
		return this;
	}
}
