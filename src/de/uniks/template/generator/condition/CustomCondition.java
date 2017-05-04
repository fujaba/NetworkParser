package de.uniks.template.generator.condition;

import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public abstract class CustomCondition<T> implements ParserCondition {
	@Override
	public boolean isExpression() {
		return false;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof ObjectCondition) {
			return ((ObjectCondition)value).update(this);
		}
		if(value instanceof LocalisationInterface) {
			return getValue((LocalisationInterface) value) != null;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(LocalisationInterface variables) {
		if(variables instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) variables;
			Object member = creator.getValue(creator, "member");
			if(member instanceof GraphMember) {
				return getValue(creator, (T)member);
			}
		}
		return null;
	}
	
	public abstract Object getValue(SendableEntityCreator creator, T member);
}
