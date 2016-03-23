package de.uniks.networkparser.converter;
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

public abstract class ByteConverter implements Converter{
	public abstract String toString(ByteBuffer values);

	@Override
	public String encode(BaseItem entity) {
		if (entity instanceof ByteItem) {
			return toString(((ByteItem) entity).getBytes(true));
		}
		byte[] array;
		if (entity instanceof BaseItem) {
			array = ((BaseItem) entity).toString().getBytes();
		} else {
			array = entity.toString().getBytes();
		}
		return toString(new ByteBuffer().with(array));
	}

	public abstract byte[] decode(String value);
}
