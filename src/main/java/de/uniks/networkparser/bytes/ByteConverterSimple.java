package de.uniks.networkparser.bytes;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.BufferedBuffer;

/**
 * Converter for Simple ByteFormat
 * @author Stefan Lindel
 */
public class ByteConverterSimple extends ByteConverter {
	@Override
	public String toString(BufferedBuffer values) {
		if (values == null || values.length() < 1) {
			return "EMTPY";
		}
		return EntityUtil.getStringType(values.byteAt(0)) + " Laenge: " + values.length();
	}

	@Override
	public byte[] decode(CharSequence value) {
		return null;
	}
}
