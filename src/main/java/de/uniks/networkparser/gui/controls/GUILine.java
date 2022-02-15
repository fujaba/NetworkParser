package de.uniks.networkparser.gui.controls;

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

/**
 * The Class GUILine.
 *
 * @author Stefan
 */
public class GUILine {
	/** The Font-Size-Family value. */
	private String color;
	private boolean customLine;
	private String width;

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * With color.
	 *
	 * @param color the color
	 * @return the GUI line
	 */
	public GUILine withColor(String color) {
		this.color = color;
		return this;
	}

	/**
	 * Gets the width.
	 *
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * With width.
	 *
	 * @param width the width
	 * @return the GUI line
	 */
	public GUILine withWidth(String width) {
		this.width = width;
		return this;
	}

	/**
	 * Checks if is custom line.
	 *
	 * @return true, if is custom line
	 */
	public boolean isCustomLine() {
		return customLine;
	}

	/**
	 * With custom line.
	 *
	 * @param customLine the custom line
	 * @return the GUI line
	 */
	public GUILine withCustomLine(boolean customLine) {
		this.customLine = customLine;
		return this;
	}
}
