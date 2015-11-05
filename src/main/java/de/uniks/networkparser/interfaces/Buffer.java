package de.uniks.networkparser.interfaces;

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
/**
 * INterface for Buffer For Tokener to parse some Values
 *
 */

public abstract class Buffer {
	/** The index. */
	protected int position;

	/**
	 * @return the length of the buffer
	 */
	public abstract int length();

	/**
	 * @return The next Char
	 */
	public abstract char getChar();

	/**
	 * @return The currentChar
	 */
	public abstract char getCurrentChar();
	
	public String getString(int len) {
		if(len<1) {
			return "";
		}
		char[] values = new char[len];
		values[0] = getCurrentChar();
		for(int i = 1; i < len; i++) {
			values[i] = getChar();
		}
		return new String(values); 
	}

	public int position() {
		return position;
	}

	public int remaining() {
		return length() - position();
	}

	public boolean isEnd() {
		return position() >= length();
	}

	public abstract String toText();

	public abstract byte[] toArray();
	
	/**
	 * @param lookahead The String for look A Head String. For Simple Buffer change position back to the length of String or Save the String.
	 * @return Self Instance
	 */
	public abstract Buffer withLookAHead(CharSequence lookahead);
	public abstract Buffer withLookAHead(char current);
}
