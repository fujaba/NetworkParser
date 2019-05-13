package de.uniks.networkparser.parser;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * DebugCondition for Debug Templates 
 * create is parsing
 * exeuteTemplate is Execute Template
 * @author Stefan DebugCondition
 */
public class DebugCondition implements ParserCondition, SendableEntityCreator {
	public static final String KEY = "debug";
	private int line=-1;
	private NetworkParserLog logger=new NetworkParserLog(); 
	
	public DebugCondition withLine(int value) {
		this.line = value;
		return this;
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof TemplateResultFragment) {
			exeuteTemplate((TemplateResultFragment) evt);
		}
		if(evt instanceof SimpleEvent) {
			SimpleEvent simpleEvt = (SimpleEvent) evt;
			if(simpleEvt.getSource() instanceof ParserEntity) {
				if(NetworkParserLog.DEBUG.equals(simpleEvt.getType()) && line>=0) {
					logger.debug(this, "update", simpleEvt.getNewValue());
					if(line==simpleEvt.getIndex()) {
						logger.debug(this, "update", "DEBUG");
					}
				}else if(ParserEntity.ERROR.equals(simpleEvt.getType())) {
					logger.error(this, "update", simpleEvt.getNewValue());
					throw new RuntimeException("parse error");
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
//		 CREATE FIELD
		// SKIP TO END
		if(buffer == null) {
			return;
		}
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
