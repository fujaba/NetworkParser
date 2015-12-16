package de.uniks.networkparser.graph;

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
import de.uniks.networkparser.list.SimpleKeyValueList;

public class DataTypeMap extends DataType {
	private DataType genericKey;
	
	private DataType genericValue;
	
	DataTypeMap() {
		super(SimpleKeyValueList.class.getName());
		this.value.withExternal(true);
	}
	
	public static DataTypeMap ref(Clazz key, Clazz value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	public static DataTypeMap ref(String key, String value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	
	public static DataTypeMap ref(DataType key, DataType value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(key).withGenericValue(value);
		return result;
	}

	private DataTypeMap withGenericKey(DataType key) {
		this.genericKey = key;
		return this;
	}

	private DataTypeMap withGenericValue(DataType value) {
		this.genericValue = value;
		return this;
	}
	
	public DataType getGenericKey() {
		return genericKey;
	}
	
	public DataType getGenericValue() {
		return genericValue;
	}
	
	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + genericKey.getName(shortName) + "," + genericValue.getName(shortName) + ">";
	}

}
