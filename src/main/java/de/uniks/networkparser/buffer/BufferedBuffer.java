package de.uniks.networkparser.buffer;

import de.uniks.networkparser.interfaces.BaseItem;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

public abstract class BufferedBuffer extends Buffer implements BaseItem {
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

	@Override
	public boolean isEnd() {
		if (position() - start + 1 < 0) {
			return true;
		}
		return position() - start + 1 >= length();
	}

	public boolean isEndCharacter() {
		if (position() - start + 1 >= length()) {
			return true;
		}
		return nextClean(true) == 0;
	}

	public BufferedBuffer withLength(int value) {
		this.length = value;
		return this;
	}

	public abstract byte byteAt(int index);

	public abstract char charAt(int index);

	/**
	 * Get the Current Character
	 * 
	 * @return The currentChar
	 */
	public char getCurrentChar() {
		return charAt(position());
	}

	/**
	 * Substring of Buffer
	 * 
	 * @param start       startindex for parsing
	 * @param endPosition the endPosition of Substring
	 * @return the Substring
	 */
	public abstract CharacterBuffer subSequence(int start, int endPosition);

	@Override
	public BufferedBuffer withLookAHead(CharSequence lookahead) {
		if (lookahead == null) {
			return this;
		}
		this.withPosition(this.position() - lookahead.length() + 1);
		return this;
	}

	@Override
	public BufferedBuffer withLookAHead(char lookahead) {
		if (lookahead < 0) {
			return this;
		}
		this.withPosition(this.position() - 1);
		return this;
	}

	@Override
	protected CharacterBuffer parseString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
		if (quotes == null) {
			sc.with(getCurrentChar());
			return sc;
		}
		int startpos = this.position();
		char c;
		char b = getCurrentChar();
		int i, quoteLen = quotes.length;
		do {
			c = getChar();
			switch (c) {
			case 0:
				c = 0;
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
					}
					if (allowQuote) {
						b = c;
						c = 1;
						continue;
					}
				}
			}
			b = c;
			for (i = 0; i < quoteLen; i++) {
				if (c == quotes[i]) {
					c = 0;
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
	 * @param n The number of characters to take.
	 * @return A string of n characters. Substring bounds error if there are not n
	 *         characters remaining in the source string.
	 */
	public String getNextString(int n) {
		int pos = 0;
		if (n < -1) {
			n = n * -1;
			char[] chars = new char[n];
			while (pos < n) {
				chars[pos] = this.charAt(this.position() - (n - pos++));
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
		if (allowDuppleMark) {
			while (c >= ' ' && STOPCHARSXML.indexOf(c) < 0) {
				c = getChar();
			}
		} else {
			while (c >= ' ' && STOPCHARSJSON.indexOf(c) < 0) {
				c = getChar();
			}
		}
		CharacterBuffer sb = subSequence(start, position()).trim();
		return sb;
	}

	public String nextString(int count) {
		int start = position();
		CharacterBuffer sb = subSequence(start, start + count).trim();
		return sb.toString();
	}

	/**
	 * @param positions first is start Position, second is Endposition
	 *
	 *                  Absolut fix Start and End start&gt;0 StartPosition
	 *                  end&gt;Start EndPosition
	 *
	 *                  Absolut from fix Position Start&gt;0 Position end NULL To
	 *                  End end -1 To this.index
	 *
	 *                  Relativ from indexPosition Start Position from this.index +
	 *                  (-Start) End = 0 current Position
	 *
	 * @return substring from buffer
	 */
	public String substring(int... positions) {
		if (positions == null || positions.length < 1) {
			positions = new int[] { -1 };
		}
		int start = positions[0], end = -1;
		if (positions.length < 2) {
			// END IS END OF BUFFER (Exclude)
			end = length();
			if (start == -1) {
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
	
	public String next(int... positions) {
		if(positions == null || positions.length != 1) {
			return substring(positions);
		}
		return subSequence(position(), position()+positions[0]).toString();
	}

	public byte[] toBytes(boolean... all) {
		byte[] result;
		int i = start;
		if (all != null && all.length > 0 && all[0]) {
			result = new byte[length];
		} else {
			if(length - position<0) {
				return null;
			}
			result = new byte[length - position];
			i = position;
		}

		for (; i < result.length; i++) {
			result[i] = byteAt(i);
		}
		return result;
	}

	public String toArrayString(boolean... addString) {
		CharacterBuffer sb = new CharacterBuffer();
		sb.with('[');

		byte[] byteArray = this.toBytes();
		if (byteArray != null && byteArray.length > 0) {
			sb.with("" + byteArray[0]);
			for (int i = 1; i < this.length; i++) {
				sb.with("," + byteArray[i]);
			}
		}
		sb.with(']');
		if (addString != null && addString.length > 0 && addString[0]) {
			sb.with(' ').with('(').with(new String(byteArray)).with(')');
		}
		return sb.toString();
	}

	public final void clear() {
		this.length = 0;
		this.start = 0;
		this.position = 0;
	}

	public abstract BufferedBuffer with(char[] buffer, int i, int readed);

	public abstract BufferedBuffer with(CharSequence... items);

	public abstract BufferedBuffer getNewList(boolean list);
}
