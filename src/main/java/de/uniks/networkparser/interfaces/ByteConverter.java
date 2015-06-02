package de.uniks.networkparser.interfaces;

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

public abstract class ByteConverter {
	public String encode(BaseItem entity) {
		if (entity instanceof ByteItem) {
			return toString(((ByteItem) entity).getBytes(true));
		}
		if (entity instanceof StringItem) {
			return toString(((StringItem) entity).toString(2).getBytes());
		}
		return toString(entity.toString().getBytes());
	}

	public String toString(ByteItem item, boolean dynamic) {
		return toString(item.getBytes(dynamic));
	}

	public String toString(BufferedBytes bufferedBytes) {
		return toString(bufferedBytes.array(), bufferedBytes.length());
	}

	public abstract String toString(byte[] values, int size);

	public String toString(byte[] values) {
		return toString(values, values.length);
	}

	public abstract byte[] decode(String value);
}
