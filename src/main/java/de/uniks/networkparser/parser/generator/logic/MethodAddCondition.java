package de.uniks.networkparser.parser.generator.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

// Add so for Adding some Code for example getValue:Attribute or CreatorCreator
public class MethodAddCondition implements ParserCondition, SendableEntityCreator {
	public static final String PROPERTY_CHILD = "child";
	private ObjectCondition child;
	@Override
	public boolean update(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExpression() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		// TODO Auto-generated method stub
		return null;
	}

}
