package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * @author Stefan
 * FeatureCondition for ModelFilter
 * 
 * Format {{#import value}} 
 */
public class ImportCondition implements ParserCondition {
	private static final char SPLITEND='}';
	public static final String TAG="import";
	private String importName;
	
	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public CharSequence getValue(LocalisationInterface variables) {
		return null;
	}

	
	@Override
	public boolean update(Object value) {
		if(value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			creator.setValue(value, "headers", importName, SendableEntityCreator.NEW);
		}
		return importName != null;
	}

	@Override
	public ImportCondition create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		ImportCondition condition = new ImportCondition();
		condition.setImportName(buffer.nextToken(false, SPLITEND).toString());
		
		buffer.skipChar(SPLITEND);
		return condition;
	}

	private boolean setImportName(String value) {
		if(value != this.importName) {
			this.importName = value;
			return true;
		}
		return false;
	}

	@Override
	public boolean isExpression() {
		return true;
	}
}
