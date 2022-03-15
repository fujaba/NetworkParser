package de.uniks.networkparser.parser;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.VariableCondition;

/**
 * DebugCondition for Debug Templates create is parsing exeuteTemplate is
 * Execute Template.
 *
 * @author Stefan DebugCondition
 */
public class DebugCondition implements ParserCondition, SendableEntityCreator {
	
	/** The Constant KEY. */
	public static final String KEY = "debug";
	private int line = -1;
	private NetworkParserLog logger = new NetworkParserLog();
	private ObjectCondition condition;

	/**
	 * With line.
	 *
	 * @param value the value
	 * @return the debug condition
	 */
	public DebugCondition withLine(int value) {
		this.line = value;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param evt the evt
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object evt) {
		if (condition != null) {
			if (condition.update(evt)) {
				Object newValue = null;
				if (evt instanceof SimpleEvent) {
					SimpleEvent simpleEvt = (SimpleEvent) evt;
					newValue = simpleEvt.getNewValue();
				}
				logger.debug(this, "update", newValue);
			}
		}
		if (evt instanceof TemplateResultFragment) {
			exeuteTemplate((TemplateResultFragment) evt);
		}
		if (evt instanceof SimpleEvent) {
			SimpleEvent simpleEvt = (SimpleEvent) evt;
			if (simpleEvt.getSource() instanceof ParserEntity) {
				if (NetworkParserLog.DEBUG.equals(simpleEvt.getType())) {
					logger.debug(this, "update", simpleEvt.getNewValue());
					if (line >= 0 && line == simpleEvt.getIndex()) {
						logger.debug(this, "update", "DEBUG");
					}
				} else if (ParserEntity.ERROR.equals(simpleEvt.getType())) {
					logger.error(this, "update", simpleEvt.getNewValue());
					throw new SimpleException("parse error");
				}
			}
		}
		return true;
	}

	/**
	 * Exeute template.
	 *
	 * @param fragment the fragment
	 * @return true, if successful
	 */
	public boolean exeuteTemplate(TemplateResultFragment fragment) {
		return true;
	}

	/**
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		/* CREATE FIELD */
		if (buffer == null) {
			return;
		}
		/* Skip Word */
		/* IF CONDITION */
		CharacterBuffer tokenPart = buffer.nextToken(SPLITEND, ENTER, '!');
		if (tokenPart.length() > 0) {
			char currentChar = buffer.getCurrentChar();
			VariableCondition left = VariableCondition.create(tokenPart, true);
			if (currentChar == '!' || currentChar == ENTER) {
				char nextChar = buffer.getChar();
				if (nextChar == ENTER) {
					Equals equalsExpression = new Equals();
					equalsExpression.withLeft(left);
					equalsExpression.create(buffer, parser, customTemplate);
					if (currentChar == '!') {
						condition = new Not().with(equalsExpression);
					} else {
						condition = equalsExpression;
					}
				}
			} else {
				condition = left;
			}
			buffer.skip();
		}
		/* SKIP TO END */
		buffer.skip();
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return true;
	}

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return KEY;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param isExpression the is expression
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean isExpression) {
		return new DebugCondition();
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		return false;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return null;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "{{#DEBUG}}";
	}
}
