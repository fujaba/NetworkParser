package de.uniks.networkparser.xml;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.Style;
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
