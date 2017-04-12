package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class TemplateCondition implements ObjectCondition {
	private ObjectCondition condition;
	private ObjectCondition template; 

	@Override
	public boolean update(Object value) {
		if(condition == null) {
			return true;
		}
		return condition.update(value);
	}

	public TemplateCondition withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}
	
	public ObjectCondition getCondition() {
		return condition;
	}

	public ObjectCondition getTemplate() {
		return template;
	}

	public TemplateCondition withTemplate(ObjectCondition value) {
		this.template = value;
		return this;
	}
}
