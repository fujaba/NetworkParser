package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.list.SimpleSet;

public class DataTypeSet extends DataType{
	private DataType generic;

	DataTypeSet() {
		super(SimpleSet.class.getName());
		this.value.withExternal(true);
	}

	private DataTypeSet withGeneric(DataType value) {
		this.generic = value;
		return this;
	}

	public DataType getGeneric() {
		return generic;
	}

	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getInternName(shortName, false) + ">";
	}

	@Override
	protected String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getInternName(shortName, primitivAllow) + ">";
	}
	
	public static DataTypeSet create(Clazz value) {
		DataTypeSet result = new DataTypeSet().withGeneric(DataType.create(value));
		return result;
	}
	public static DataTypeSet create(String value) {
		DataTypeSet result = new DataTypeSet().withGeneric(DataType.create(value));
		return result;
	}

	public static DataTypeSet create(DataType value) {
		DataTypeSet result = new DataTypeSet().withGeneric(value);
		return result;
	}
	
	public static DataTypeSet create(Class<?> value) {
		DataTypeSet result = new DataTypeSet().withGeneric(DataType.create(value));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj) == false) {
			return false;
		}
		if(obj instanceof DataTypeSet == false) {
			return false;
		}
		if(obj.hashCode() == this.hashCode()) {
			return true;
		}
		if(this.generic == null) {
			return ((DataTypeSet)obj).getGeneric() == null;
		}
		return ((DataTypeSet)obj).getGeneric().equals(this.generic);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
