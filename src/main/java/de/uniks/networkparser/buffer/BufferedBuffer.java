package de.uniks.networkparser.buffer;

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

public abstract class BufferedBuffer extends Buffer {
	/** The count is the number of characters used. */
	protected int length;

	/** The start is the number of characters started. */
	int start;

	public boolean back() {
		if (this.position > 0) {
			this.position--;
			return true;
		}
		return false;
	}

	public BufferedBuffer withPosition(int value) {
		this.position = value;
		return this;
	}

	@Override
	public int length() {
		return length;
	}
	
	public BufferedBuffer withLength(int value) {
		this.length = value;
		return this;
	}

	public abstract byte byteAt(int index);

	public abstract char charAt(int index);
	
	/**
	 * @return The currentChar
	 */
	public char getCurrentChar() {
		return charAt(position());
	}

	/**
	 * @param start
	 *			startindex for parsing
	 * @param length
	 *			the length of Substring
	 * @return the Substring
	 */
	public abstract CharacterBuffer subSequence(int start, int length);
	
	@Override
	public BufferedBuffer withLookAHead(CharSequence lookahead) {
		if(lookahead == null) {
			return this;
		}
		this.withPosition(this.position() - lookahead.length() + 1);
		return this;
	}
	
	@Override
	public BufferedBuffer withLookAHead(char lookahead) {
		if(lookahead < 0) {
			return this;
		}
		this.withPosition(this.position() - 1);
		return this;
	}
}
