package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;

public interface ParserCondition extends ObjectCondition{
	public Object getValue(LocalisationInterface variables);
	public ObjectCondition create(CharacterBuffer buffer);
	public boolean isExpression();
	public String getKey();
}
