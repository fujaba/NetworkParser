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
