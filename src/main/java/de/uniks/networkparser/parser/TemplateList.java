package de.uniks.networkparser.parser;

import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class TemplateList extends SimpleKeyValueList<String, String> implements TemplateItem {
	public static final String NAME="TemplateList";

	@Override
	public Object getValue(String attribute) {
		int pos = super.indexOf(attribute);
		if(pos>=0) {
			return super.getValueByIndex(pos);
		}
		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
