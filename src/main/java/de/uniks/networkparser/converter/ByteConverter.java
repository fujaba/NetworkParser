package de.uniks.networkparser.converter;

import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.BufferedBuffer;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;

public abstract class ByteConverter implements Converter {
	public abstract String toString(BufferedBuffer values);

	public String toString(byte... values) {
		ByteBuffer buffer = new ByteBuffer().with(values);
		return this.toString(buffer);
	}

	@Override
	public String encode(BaseItem entity) {
		ByteBuffer buffer;
		if (entity instanceof ByteItem) {
			buffer = ((ByteItem) entity).getBytes(true);
		} else {
			byte[] array = ((BaseItem) entity).toString().getBytes(Charset.forName(BaseItem.ENCODING));
			buffer = new ByteBuffer().with(array);
		}
		if (buffer != null) {
			return toString(buffer);
		}
		return "";
	}

	public abstract byte[] decode(CharSequence value);
}
