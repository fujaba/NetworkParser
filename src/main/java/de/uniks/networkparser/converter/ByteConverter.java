package de.uniks.networkparser.converter;

import java.nio.charset.Charset;

import de.uniks.networkparser.buffer.BufferedBuffer;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
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
