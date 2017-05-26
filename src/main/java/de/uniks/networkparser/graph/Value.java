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

public abstract class Value extends GraphMember {
	public static final String PROPERTY_INITIALIZATION = "initialization";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_TYPEVALUE = "typeValue";

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
	
	@Override
	public Object getValue(String attribute) {
		int pos = attribute.indexOf('.');
		String attrName;
		if(pos>0) {
			attrName = attribute.substring(0, pos);
		}else {
			attrName = attribute;
		}
		if(PROPERTY_TYPE.equalsIgnoreCase(attrName)) {
			return this.getType();
		}
		if (PROPERTY_TYPEVALUE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				return this.getType().getClazz().getValue(attribute.substring(pos + 1));
			}
			return this.getType().getClazz();
		}
		return super.getValue(attribute);
	}

	public Value withValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return this.value;
	}
}
