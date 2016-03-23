package de.uniks.networkparser.graph;

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
