package de.uniks.networkparser.xml;

import de.uniks.networkparser.Style;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

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
		if (style == null || sb == null) {
			return;
		}
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
	 * @param attribute The Attribute Key
	 * @param value     The new Value of Attribute
	 * @return success of set of the Value
	 */
	public boolean set(String attribute, Object value) {
		if (style.setValue(style, attribute, value, SendableEntityCreator.NEW)) {
			return true;
		}
		return false;
	}

	/**
	 * Get The Value of Attribute.
	 *
	 * @param key The Key of Attribute
	 * @return The Value of Attribute
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
	 * 
	 * @param value The new Option of Bold
	 */
	public void setBold(boolean value) {
		style.withBold(value);
	}
}
