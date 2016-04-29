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

	@Override
	protected CharacterBuffer parseString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
		if(quotes== null) {
			sc.with(getCurrentChar());
			return sc;
		}
		int startpos = this.position();
		char c;
//		boolean isQuote = false;
		char b = getCurrentChar();
		int i, quoteLen=quotes.length;
		do {
			c = getChar();
			switch (c) {
			case 0:
				c=0;
				break;
			case '\n':
			case '\r':
				break;
			default:
				if (b == '\\') {
					if (c == '\\') {
						if (allowQuote) {
							c = 0;
						}
//						isQuote = false;
					}
					if (allowQuote) {
						b = c;
						c = 1;
						continue;
					}
//					isQuote = true;
				}
			}
			b = c;
			for(i=0;i<quoteLen;i++) {
				if (c == quotes[i]) {
					c=0;
					break;
				}
			}
		} while (c != 0);

		int endPos = this.position();
		if (nextStep) {
			skip();
		}
		sc.with(this.subSequence(startpos, endPos));
		return sc;
	}

	/**
	 * Get the next n characters.
	 *
	 * @param n
	 *			The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not
	 *		 n characters remaining in the source string.
	 */
	public String getNextString(int n) {
		int pos = 0;
		if (n < -1) {
			n = n * -1;
			char[] chars = new char[n];
			while (pos < n) {
				chars[pos] = this.charAt(this.position()
						- (n - pos++));
			}
			return new String(chars);
		} else if (n == -1) {
			n = length() - this.position();
		} else if (n == 0) {
			return "";
		} else if (position() + n > length()) {
			n = length() - position();
		}
		char[] chars = new char[n];

		while (pos < n) {
			chars[pos] = charAt(position() + pos++);
		}
		return new String(chars);
	}

	@Override
	protected CharacterBuffer nextValue(char c, boolean allowDuppleMark) {
		int start = position();
		if(allowDuppleMark) {
			while (c >= ' ' && STOPCHARSXML.indexOf(c) < 0) {
				c = getChar();
			}
		}else {
			while (c >= ' ' && STOPCHARSJSON.indexOf(c) < 0) {
				c = getChar();
			}
		}
		CharacterBuffer sb = subSequence(start, position()).trim();
		return sb;
	}

	/**
	 * @param positions
	 *            first is start Position, second is Endposition
	 *
	 *            Absolut fix Start and End start&gt;0 StartPosition
	 *            end&gt;Start EndPosition
	 *
	 *            Absolut from fix Position Start&gt;0 Position end NULL To End
	 *            end -1 To this.index
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
			end = length();
			if(start==-1) {
				start = this.position();
			}
		} else {
			end = positions[1];
		}
		if (end == -1) {
			end = this.position();
		} else if (end == 0) {
			if (start < 0) {
				end = this.position();
				start = this.position() + start;
			} else {
				end = this.position() + start;
				if (this.position() + end > length()) {
					end = length();
				}
				start = this.position();
			}
		}
		if (start < 0 || end <= 0 || start > end) {
			return "";
		}
		return subSequence(start, end).toString();
	}
}
