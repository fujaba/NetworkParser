package de.uniks.networkparser.converter;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.BufferedBuffer;

public class ByteConverterSimple extends ByteConverter {
	@Override
	public String toString(BufferedBuffer values) {
		if (values == null || values.length() < 1) {
			return "EMTPY";
		}
		return EntityUtil.getStringTyp(values.byteAt(0)) + " Laenge: " + values.length();
	}

	@Override
	public byte[] decode(String value) {
		return null;
	}
}
