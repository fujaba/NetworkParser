package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.list.SimpleKeyValueList;

public class DataTypeMap extends DataType {
	private DataType genericKey;

	private DataType genericValue;

	DataTypeMap() {
		super(SimpleKeyValueList.class.getName());
		this.value.withExternal(true);
	}

	public static DataTypeMap create(Object key, Object value) {
		DataType keyData = DataType.create(key);
		DataType valueData = DataType.create(value);
		DataTypeMap result = new DataTypeMap().withGenericKey(keyData).withGenericValue(valueData);
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
		return getInternName(shortName, false);
	}

	@Override
	protected String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null || genericKey == null || genericValue == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + genericKey.getInternName(shortName, primitivAllow) + ","
				+ genericValue.getInternName(shortName, primitivAllow) + ">";
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) == false) {
			return false;
		}
		if (obj instanceof DataTypeMap == false) {
			return false;
		}
		if (obj.hashCode() == this.hashCode()) {
			return true;
		}
		DataTypeMap otherDTM = (DataTypeMap) obj;
		if (this.genericKey == null) {
			if (otherDTM.getGenericKey() != null) {
				return false;
			}
		} else {
			if (otherDTM.getGenericKey().equals(this.genericKey) == false) {
				return false;
			}
		}
		if (this.genericValue == null) {
			return otherDTM.getGenericValue() == null;
		}
		return otherDTM.getGenericValue().equals(this.genericValue);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public DataTypeMap withExternal(boolean external) {
		super.withExternal(external);
		return this;
	}

	public String getValue(String value) {
		if (PROPERTY_NAME.equals(value)) {
			return getGenericKey().getName(true);
		}
		if (PROPERTY_CATEGORIE.equals(value)) {
			return "MAP";
		}
		return null;
	}
}
