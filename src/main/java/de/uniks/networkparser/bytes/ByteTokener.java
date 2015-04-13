package de.uniks.networkparser.bytes;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class ByteTokener extends Tokener {
	@Override
	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
		// buffer
		// if (typ == ByteIdMap.DATATYPE_CLAZZ) {
		// String clazz = (String) getDecodeObject(ByteIdMap.DATATYPE_CLAZZ,
		// in);

	}

	@Override
	public void parseToEntity(AbstractList<?> entityList) {
		// TODO Auto-generated method stub

	}

}