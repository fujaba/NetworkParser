package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;

public interface ParserCondition extends ObjectCondition {
	public static final char SPLITEND='}';
	public static final char SPLITSTART='{';
	public static final char ENTER='=';
	public static final char SPACE=' ';

	public Object getValue(LocalisationInterface variables);
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate);
	public boolean isExpression();
	public String getKey();
	public Object getSendableInstance(boolean isExpression);
}
