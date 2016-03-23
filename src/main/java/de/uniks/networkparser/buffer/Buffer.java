package de.uniks.networkparser.buffer;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.list.SimpleList;

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

public abstract class Buffer implements BufferItem {
	public final static String STOPCHARSJSON = ",:]}/\\\"[{;=# ";
	public final static String STOPCHARSXML = ",]}/\\\"[{;=# ";
	public final static String STOPCHARSXMLEND = ",]}/\\\"[{;=#> ";
//	private byte lookAHeadByte;
//	private boolean isLookAhead;

	/** The index. */
	protected int position;

	public short getShort() {
		byte[] bytes = array(Short.SIZE /Byte.SIZE, false);
		short result = bytes[0];
		result = (short) (result << 8 + bytes[1]);
		return result;
	}

	public int getInt() {
		byte[] bytes = array(Integer.SIZE /Byte.SIZE, false);
		return (int) ((bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3]);
	}

	public long getLong() {
		byte[] bytes = array(Long.SIZE /Byte.SIZE, false);
		long result = bytes[0];
		result = result << 8 + bytes[1];
		result = result << 8 + bytes[2];
		result = result << 8 + bytes[3];
		result = result << 8 + bytes[4];
		result = result << 8 + bytes[5];
		result = result << 8 + bytes[6];
		result = result << 8 + bytes[7];
		return result;
	}
	
	public float getFloat() {
		int asInt = getInt();
		return Float.intBitsToFloat(asInt);
	}

	public double getDouble() {
		long asLong = getLong();
		return Double.longBitsToDouble(asLong);
	}

	public byte[] array(int len, boolean current) {
		if(len==-1) {
			len = remaining();
		}else if(len == -2) {
			len = length();
		}
		if(len<0) {
			return null;
		}
		byte[] result = new byte[len];
		int start=0;
		if(current) {
			if(len>0 && position <0) {
				position = 0;
			}
			result[0] = (byte) getCurrentChar();
			start = 1;
		}
		for(int i=start; i < len;i++) {
			result[i] = getByte();
		}
//		}
		return result;
	}
	
	@Override
	public byte getByte() {
//		if(isLookAhead) {
//			isLookAhead = false;
//			return lookAHeadByte;
//		}
//		isLookAhead = true;
//		char item = getChar();
//		lookAHeadByte = (byte) (item&0x00FF);
//		return (byte) ((item&0xFF00)>>8);
		return (byte)getChar();
	}
	
	@Override
	public int position() {
		return position;
	}

	@Override
	public int remaining() {
		return length() - position() - 1;
	}

	@Override
	public boolean isEmpty() {
        return length() == 0;
    }

	@Override
	public boolean isEnd() {
		return position() >= length();
	}

	public CharacterBuffer getString(int len) {
		CharacterBuffer result = new CharacterBuffer();
		if(len<1) {
			return result;
		}
		result.withBufferLength(len);
		result.with(getCurrentChar());
		for(int i = 1; i < len; i++) {
			result.with(getChar());
		}
		return result;
	}

	@Override
	public char nextClean(boolean currentValid) {
		if(position< 0) {
			position = 0;
		}
		char c = getCurrentChar();
		if (currentValid && c > ' ') {
			return c;
		}
		do {
			c = getChar();
		} while (c != 0 && c <= ' ');
		return c;
	}

	@Override
	public CharacterBuffer nextString() {
		return nextString(new CharacterBuffer(), false, false, '"');
	}

	@Override
	public CharacterBuffer nextString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
		if (getCurrentChar() == 0 || quotes == null) {
			return sc;
		}
		char c = getCurrentChar();
		int i=0;
		for(i=0;i<quotes.length;i++) {
			if (c == quotes[i]) {
				break;
			}
		}
		if (i < quotes.length) {
			if (nextStep) {
				skip();
			}
			return sc;
		}
		parseString(sc, allowQuote, nextStep, quotes);
		return sc;
	}

	protected CharacterBuffer parseString(CharacterBuffer sc, boolean allowQuote, boolean nextStep, char... quotes) {
		sc.with(getCurrentChar());
		if(quotes== null) {
			return sc;
		}
		char c, b = 0;
		int i;
		boolean isQuote = false;

		int quoteLen=quotes.length;
		do {
			c = getChar();
			i=quoteLen;
			switch (c) {
			case 0:
				return sc;
			case '\n':
			case '\r':
			default:
				if (b == '\\') {
					if (allowQuote) {
						sc.with(c);
						if (c == '\\') {
							c = 1;
						}
						b = c;
						c = 1;
						continue;
					} else if (c == '\\') {
						c = 1;
					}
					isQuote = true;
				}
				for(i=0;i<quoteLen;i++) {
					if (c != quotes[i]) {
						break;
					}
				}
				if (i == quoteLen) {
					sc.with(c);
				}
				b = c;
			}
			if (i < quoteLen) {
				c=0;
			}
		} while (c != 0);
		if (nextStep) {
			skip();
		}
		if (isQuote) {
			sc.remove(sc.length() - 1);
		}
		return sc;
	}

	protected CharacterBuffer nextValue(char c, boolean allowDuppleMark) {
		CharacterBuffer sb = new CharacterBuffer();
		if(allowDuppleMark) {
			while (c >= ' ' && STOPCHARSXML.indexOf(c) < 0) {
				sb.with(c);
				c = getChar();
			}
		}else {
			while (c >= ' ' && STOPCHARSJSON.indexOf(c) < 0) {
				sb.with(c);
				c = getChar();
			}
		}
		return sb.trim();
	}

	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMark, char c) {
		CharacterBuffer value = nextValue(c, allowDuppleMark);
		if (value.length() < 1) {
			return null;
		}
		if (value.equals("")) {
			return value;
		}
		if (value.equalsIgnoreCase("true")) {
			return Boolean.TRUE;
		}
		if (value.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		if (value.equalsIgnoreCase("null")) {
			return null;
		}
		/*
		 * If it might be a number, try converting it. If a number cannot be
		 * produced, then the value will just be a string. Note that the plus
		 * and implied string conventions are non-standard. A JSON parser may
		 * accept non-JSON forms as long as it accepts all correct JSON forms.
		 */
		Double d;
		char b = value.charAt(0);
		if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
			try {
				if (value.indexOf('.') > -1 || value.indexOf('e') > -1
						|| value.indexOf('E') > -1) {
					d = Double.valueOf(value.toString());
					if (!d.isInfinite() && !d.isNaN()) {
						return d;
					}
				} else {
					Long myLong = Long.valueOf(value.toString());
					if (myLong.longValue() == myLong.intValue()) {
						return Integer.valueOf(myLong.intValue());
					}
					return myLong;
				}
			} catch (Exception ignore) {
				// DO nothing
			}
		}
		return value;
	}

	@Override
	public boolean skipTo(char search, boolean notEscape) {
		int len = length();
		char lastChar = 0;
		if (this.position() > 0 && this.position() < len) {
			lastChar = this.getCurrentChar();
		}
		while (this.position() < len) {
			char currentChar = getCurrentChar();
			if (currentChar == search
					&& (!notEscape || lastChar != '\\')) {
				return true;
			}
			lastChar = currentChar;
			skip();
		}
		return false;
	}

	@Override
	public boolean skipTo(String search, boolean order, boolean notEscape) {
		char[] character = search.toCharArray();
		int z = 0;
		int strLen = character.length;
		int len = length();
		char lastChar = 0;
		if (this.position() > 0 && this.position() < len) {
			lastChar = this.getCurrentChar();
		}
		while (this.position() < len) {
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
			skip();
		}
		return false;
	}

	@Override
	public boolean skip(int pos) {
		while (pos > 0) {
			if (getChar() == 0) {
				return false;
			}
			pos--;
		}
		return true;
	}
	@Override
	public boolean skip() {
		return getChar() != 0;
	}

	@Override
	public CharacterBuffer nextToken(String stopWords) {
		nextClean(false);
		CharacterBuffer characterBuffer = new CharacterBuffer();
		char c = getCurrentChar();
		char[] stops = new char[stopWords.length()];
		for(int i=0;i<stopWords.length();i++) {
			stops[i] = stopWords.charAt(i);
			if(stops[i] == c) {
				return characterBuffer;
			}
		}
		parseString(characterBuffer, true, false, stops);
		return characterBuffer;
	}

	@Override
	public boolean checkValues(char... items) {
		char current = getCurrentChar();
		for (char item : items) {
			if (current == item) {
				return true;
			}
		}
		return false;
	}
	@Override
	public SimpleList<String> getStringList() {
		SimpleList<String> list = new SimpleList<String>();
		CharacterBuffer sc = new CharacterBuffer();
		do {
			sc.reset();
			nextString(sc, true, true, '"');
			if (sc.length() > 0) {
				if (sc.indexOf('\"')>=0) {
					list.add("\"" + sc + "\"");
				} else {
					list.add(sc.toString());
				}
			}
		} while (sc.length() > 0);
		return list;
	}

	@Override
	public SimpleList<String> splitStrings(String value, boolean split) {
		SimpleList<String> result = new SimpleList<String>();
		if (value.startsWith("\"") && value.endsWith("\"")) {
			result.add(value.substring(1, value.length() - 1));
			return result;
		}
		String[] values = value.split(" ");
		for (String item : values) {
			result.add(item);
		}
		return result;
	}

	@Override
	public char skipChar(char... quotes) {
		char c = getCurrentChar();
		if(quotes == null) {
			return c;
		}
		boolean found;
		do {
			found=false;
			for(int i=0;i<quotes.length;i++) {
				if(quotes[i] == c) {
					found = true;
					break;
				}
			}
			if(found == false) {
				break;
			}
			c = getChar();
		} while(c!=0);
		return c;
	}
}