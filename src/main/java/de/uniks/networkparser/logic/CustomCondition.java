package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public abstract class CustomCondition<T> implements ParserCondition {
	protected static final String PROPERTY_MEMBER="member";
	protected boolean isExpression;

	@Override
	public boolean isExpression() {
		return isExpression;
	}
	
	@SuppressWarnings("unchecked")
	public <ST extends CustomCondition<T>> ST withExpression(boolean value) {
		this.isExpression = value;
		return (ST) this;
	}
	
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		skipEnd(buffer);
	}
	protected String addImport(Object value, Class<?> importName) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			creator.setValue(value, "headers", importName.getName(), SendableEntityCreator.NEW);
			return importName.getSimpleName();
		}
		return null;
	}
	protected boolean addImport(Object value, String importName) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			return creator.setValue(value, "headers", importName, SendableEntityCreator.NEW);
		}
		return false;
	}
	
	protected GraphMember getMember(Object value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			GraphMember member = (GraphMember) creator.getValue(creator, PROPERTY_MEMBER);
			return member;
		}
		return null;
	}
	
	protected void skipEnd(CharacterBuffer buffer) {
		buffer.skipTo(SPLITEND, true);
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
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
	
	@Override
	@SuppressWarnings("unchecked")
	public Object getValue(LocalisationInterface variables) {
		if(variables instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) variables;
			return getValue(creator, (T)getMember(variables));
		}
		return null;
	}
	
	public abstract Object getValue(SendableEntityCreator creator, T member);
}
