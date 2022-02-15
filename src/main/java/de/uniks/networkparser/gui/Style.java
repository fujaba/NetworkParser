package de.uniks.networkparser.gui;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.HashMap;
import java.util.Map;

import de.uniks.networkparser.gui.controls.GUILine;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

/**
 * The Class Style.
 *
 * @author Stefan
 */
public class Style implements Cloneable, SendableEntityCreatorNoIndex {
	
	/**  The Constant PROPERTY_NAME for Name of Style. */
	public static final String PROPERTY_NAME = "name";
	private String name;

	/**  The Constant PROPERTY_BOLD for Bold Attribute. */
	public static final String PROPERTY_BOLD = "bold";
	/** The Bold value. */
	private boolean bold;

	/**  The Constant PROPERTY_ITALIC for Italic Attribute. */
	public static final String PROPERTY_ITALIC = "italic";
	/** The Italic value. */
	private boolean italic;

	/**  The Constant PROPERTY_FONT for Font-Family Attribute. */
	public static final String PROPERTY_FONTFAMILY = "fontfamily";
	/** The Font-Family value. */
	private String fontfamily;

	/**  The Constant PROPERTY_FONTSIZE for Font-Size Attribute. */
	public static final String PROPERTY_FONTSIZE = "size";
	/** The Font-Size-Family value. */
	private String fontsize;

	/**  The Constant PROPERTY_FORGROUND for Color of Font Attribute. */
	public static final String PROPERTY_FORGROUND = "foreground";
	/** The Foreground-Color. */
	private String forground;

	/**  The Constant PROPERTY_BACKGROUND for Color of Background Attribute. */
	public static final String PROPERTY_BACKGROUND = "background";
	/** The Font-Size-Family value. */
	private String background;

	/**  The Constant PROPERTY_BACKGROUND for Color of Background Attribute. */
	public static final String PROPERTY_UNDERLINE = "underline";
	/** The Underline value. */
	private boolean underline;

	/**  The Constant PROPERTY_BACKGROUND for Color of Background Attribute. */
	public static final String PROPERTY_ALIGNMENT = "alignment";
	/** The Underline value. */
	private String alignment;

	/**  The Constant PROPERTY_WIDTH for Width of Width. */
	public static final String PROPERTY_WIDTH = "width";
	/** The Width value. */
	private double width;

	/**  The Constant PROPERTY_HEIGHT for Height of Height. */
	public static final String PROPERTY_HEIGHT = "height";
	/** The Height value. */
	private double height;

	/** The Constant PROPERTY_BORDER. */
	public static final String PROPERTY_BORDER = "borders";

	protected HashMap<GUIPosition, GUILine> borders = new HashMap<GUIPosition, GUILine>();

	/**
	 * Checks if is bold.
	 *
	 * @return true, if is bold
	 */
	public boolean isBold() {
		return bold;
	}

	/**
	 * With bold.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withBold(boolean value) {
		Boolean oldValue = this.bold;
		this.bold = value;
		propertyChange(PROPERTY_BOLD, oldValue, value);
		return this;
	}

	/**
	 * Checks if is italic.
	 *
	 * @return true, if is italic
	 */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * With italic.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withItalic(boolean value) {
		Boolean oldValue = this.italic;
		this.italic = value;
		propertyChange(PROPERTY_ITALIC, oldValue, value);
		return this;
	}

	/**
	 * Gets the font family.
	 *
	 * @return the font family
	 */
	public String getFontFamily() {
		return fontfamily;
	}

	/**
	 * With font family.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withFontFamily(String value) {
		String oldValue = this.fontfamily;
		this.fontfamily = value;
		propertyChange(PROPERTY_FONTFAMILY, oldValue, value);
		return this;
	}

	/**
	 * Gets the font size.
	 *
	 * @return the font size
	 */
	public String getFontSize() {
		return fontsize;
	}

	/**
	 * With font size.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withFontSize(String value) {
		String oldValue = this.fontsize;
		this.fontsize = value;
		propertyChange(PROPERTY_FONTSIZE, oldValue, value);
		return this;
	}

	/**
	 * Gets the forground.
	 *
	 * @return the forground
	 */
	public String getForground() {
		return forground;
	}

	/**
	 * With forground.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withForground(String value) {
		String oldValue = this.forground;
		this.forground = value;
		propertyChange(PROPERTY_FORGROUND, oldValue, value);
		return this;
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * With background.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withBackground(String value) {
		String oldValue = this.background;
		this.background = value;
		propertyChange(PROPERTY_BACKGROUND, oldValue, value);
		return this;
	}

    /**
     * Clone.
     *
     * @return new StyleObject
     */
	@Override
	public Style clone() {
		return clone(new Style());
	}

	/**
	 * Clone.
	 *
	 * @param prototyp the prototyp
	 * @return the style
	 */
	public Style clone(Style prototyp) {
		if (prototyp == null) {
			return null;
		}
		return prototyp.withFontFamily(fontfamily).withFontSize(fontsize).withForground(forground)
				.withBackground(background).withBold(bold).withItalic(italic).withAlignment(alignment)
				.withUnderline(underline).withWidth(width).withHeight(height);
	}

	/**
	 * Checks if is underline.
	 *
	 * @return true, if is underline
	 */
	public boolean isUnderline() {
		return underline;
	}

	/**
	 * With underline.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withUnderline(boolean value) {
		Boolean oldValue = this.underline;
		this.underline = value;
		propertyChange(PROPERTY_UNDERLINE, oldValue, value);
		return this;
	}

	/**
	 * Gets the alignment.
	 *
	 * @return the alignment
	 */
	public String getAlignment() {
		return alignment;
	}

	/**
	 * With alignment.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withAlignment(GUIPosition value) {
		String oldValue = this.alignment;
		this.alignment = "" + value;
		propertyChange(PROPERTY_ALIGNMENT, oldValue, value);
		return this;
	}

	/**
	 * With alignment.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withAlignment(String value) {
		String oldValue = this.alignment;
		this.alignment = value;
		propertyChange(PROPERTY_ALIGNMENT, oldValue, value);
		return this;
	}

	/**
	 * Gets the height.
	 *
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * With height.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withHeight(double value) {
		Double oldValue = this.height;
		this.height = value;
		propertyChange(PROPERTY_HEIGHT, oldValue, value);
		return this;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * With width.
	 *
	 * @param value the value
	 * @return the style
	 */
	public Style withWidth(double value) {
		Double oldValue = this.width;
		this.width = value;
		propertyChange(PROPERTY_WIDTH, oldValue, value);
		return this;
	}

	/**
	 * With border.
	 *
	 * @param position the position
	 * @param line the line
	 * @return the style
	 */
	public Style withBorder(GUIPosition position, GUILine line) {
		getBorders().put(position, line);
		propertyChange(PROPERTY_BORDER, null, position);
		return this;
	}

	/**
	 * Sets the border.
	 *
	 * @param position the position
	 * @param width the width
	 * @param color the color
	 */
	public void setBorder(GUIPosition position, String width, String color) {
		GUILine border = this.borders.get(position);
		if (width != null) {
			if (border == null) {
				this.borders.put(position, new GUILine().withColor(color).withWidth(width));
				this.propertyChange(PROPERTY_BORDER, null, this.borders);
			} else {
				if (!border.isCustomLine()) {
					border.withColor(color);
					border.withWidth(width);
					this.propertyChange(PROPERTY_BORDER, null, this.borders);
				}
			}
		} else if (border != null) {
			if (!border.isCustomLine()) {
				this.borders.remove(position);
				this.propertyChange(PROPERTY_BORDER, null, this.borders);
			}
		}
	}

	/**
	 * Gets the borders.
	 *
	 * @return the borders
	 */
	public Map<GUIPosition, GUILine> getBorders() {
		return borders;
	}

	/**
	 * With out border.
	 *
	 * @param position the position
	 * @return the style
	 */
	public Style withOutBorder(GUIPosition position) {
		GUILine removedItem = getBorders().remove(position);
		if (removedItem != null) {
			propertyChange(PROPERTY_BORDER, position, null);
		}
		return this;
	}

	/**
	 * Property change.
	 *
	 * @param property the property
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void propertyChange(String property, Object oldValue, Object newValue) {
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_NAME, PROPERTY_FONTFAMILY, PROPERTY_FONTSIZE, PROPERTY_FORGROUND,
				PROPERTY_BACKGROUND, PROPERTY_BOLD, PROPERTY_ITALIC, PROPERTY_UNDERLINE, PROPERTY_ALIGNMENT,
				PROPERTY_WIDTH, PROPERTY_HEIGHT };
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attrName the attr name
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attrName) {
		String attribute;
		if (attrName == null || !(entity instanceof Style)) {
			return false;
		}
		Style style = (Style) entity;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (PROPERTY_BOLD.equalsIgnoreCase(attribute)) {
			return style.isBold();
		}
		if (PROPERTY_ITALIC.equalsIgnoreCase(attribute)) {
			return style.isItalic();
		}
		if (PROPERTY_FONTFAMILY.equalsIgnoreCase(attribute)) {
			return style.getFontFamily();
		}
		if (PROPERTY_FONTSIZE.equalsIgnoreCase(attribute)) {
			return style.getFontSize();
		}
		if (PROPERTY_FORGROUND.equalsIgnoreCase(attribute)) {
			return style.getForground();
		}
		if (PROPERTY_BACKGROUND.equalsIgnoreCase(attribute)) {
			return style.getBackground();
		}
		if (PROPERTY_UNDERLINE.equalsIgnoreCase(attribute)) {
			return style.isUnderline();
		}
		if (PROPERTY_ALIGNMENT.equalsIgnoreCase(attribute)) {
			return style.getAlignment();
		}
		if (PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			return style.getWidth();
		}
		if (PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			return style.getHeight();
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return style.getName();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof Style)) {
			return false;
		}
		Style style = (Style) entity;
		if (PROPERTY_BOLD.equalsIgnoreCase(attribute)) {
			style.withBold((Boolean) value);
			return true;
		}
		if (PROPERTY_ITALIC.equalsIgnoreCase(attribute)) {
			style.withItalic((Boolean) value);
			return true;
		}
		if (PROPERTY_FONTFAMILY.equalsIgnoreCase(attribute)) {
			style.withFontFamily((String) value);
			return true;
		}
		if (PROPERTY_FONTSIZE.equalsIgnoreCase(attribute)) {
			if (value != null) {
				style.withFontSize(value.toString());
			}
			return true;
		}
		if (PROPERTY_FORGROUND.equalsIgnoreCase(attribute)) {
			style.withForground((String) value);
			return true;
		}
		if (PROPERTY_BACKGROUND.equalsIgnoreCase(attribute)) {
			style.withBackground((String) value);
			return true;
		}
		if (PROPERTY_UNDERLINE.equalsIgnoreCase(attribute)) {
			style.withUnderline((Boolean) value);
			return true;
		}
		if (PROPERTY_ALIGNMENT.equalsIgnoreCase(attribute)) {
			style.withAlignment((String) value);
			return true;
		}
		if (PROPERTY_WIDTH.equalsIgnoreCase(attribute)) {
			style.withWidth(Double.parseDouble("" + value));
			return true;
		}
		if (PROPERTY_HEIGHT.equalsIgnoreCase(attribute)) {
			style.withHeight(Double.parseDouble("" + value));
			return true;
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			style.withName((String) value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Style();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the style
	 */
	public Style withName(String name) {
		this.name = name;
		return this;
	}
}
