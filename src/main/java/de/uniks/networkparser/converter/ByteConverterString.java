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
import de.uniks.networkparser.buffer.BufferedBuffer;

public class ByteConverterString extends ByteConverter {
	/**
	 * To simple string.
	 *
	 * @param values the bytes
	 * @return the string
	 */
	@Override
	public String toString(BufferedBuffer values) {
		if (values == null) {
			return null;
		}
		StringBuilder returnValue = new StringBuilder(values.length());
		for (int i = 0; i < values.length(); i++) {
			returnValue.append(values.charAt(i));
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
		byte[] out = new byte[value.length()];
		int n = value.length();

		for (int i = 0; i < n; i++) {
			out[i] = (byte) value.charAt(i);
		}
		return out;
	}

}
