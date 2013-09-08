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

import de.uniks.jism.exceptions.TextParsingException;
import de.uniks.jism.interfaces.BaseEntity;
import de.uniks.jism.interfaces.JISMEntity;
import de.uniks.jism.interfaces.BaseEntityList;

/**
 * The Class Tokener.
 */
public abstract class Tokener {
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	
	/** BUFFER */
	protected Buffer buffer;
	
	/**
	 * Reset the Tokener
	 * 
	 * @param value
	 */
	public Tokener withText(String value) {
		this.buffer = new CharacterBuffer(value);
		return this;
	}

	/**
	 * Back up one character. This provides a sort of lookahead capability, so
	 * that you can test for a digit or letter before attempting to parse the
	 * next number or identifier.
	 */
	public void back() {
		if (this.buffer.length() <= 0) {
			throw new RuntimeException(
					"Stepping back two steps is not supported");
		}
		this.buffer.back();
	}

	/**
	 * Check if End of String
	 * 
	 * @return true, if successful
	 */
	public boolean isEnd() {
		return buffer.isEnd();
	}

	/**
	 * Get the next character in the source string.
	 * 
	 * @return The next character, or 0 if past the end of the source string.
	 */
	public char next() {
		if (this.isEnd()) {
			return 0;
		}
		return buffer.getChar();
	}

	/**
	 * Get the next n characters.
	 * 
	 * @param n
	 *            The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not
	 *         n characters remaining in the source string.
	 */
	public String getNextString(int n) {
		int pos = 0;
		if(n<-1){
			n=n*-1;
			char[] chars = new char[n];
			while (pos < n) {
				chars[pos] = this.buffer.charAt(this.buffer.position() - (n-pos++));
			}
			return new String(chars);
		}else if (n == -1) {
			n = buffer.length() - this.buffer.position();
		} else if (n == 0) {
			return "";
		} else if (this.buffer.position() + n > this.buffer.length()) {
			n = buffer.length() - this.buffer.position();
		}
		char[] chars = new char[n];

		while (pos < n) {
			chars[pos] = this.buffer.charAt(this.buffer.position() + pos++);
		}
		return new String(chars);
	}

	/**
	 * Get the next n characters.
	 * 
	 * @param n
	 *            The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not
	 *         n characters remaining in the source string.
	 */
	public String skipPos(int n) {
		if (n == -1) {
			n = buffer.remaining();
		} else if (n == 0) {
			return "";
		}

		char[] chars = new char[n];
		int pos = 0;

		while (pos < n) {
			chars[pos] = next();
			if (isEnd()) {
				throw new TextParsingException("Substring bounds error", this);
			}
			pos += 1;
		}
		return new String(chars);
	}

	/**
	 * Get the next char in the string, skipping whitespace.
	 * 
	 * @return A character, or 0 if there are no more characters.
	 */
	public char nextClean() {
		char c=getCurrentChar();
		do{
			c = next();
		}while(c!=0 && c <= ' ');
		return c;
	}
	
	public char nextStartClean() {
		char c=getCurrentChar();
		if(c!=0 && c <= ' '){
			c=nextClean();
		}
		return c;
	}
	
	/**
	 * Return the characters up to the next close quote character. Backslash
	 * processing is done. The formal JSON format does not allow strings in
	 * single quotes, but an implementation is allowed to accept them.
	 * 
	 * @param quote
	 *            The quoting character, either <code>"</code>
	 *            &nbsp;<small>(double quote)</small> or <code>'</code>
	 *            &nbsp;<small>(single quote)</small>.
	 * @param quote
	 *            allowCRLF
	 * @return A String.
	 */
	public String nextString(char quote, boolean allowCRLF) {
		if(getCurrentChar()==0){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(getCurrentChar());

		char c;
		do{
			c = next();
			switch (c) {
			case 0:
			case '\n':
			case '\r':
				if (!allowCRLF) {
					throw new TextParsingException("Unterminated string", this);
				}
				break;
			case '\\':
				c = next();
				switch (c) {
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'u':
					sb.append((char) Integer.parseInt(skipPos(4), 16));
					break;
				case '"':
				case '\'':
				case '\\':
				case '/':
					sb.append(c);
					c = 1;
					break;
				default:
					throw new TextParsingException("Illegal escape.", this);
				}
				break;
			default:
				if (c != quote) {
					sb.append(c);
				}
			}
		}while (c != 0 && c != quote);

		next();
		return sb.toString();
	}

	/**
	 * Handle unquoted text. This could be the values true, false, or null, or
	 * it can be a number. An implementation (such as this one) is allowed to
	 * also accept non-standard forms.
	 * 
	 * Accumulate characters until we reach the end of the text or a formatting
	 * character.
	 */
	public Object nextValue(JISMEntity creator) {
		char c = nextStartClean();
		StringBuilder sb = new StringBuilder();
		while (c >= ' ' && getStopChars().indexOf(c) < 0) {
			sb.append(c);
			c = next();
		}

		String value = sb.toString().trim();
		if (value.length()<1) {
			throw new TextParsingException("Missing value", this);
		}
		return EntityUtil.stringToValue(value);
	}

	protected String getStopChars() {
		return STOPCHARS;
	}

	/**
	 * Skip.
	 * 
	 * @param pos
	 *            the pos
	 * @return true, if successful
	 */
	public boolean skip(int pos) {
		while (pos > 0) {
			if (next() == 0) {
				return false;
			}
			pos--;
		}
		return true;
	}

	/**
	 * Skip.
	 * 
	 * @param search
	 *            the The String of searchelements
	 * @param notSearch
	 *            the String of elements with is not in string
	 * @param order
	 *            the if the order of search element importent
	 * @return true, if successful
	 */
	public boolean stepPos(String search, boolean order, boolean notEscape) {
		char[] character = search.toCharArray();
		int z = 0;
		int strLen = character.length;
		int len = buffer.length();
		char lastChar = 0;
		if (this.buffer.position() > 0 && this.buffer.position() < len) {
			lastChar = this.buffer.charAt(this.buffer.position() - 1);
		}
		while (this.buffer.position() < len) {
			char currentChar = getCurrentChar();
			if (order) {
				if (currentChar == character[z]) {
					z++;
					if (z >= strLen) {
						return true;
					}
				} else {
					z = 0;
				}
			} else {
				for (char zeichen : character) {
					if (currentChar == zeichen
							&& (!notEscape || lastChar != '\\')) {
						return true;
					}
				}
			}
			lastChar = currentChar;
			next();
		}
		return false;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int position() {
		return this.buffer.position();
	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public int length() {
		return buffer.length();
	}

	/**
	 * Make a printable string of this JSONTokener.
	 * 
	 * @return " at {index} [character {character} line {line}]"
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}

	/**
	 * Char at.
	 * 
	 * @param pos the Position of the bufferarray
	 * @return the char
	 */
	public char charAt(int pos) {
		return this.buffer.charAt(pos);
	}

	/**
	 * Gets the current char.
	 * 
	 * @return the current char
	 */
	public char getCurrentChar() {
		if (buffer.remaining()>0) {
			return this.buffer.charAt(this.buffer.position());
		}
		return 0;
	}

	/**
	 * @param positions
	 *            first is start Position, second is Endposition
	 * 
	 *            Absolut fix Start and End start>0 StartPosition end>Start
	 *            EndPosition
	 * 
	 *            Absolut from fix Position Start>0 Position end NULL To End end
	 *            -1 To this.index
	 * 
	 *            Relativ from indexPosition Start Position from this.index +
	 *            (-Start) End = 0 current Position
	 * 
	 * @return substring from buffer
	 */
	public String substring(int... positions) {
		int start = positions[0], end = -1;
		if (positions.length < 2) {
			// END IS END OF BUFFER (Exclude)
			end = buffer.length();
		} else {
			end = positions[1];
		}
		if (end == -1) {
			end = this.buffer.position();
		} else if (end == 0) {
			if (start < 0) {
				end = this.buffer.position();
				start = this.buffer.position() + start;
			} else {
				end = this.buffer.position() + start;
				if (this.buffer.position() + end > buffer.length()) {
					end = buffer.length();
				}
				start = this.buffer.position();
			}
		}
		if (start < 0 || end <= 0 || start > end) {
			return "";
		}
		return this.buffer.substring(start, end-start);
	}

	/**
	 * Check values.
	 * 
	 * @param items
	 *            the items
	 * @return true, if successful
	 */
	public boolean checkValues(char... items) {
		char current = this.buffer.charAt(this.buffer.position());
		for (char item : items) {
			if (current == item) {
				return true;
			}
		}
		return false;
	}

	public String getNextTag() {
		nextClean();
		int startTag = this.buffer.position();
		if (stepPos(" >//<", false, true)) {
			return this.buffer.substring(startTag, this.buffer.position()-startTag);
		}
		return "";
	}

	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index) {
		this.buffer.withPosition(index);
	}
	
	public byte[] toArray(){
		return buffer.toArray();
	}
	
	public String toText(){
		return buffer.toText();
	}

	public abstract void parseToEntity(BaseEntity entity);

	public abstract void parseToEntity(BaseEntityList entityList);
}
