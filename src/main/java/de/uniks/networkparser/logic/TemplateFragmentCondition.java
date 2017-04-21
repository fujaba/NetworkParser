package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

// {{#template PACKAGE}}{{#endtemplate}}
public class TemplateFragmentCondition implements ParserCondition{
	public static final String PROPERTY_CLONE="clone";
	public static final String PROPERTY_FILE="file";
	public static final String PROPERTY_KEY="key";
	public static final String PROPERTY_TEMPLATE="template";

	public static final String TAG="template"; 
	private static final char SPLITEND='}';
	private static final char ENTER='=';
	private String id;
	private ObjectCondition child;

	@Override
	public boolean isExpression() {
		return true;
	}

	@Override
	public String getKey() {
		return TAG;
	}
	
	private int getIdKey() {
		if("PACKAGE".equalsIgnoreCase(this.id)) {
			return TemplateParser.PACKAGE;
		}
		if("IMPORT".equalsIgnoreCase(this.id)) {
			return TemplateParser.IMPORT;
		}
		if("TEMPLATE".equalsIgnoreCase(this.id)) {
			return TemplateParser.TEMPLATE;
		}
		if("FIELD".equalsIgnoreCase(this.id)) {
			return TemplateParser.FIELD;
		}
		if("VALUE".equalsIgnoreCase(this.id)) {
			return TemplateParser.VALUE;
		}
		if("METHOD".equalsIgnoreCase(this.id)) {
			return TemplateParser.METHOD;
		}
		if("TEMPLATEEND".equalsIgnoreCase(this.id)) {
			return TemplateParser.TEMPLATEEND;
		}
		return TemplateParser.DECLARATION;
	}
	
	@Override
	public boolean update(Object value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			// VODOO
			SendableEntityCreator newInstance = (SendableEntityCreator) creator.getValue(creator, PROPERTY_CLONE);
			newInstance.setValue(newInstance, PROPERTY_KEY, this.getIdKey(), SendableEntityCreator.NEW);
			newInstance.setValue(newInstance, PROPERTY_TEMPLATE, this.child, SendableEntityCreator.NEW);
			
			newInstance.setValue(newInstance, PROPERTY_FILE, creator.getValue(creator, PROPERTY_FILE), SendableEntityCreator.NEW);

			this.child.update(newInstance);
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TemplateFragmentCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		CharacterBuffer id = buffer.nextToken(false, SPLITEND, ENTER);
		this.id = id.toString();
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
		this.child = parser.parsing(buffer, customTemplate, true, "endtemplate");
		buffer.skipTo(SPLITEND, true);
		buffer.skipChar(SPLITEND);
		return this;
	}
	
	public String getId() {
		return id;
	}

	@Override
	public TemplateFragmentCondition getSendableInstance(boolean prototyp) {
		return new TemplateFragmentCondition();
	}
}
