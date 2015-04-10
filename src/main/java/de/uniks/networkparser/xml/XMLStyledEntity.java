package de.uniks.networkparser.xml;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
import de.uniks.networkparser.event.Style;

/**
 * Style Element of XML.
 *
 * @author Stefan
 */
public class XMLStyledEntity extends XMLEntity {
	/** The Styles of XMLStyledEntity. */
	private Style style = new Style();

	@Override
	protected void toStringChildren(StringBuilder sb, int indentFactor,
			int intent) {
		// Starttag
		if (style.isBold()) {
			sb.append("<b>");
		}
		if (style.isItalic()) {
			sb.append("<i>");
		}
		super.toStringChildren(sb, indentFactor, intent);

		// EndTag
		if (style.isItalic()) {
			sb.append("</i>");
		}
		if (style.isBold()) {
			sb.append("</b>");
		}
	}

	/**
	 * Set new Value of Attribute.
	 *
	 * @param attribute
	 *            The Attribute Key
	 * @param value
	 *            The new Value of Attribute
	 * @return succes of set of the Value
	 */
	public boolean set(String attribute, Object value) {
		if (style.set(attribute, value)) {
			return true;
		}
		return false;
	}

	/**
	 * Get The Value of Attribute.
	 *
	 * @param key
	 *            The Key of Attribute
	 * @return The Value of Attribute
	 */
	public Object get(String key) {
		Object attrValue = style.get(key);
		if (attrValue != null) {
			return attrValue;
		}
		return super.get(key);
	}

	/** @return Is XML is Bold */
	public boolean isBold() {
		return style.isBold();
	}

	/**
	 * @param value
	 *            The new Option of Bold
	 */
	public void setBold(boolean value) {
		style.withBold(value);
	}
}
