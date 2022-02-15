package de.uniks.networkparser.interfaces;

// TODO: Auto-generated Javadoc
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
 * The Enum GUIPosition.
 *
 * @author Stefan
 */
public enum GUIPosition {
	
	/** The center. */
	CENTER("Center"), 
 /** The east. */
 EAST("East"), 
 /** The north. */
 NORTH("North"), 
 /** The northwest. */
 NORTHWEST("NorthWest"), 
 /** The northeast. */
 NORTHEAST("NorthEast"), 
 /** The south. */
 SOUTH("South"),
	
	/** The southwest. */
	SOUTHWEST("SouthWest"), 
 /** The southeast. */
 SOUTHEAST("SouthEast"), 
 /** The west. */
 WEST("West"), 
 /** The all. */
 ALL("All");
	
	/** The value. */
	private String value;

	/**
	 * Instantiates a new GUI position.
	 *
	 * @param value the value
	 */
	GUIPosition(String value) {
		this.setValue(value);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
