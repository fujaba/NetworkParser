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

	public static DataTypeMap create(Clazz key, Clazz value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.create(key)).withGenericValue(DataType.create(value));
		return result;
	}
	public static DataTypeMap create(String key, String value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.create(key)).withGenericValue(DataType.create(value));
		return result;
	}

	public static DataTypeMap create(DataType key, DataType value) {
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
		return this.value.getName(shortName) + "<" + genericKey.getInternName(shortName, false) + "," + genericValue.getInternName(shortName, false) + ">";
	}
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj) == false) {
			return false;
		}
		if(obj instanceof DataTypeMap == false) {
			return false;
		}
		if(obj.hashCode() == this.hashCode()) {
			return true;
		}
		DataTypeMap otherDTM = (DataTypeMap) obj;
		if(this.genericKey == null) {
			if(otherDTM.getGenericKey() != null) {
				return false;
			}
		} else {
			if(otherDTM.getGenericKey().equals(this.genericKey) == false) {
				return false;
			}
		}
		if(this.genericValue == null) {
			return otherDTM.getGenericValue() == null;
		}
		return otherDTM.getGenericValue().equals(this.genericValue);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
