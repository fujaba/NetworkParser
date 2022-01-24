package de.uniks.networkparser.bytes;

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
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.ByteBuffer;

/**
 * Converter for Byte to ByteStream
 * @author Stefan Lindel
 */
public class ByteConverterBinary extends ByteConverter {
	public static String toString(byte value) {
		ByteConverterBinary converter = new ByteConverterBinary();
		return converter.toString(new ByteBuffer().with(value));
	}

	public static String toString(int value) {
		return toString((byte) value);
	}

	/**
	 * To Binary string.
	 *
	 * @param values the bytes
	 * @return the string
	 */
	@Override
	public String toString(BufferedBuffer values) {
		StringBuilder sb = new StringBuilder();
		if (values == null) {
			return sb.toString();
		}
		for (int z = 0; z < values.length(); z++) {
			int number = values.byteAt(z);
			char[] bits = new char[] { '0', '0', '0', '0', '0', '0', '0', '0' };
			int i = 7;
			if (number < 0) {
				number += 256;
			}
			while (number != 0) {
				bits[i] = (char) (48 + (number % 2));
				number = (byte) (number / 2);
				i--;
			}
			sb.append(new String(bits));
		}
		return sb.toString();
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
		int n = value.length();
		byte[] out = new byte[n / 8];

		if (n < 8 || n % 8 > 0) {
			return null;
		}

		for (int i = 0; i < n;) {
			int charText = 0;
			for (int z = 0; z < 8; z++) {
				charText = charText << ((byte) (value.charAt(i++) - 48));
			}
			/* now just shift the high order nibble and add them together */
			out[i / 8] = (byte) charText;
		}
		return out;
	}

}
