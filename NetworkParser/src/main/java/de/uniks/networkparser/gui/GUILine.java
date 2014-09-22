package de.uniks.networkparser.gui;

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

public class GUILine {
	/** The Font-Size-Family value. */
	private String color;
	private boolean customLine;
	private String width;

	public String getColor() {
		return color;
	}

	public GUILine withColor(String color) {
		this.color = color;
		return this;
	}

	public String getWidth() {
		return width;
	}

	public GUILine withWidth(String width) {
		this.width = width;
		return this;
	}

	public boolean isCustomLine() {
		return customLine;
	}

	public GUILine withCustomLine(boolean customLine) {
		this.customLine = customLine;
		return this;
	}
}
