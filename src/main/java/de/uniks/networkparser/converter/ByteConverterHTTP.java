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
import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteTokener;
import de.uniks.networkparser.interfaces.BaseItem;

public class ByteConverterHTTP extends ByteConverter {
	@Override
	public String toString(BufferedBuffer values) {
		CharacterBuffer returnValue = new CharacterBuffer();

		if (values != null) {
			for (int i = 0; i < values.length(); i++) {
				int value = values.byteAt(i);
				if (value <= 32 || value == 127) {
					returnValue.with(ByteTokener.SPLITTER);
					returnValue.with((char) (value + ByteTokener.SPLITTER + 1));
				} else {
					returnValue.with((char) value);
				}
			}
		}
		return returnValue.toString();
	}

	/**
	 * Decode http.
	 *
	 * @param values the bytes
	 * @return the object
	 */
	@Override
	public byte[] decode(CharSequence values) {
		if (values instanceof String) {
			return decode(((String) values).getBytes(Charset.forName(BaseItem.ENCODING)));
		}
		return null;
	}

	public byte[] decode(byte[] values) {
		if (values == null) {
			return null;
		}
		int len = values.length;
		ByteBuffer buffer = ByteBuffer.allocate(len);
		for (int i = 0; i < len; i++) {
			int value = values[i];
			if (value == ByteTokener.SPLITTER) {
				value = values[++i];
				buffer.put((byte) (value - ByteTokener.SPLITTER - 1));
			} else {
				buffer.put((byte) value);
			}
		}
		buffer.flip(false);
		return buffer.array(buffer.length(), true);
	}
}
