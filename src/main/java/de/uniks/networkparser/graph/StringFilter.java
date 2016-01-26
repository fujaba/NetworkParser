package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.Condition;

public class StringFilter implements Condition<Object>{
	private String attribute;
	private String otherValue;
	private enum TYPE{EQUALS, EQUALSIGNORECASE, INDEXOF, NOT};
	private TYPE type;

	public StringFilter(String attribute) {
		this.attribute = attribute;
	}

	public boolean check(Object value) {
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
			} else if(this.type==TYPE.INDEXOF) {
				return ((String)itemValue).indexOf(otherValue) >= 0;
			} else if(this.type==TYPE.NOT) {
				return itemValue.equals(otherValue) == false;
			}
		}
		return false;
	}
	
	public StringFilter with(String value, TYPE type) {
		this.otherValue = value;
		this.type = type;
		return this;
	}
	
	public StringFilter equals(String otherValue) {
		return new StringFilter(this.attribute).with(otherValue, TYPE.EQUALS);
	}
	public StringFilter not(String otherValue) {
		return new StringFilter(this.attribute).with(otherValue, TYPE.NOT);
	}
	public StringFilter equalsIgnoreCase(String otherValue) {
		return new StringFilter(this.attribute).with(otherValue, TYPE.EQUALSIGNORECASE);
	}
	
	@Override
	public String toString() {
		return attribute;
	}
}
