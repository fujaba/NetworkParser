package de.uniks.jism.bytes.converter;

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

import de.uniks.jism.bytes.ByteIdMap;
import de.uniks.jism.bytes.BytesBuffer;

public class ByteConverterHTTP extends ByteConverter {

	@Override
	public String toString(byte[] values, int size) {
		StringBuilder returnValue = new StringBuilder();

		if (values != null) {
			for (int i = 0; i < size; i++) {
				byte value = values[i];
				if (value <= 32 || value == 127) {
					returnValue.append(ByteIdMap.SPLITTER);
					returnValue.append((char) (value + ByteIdMap.SPLITTER + 1));
				} else {
					returnValue.append((char) value);
				}
			}
		}
		return returnValue.toString();
	}

	/**
	 * Decode http.
	 * 
	 * @param bytes
	 *            the bytes
	 * @return the object
	 */
	@Override
	public byte[] decode(String value) {
		int len = value.length();
		BytesBuffer buffer=BytesBuffer.allocate(len);
		for (int i = 0; i < len; i++) {
			int c = value.charAt(i);
			if (c == ByteIdMap.SPLITTER) {
				c = value.charAt(++i);
				buffer.put((byte) (c - ByteIdMap.SPLITTER - 1));
			} else {
				buffer.put((byte) c);
			}
		}
		buffer.flip();
		return buffer.getValue(buffer.length());
	}
}
