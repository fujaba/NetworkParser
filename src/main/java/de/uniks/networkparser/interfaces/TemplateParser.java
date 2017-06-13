package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.buffer.CharacterBuffer;

public interface TemplateParser {
	public static final int DECLARATION = 0;
	
	public static final int PACKAGE = 1;

	public static final int IMPORT = 2;
	
	public static final int TEMPLATE = 3;
	
	public static final int FIELD = 4;

	public static final int VALUE = 5;
	
	public static final int METHOD = 6;

	public static final int TEMPLATEEND = Integer.MAX_VALUE;

	public ObjectCondition parsing(CharacterBuffer template, LocalisationInterface customTemplate, boolean isExpression, String... stopWords);
}
