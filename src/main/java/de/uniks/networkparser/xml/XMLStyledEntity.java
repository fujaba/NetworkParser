package de.uniks.networkparser.xml;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.gui.Style;
/**
 * Style Element of XML.
 *
 * @author Stefan
 */

public class XMLStyledEntity extends XMLEntity {
	/** The Styles of XMLStyledEntity. */
	private Style style = new Style();

	@Override
	protected void toStringChildren(CharacterBuffer sb, EntityStringConverter converter) {
		// Starttag
		if (style.isBold()) {
			sb.with("<b>");
		}
		if (style.isItalic()) {
			sb.with("<i>");
		}
		super.toStringChildren(sb, converter);

		// EndTag
		if (style.isItalic()) {
			sb.with("</i>");
		}
		if (style.isBold()) {
			sb.with("</b>");
		}
	}

	/**
	 * Set new Value of Attribute.
	 *
	 * @param attribute	The Attribute Key
	 * @param value		The new Value of Attribute
	 * @return 			success of set of the Value
	 */
	public boolean set(String attribute, Object value) {
		if (style.setValue(style, attribute, value, IdMap.NEW)) {
			return true;
		}
		return false;
	}

	/**
	 * Get The Value of Attribute.
	 *
	 * @param key	The Key of Attribute
	 * @return 		The Value of Attribute
	 */
	public Object get(String key) {
		Object attrValue = style.getValue(style, key);
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
	 * Add Switch for bold Text
	 * @param value	The new Option of Bold
	 */
	public void setBold(boolean value) {
		style.withBold(value);
	}
}
