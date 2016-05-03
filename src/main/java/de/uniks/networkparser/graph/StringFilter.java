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
