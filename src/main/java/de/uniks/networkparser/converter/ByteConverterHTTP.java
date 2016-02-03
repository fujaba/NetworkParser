package de.uniks.networkparser.converter;

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
import de.uniks.networkparser.bytes.ByteIdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class ByteConverterHTTP extends ByteConverter {
	@Override
	public String toString(ByteBuffer values) {
		CharacterBuffer returnValue = new CharacterBuffer();

		if (values != null) {
			for (int i = 0; i < values.length(); i++) {
				int value = values.byteAt(i);
                if (value <= 32 || value == 127) {
					returnValue.with(ByteIdMap.SPLITTER);
					returnValue.with((char) (value + ByteIdMap.SPLITTER + 1));
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
	 * @param values
	 *			the bytes
	 * @return the object
	 */
	@Override
	public byte[] decode(String values) {
		return decode(values.getBytes());
	}
	public byte[] decode(byte[] values) {
		if(values == null) {
			return null;
		}
		int len = values.length;
		ByteBuffer buffer = ByteBuffer.allocate(len);
		for (int i = 0; i < len; i++) {
			int value = values[i];
			if (value == ByteIdMap.SPLITTER) {
				value = values[++i];
				buffer.put((byte) (value - ByteIdMap.SPLITTER - 1));
			} else {
				buffer.put((byte) value);
			}
		}
		buffer.flip();
		return buffer.getValue(buffer.length());
	}
}
