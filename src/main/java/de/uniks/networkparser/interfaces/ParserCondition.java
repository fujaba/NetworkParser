package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleKeyValueList;

public interface ParserCondition extends ObjectCondition{
	public CharSequence getValue(SimpleKeyValueList<String, String> variables);
	public ObjectCondition create(CharacterBuffer buffer);
	public boolean isExpression();
	public String getKey();
}
