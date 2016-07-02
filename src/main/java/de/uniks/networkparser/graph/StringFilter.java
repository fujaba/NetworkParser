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
import de.uniks.networkparser.interfaces.Condition;

public class StringFilter<T> implements Condition<T>{
	private String attribute;
	private String otherValue;
	private enum TYPE{EQUALS, EQUALSIGNORECASE, CONTAINS, NOT};
	private TYPE type;

	public StringFilter(String attribute) {
		this.attribute = attribute;
	}

	public boolean update(Object value) {
		Object itemValue =null;
		if(value instanceof GraphMember) {
			itemValue = ((GraphMember)value).getValue(attribute);
		}
		if(otherValue ==null) {
			return itemValue == null;
		}
		if(itemValue !=null && itemValue instanceof String) {
			if(this.type==TYPE.EQUALS) {
				return itemValue.equals(otherValue);
			} else if(this.type==TYPE.EQUALSIGNORECASE) {
				return ((String)itemValue).equalsIgnoreCase(otherValue);
			} else if(this.type==TYPE.CONTAINS) {
				return ((String)itemValue).contains(otherValue);
			} else if(this.type==TYPE.NOT) {
				return itemValue.equals(otherValue) == false;
			}
		}
		return false;
	}

	public StringFilter<T> with(String value, TYPE type) {
		this.otherValue = value;
		this.type = type;
		return this;
	}

	public StringFilter<T> equals(String otherValue) {
		return new StringFilter<T>(this.attribute).with(otherValue, TYPE.EQUALS);
	}
	public StringFilter<T> not(String otherValue) {
		return new StringFilter<T>(this.attribute).with(otherValue, TYPE.NOT);
	}
	public StringFilter<T> equalsIgnoreCase(String otherValue) {
		return new StringFilter<T>(this.attribute).with(otherValue, TYPE.EQUALSIGNORECASE);
	}
	public StringFilter<T> contains(String otherValue) {
		return new StringFilter<T>(this.attribute).with(otherValue, TYPE.CONTAINS);
	}

	@Override
	public String toString() {
		return attribute;
	}
}
