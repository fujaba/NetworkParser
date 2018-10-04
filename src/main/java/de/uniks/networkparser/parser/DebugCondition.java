package de.uniks.networkparser.parser;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * @author Stefan Add Condition
 */
public class DebugCondition implements ParserCondition, SendableEntityCreator {
	public static final String KEY = "debug";

	@Override
	public boolean update(Object value) {
		return true;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
//		 CREATE FIELD
		// SKIP TO END
		buffer.skipTo(SPLITEND, false);
		buffer.skip();
		buffer.skip();
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return true;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new DebugCondition();
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public String toString() {
		return "{{#DEBUG}}";
	}
}
