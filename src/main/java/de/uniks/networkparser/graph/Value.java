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

public abstract class Value extends GraphMember {
	public static final String PROPERTY_INITIALIZATION = "initialization";
	public static final String PROPERTY_TYPE = "type";

	protected DataType type = null;
	protected String value = null;

	public Value with(DataType value) {
		if ((this.type == null && value != null)
				|| (this.type != null && this.type != value)) {
			this.type = value;
		}
		return this;
	}

	public Value with(Clazz value) {
		this.type = new DataType(value);
		return this;
	}

	public Value with(Class<?> value) {
		this.type = DataType.create(value);
		return this;
	}

	public String getType(boolean shortName) {
		if(type==null) {
			return "?";
		}
		return type.getName(shortName);
	}

	public DataType getType() {
		return type;
	}

	public Value withValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return this.value;
	}
}
