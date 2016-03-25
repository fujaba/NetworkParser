package de.uniks.networkparser.xml;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;

public class XMLContainer extends XMLEntity{
	private SimpleList<String> prefix = new SimpleList<String>();
	
	public XMLContainer withPrefix(String value) {
		this.prefix.add(value);
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(String item : prefix) {
			sb.append(item);
			sb.append(BaseItem.CRLF);
		}
		sb.append(super.toString());
		return sb.toString();
	}
	
	@Override
	public String toString(int indentFactor) {
		StringBuilder sb=new StringBuilder();
		for(String item : prefix) {
			sb.append(item);
			sb.append(BaseItem.CRLF);
		}
		sb.append(super.toString(indentFactor));
		return sb.toString();
	}
}
