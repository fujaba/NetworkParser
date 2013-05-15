package de.uniks.jism;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import de.uniks.jism.interfaces.PeerMessage;

public class Style implements PeerMessage{
	/** The Constant PROPERTY_BOLD for Bold Attribute */
	public static final String PROPERTY_BOLD = "bold";
	/** The Bold value. */
	private boolean bold;

	/** The Constant PROPERTY_ITALIC for Italic Attribute */
	public static final String PROPERTY_ITALIC = "italic";
	/** The Italic value. */
	private boolean italic;

	/** The Constant PROPERTY_FONT for Font-Family Attribute */
	public static final String PROPERTY_FONTFAMILY = "fontfamily";
	/** The Font-Family value. */
	private String fontfamily;

	/** The Constant PROPERTY_FONTSIZE for Font-Size Attribute */
	public static final String PROPERTY_FONTSIZE = "size";
	/** The Font-Size-Family value. */
	private String fontsize;

	/** The Constant PROPERTY_FORGROUND for Color of Font Attribute */
	public static final String PROPERTY_FORGROUND = "foreground";
	/** The Foreground-Color. */
	private String forground;

	/** The Constant PROPERTY_BACKGROUND for Color of Background Attribute */
	public static final String PROPERTY_BACKGROUND = "background";
	/** The Font-Size-Family value. */
	private String background;

	/**
	 * Set the Settings
	 */
	public Style withSettings(Object... fields){
		if (fields.length % 2 == 0) {
			for (int z = 0; z < fields.length; z += 2) {
				set((String)fields[z], fields[z + 1]);
			}
		}
		return this;
	}

	public boolean isBold() {
		return bold;
	}

	public Style withBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public boolean isItalic() {
		return italic;
	}

	public Style withItalic(boolean italic) {
		this.italic = italic;
		return this;
	}

	public String getFontFamily() {
		return fontfamily;
	}

	public Style withFontFamily(String fontFamily) {
		this.fontfamily = fontFamily;
		return this;
	}

	public String getFontSize() {
		return fontsize;
	}

	public Style withFontSize(String size) {
		this.fontsize = size;
		return this;
	}

	/*
	 * Generic Getter for Attributes
	 */
	@Override
	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_BOLD)) {
			return isBold();
		} else if (attribute.equalsIgnoreCase(PROPERTY_ITALIC)) {
			return isItalic();
		} else if (attribute.equalsIgnoreCase(PROPERTY_FONTFAMILY)) {
			return getFontFamily();
		} else if (attribute.equalsIgnoreCase(PROPERTY_FONTSIZE)) {
			return getFontSize();
		} else if (attribute.equalsIgnoreCase(PROPERTY_FORGROUND)) {
			return getForground();
		} else if (attribute.equalsIgnoreCase(PROPERTY_BACKGROUND)) {
			return getBackground();
		}
		return null;
	}

	/*
	 * Generic Setter for Attributes
	 */
	@Override
	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_BOLD)) {
			withBold((Boolean) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_ITALIC)) {
			withItalic((Boolean) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_FONTFAMILY)) {
			withFontFamily((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_FONTSIZE)) {
			withFontSize(value.toString());
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_FORGROUND)) {
			withForground((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_BACKGROUND)) {
			withBackground((String) value);
			return true;
		}
		return false;
	}

	public String getForground() {
		return forground;
	}

	public Style withForground(String forground) {
		this.forground = forground;
		return this;
	}

	public String getBackground() {
		return background;
	}

	public Style withBackground(String background) {
		this.background = background;
		return this;
	}
}
