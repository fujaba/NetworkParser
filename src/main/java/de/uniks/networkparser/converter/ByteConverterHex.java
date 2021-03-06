package de.uniks.networkparser.converter;

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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class ByteConverterHex extends ByteConverter {
	private static final String HEXVAL = "0123456789ABCDEF";

	/**
	 * To hex string.
	 *
	 * @param values the bytes
	 * @return the string
	 */
	@Override
	public String toString(BufferedBuffer values) {
		return toString(values, 0);
	}

	public String toString(BufferedBuffer values, int space) {
		if (values == null) {
			return null;
		}
		CharacterBuffer returnValue = new CharacterBuffer()
				.withBufferLength(values.length() << 1 + values.length() * space);
		String step = EntityUtil.repeat(' ', space);
		for (int i = 0; i < values.length(); i++) {
			int value = values.byteAt(i);
			if (value < 0) {
				value += 256;
			}
			returnValue.with(HEXVAL.charAt(value / 16));
			returnValue.with(HEXVAL.charAt(value % 16));
			returnValue.with(step);
		}
		return returnValue.toString();
	}

	/**
	 * To byte string.
	 *
	 * @param value the hex string
	 * @return the byte[]
	 */
	@Override
	public byte[] decode(CharSequence value) {
		if (value == null) {
			return null;
		}
		return decoding(value, 0, value.length());
	}

	public static byte[] decoding(CharSequence value, int pos, int len) {
		if (value == null || len < value.length()) {
			return null;
		}
		byte[] out = new byte[len / 2];

		int n = len;

		for (int i = pos; i < n; i += 2) {
			/* make a bit representation in an int of the hex value */
			int hn = HEXVAL.indexOf(value.charAt(i));
			int ln = HEXVAL.indexOf(value.charAt(i + 1));

			/* now just shift the high order nibble and add them together */
			out[i / 2] = (byte) ((hn << 4) | ln);
		}
		return out;
	}

	public static char fromHex(CharSequence value, int pos, int len) {
		byte[] bytes = decoding(value, pos, len);
		if (bytes == null || bytes.length < 1) {
			return 0;
		}
		if (len == 4 && bytes.length > 3) {
			return (char) ((HEXVAL.indexOf(bytes[0]) << 24) + (HEXVAL.indexOf(bytes[1]) << 16)
					+ (HEXVAL.indexOf(bytes[2]) << 8) + HEXVAL.indexOf(bytes[3]));
		}
		if (bytes.length > 2) {
			return (char) ((HEXVAL.indexOf(bytes[0]) << 16) + (HEXVAL.indexOf(bytes[1]) << 8)
					+ HEXVAL.indexOf(bytes[2]));
		}
		if (bytes.length > 1) {
			return (char) ((HEXVAL.indexOf(bytes[0]) << 8) + HEXVAL.indexOf(bytes[1]));
		}
		return (char) HEXVAL.indexOf(bytes[0]);
	}

	public static String CONTROLCHARACTER = "abtnvfr";

	public static final String unQuoteControlCharacter(CharSequence value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(value.length());
		char c;
		int i = 0;
		int len = value.length();
		if (value.charAt(0) == '\"') {
			i++;
			len--;
		}
		for (; i < len; i++) {
			c = value.charAt(i);
			if (c == '\\') {
				if (i + 1 == len) {
					sb.append('\\');
					break;
				}
				c = value.charAt(++i);
				int pos = CONTROLCHARACTER.indexOf(c);
				if (pos >= 0) {
					sb.append(pos + 7);
				} else if (c == '\"') {
					sb.append('\"');
				} else if (c == 0x39) {
					sb.append(0x39);
				} else if (c == 'u') {
					sb.append(fromHex(value, i, i + 4));
					i += 4;
				} else if (c == 'o') {
					sb.append(fromHex(value, i, i + 3));
				} else {
					sb.append(c);
				}
				continue;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
