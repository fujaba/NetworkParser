package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;

public interface TemplateParser {
	public ObjectCondition parseCharacterBuffer(CharacterBuffer template, LocalisationInterface customTemplate, boolean isExpression, String... stopWords);
}
