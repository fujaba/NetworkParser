package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.list.SimpleKeyValueList;

public interface TemplateCondition extends ObjectCondition{
	public CharSequence getValue(SimpleKeyValueList<String, String> variables);
	public boolean isExpression();
}
