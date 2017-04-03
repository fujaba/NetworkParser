package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class ChainCondition implements ObjectCondition {
	private ObjectCondition condition;
	private Object template; 

	@Override
	public boolean update(Object value) {
		if(condition == null) {
			return true;
		}
		return condition.update(value);
	}

	@SuppressWarnings("unchecked")
	public ChainCondition addTemplate(ObjectCondition...conditions) {
		if(conditions == null) {
			return this;
		}
		if(conditions.length == 1 && this.template == null) {
			this.template = conditions[0];
			return this;
		}
		SimpleList<ObjectCondition> list;
		if(this.template instanceof SimpleList<?>) {
			list = (SimpleList<ObjectCondition>) this.template;
		} else {
			list = new SimpleList<ObjectCondition>(); 
			list.with(this.template);
			this.template = list;
		}
		for(ObjectCondition item : conditions) {
			list.add(item);
		}
		return this;
	}
	
	public ChainCondition withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}
	
	public ObjectCondition getCondition() {
		return condition;
	}

	@SuppressWarnings("unchecked")
	public ObjectCondition getTemplate() {
		if(this.template instanceof SimpleList<?>) { 
			return ((SimpleList<ObjectCondition>)this.template).first();
		}
		return (ObjectCondition) template;	
	}

	public ChainCondition withTemplate(ObjectCondition value) {
		this.template = value;
		return this;
		
	}
}
