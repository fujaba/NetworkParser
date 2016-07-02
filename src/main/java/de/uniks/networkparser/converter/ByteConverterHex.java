package de.uniks.networkparser.converter;

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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class ByteConverterHex extends ByteConverter {
	/**
	 * To hex string.
	 *
	 * @param values
	 *			the bytes
	 * @return the string
	 */
	@Override
	public String toString(BufferedBuffer values) {
		return toString(values, 0);
	}
	public String toString(BufferedBuffer values, int space) {
		if(values == null) {
			return null;
		}
		String hexVal = "0123456789ABCDEF";

		CharacterBuffer returnValue = new CharacterBuffer().withBufferLength(values.length() << 1 + values.length() * space);
		String step = EntityUtil.repeat(' ', space);
		for (int i = 0; i < values.length(); i++) {
			int value = values.byteAt(i);
			if (value < 0) {
				value += 256;
			}
			returnValue.with(hexVal.charAt(value / 16));
			returnValue.with(hexVal.charAt(value % 16));
			returnValue.with(step);
		}
		return returnValue.toString();
	}

	/**
	 * To byte string.
	 *
	 * @param value
	 *			the hex string
	 * @return the byte[]
	 */
	@Override
	public byte[] decode(String value) {
		String hexVal = "0123456789ABCDEF";
		byte[] out = new byte[value.length() / 2];

		int n = value.length();

		for (int i = 0; i < n; i += 2) {
			// make a bit representation in an int of the hex value
			int hn = hexVal.indexOf(value.charAt(i));
			int ln = hexVal.indexOf(value.charAt(i + 1));

			// now just shift the high order nibble and add them together
			out[i / 2] = (byte) ((hn << 4) | ln);
		}
		return out;
	}

}
