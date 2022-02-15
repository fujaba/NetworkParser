package de.uniks.networkparser.parser;

import de.uniks.networkparser.interfaces.TemplateItem;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class TemplateList.
 *
 * @author Stefan
 */
public class TemplateList extends SimpleKeyValueList<String, String> implements TemplateItem {
	
	/** The Constant NAME. */
	public static final String NAME = "TemplateList";

	/**
	 * Gets the value.
	 *
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(String attribute) {
		int pos = super.indexOf(attribute);
		if (pos >= 0) {
			return super.getValueByIndex(pos);
		}
		return null;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return NAME;
	}

}
