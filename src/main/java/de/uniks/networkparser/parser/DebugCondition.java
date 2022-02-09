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
 * Execute Template
 * 
 * @author Stefan DebugCondition
 */
public class DebugCondition implements ParserCondition, SendableEntityCreator {
	public static final String KEY = "debug";
	private int line = -1;
	private NetworkParserLog logger = new NetworkParserLog();
	private ObjectCondition condition;

	public DebugCondition withLine(int value) {
		this.line = value;
		return this;
	}

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

	public boolean exeuteTemplate(TemplateResultFragment fragment) {
		return true;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		/* CREATE FIELD */
		if (buffer == null) {
			return;
		}
		/* Skip Word */
		/* IF CONDITION */
		CharacterBuffer tokenPart = buffer.nextToken(false, SPLITEND, ENTER, '!');
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
