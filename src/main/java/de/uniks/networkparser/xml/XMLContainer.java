package de.uniks.networkparser.xml;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;

public class XMLContainer extends XMLEntity{
	private SimpleList<String> prefix = new SimpleList<String>();

	public XMLContainer withPrefix(String value) {
		this.prefix.add(value);
		return this;
	}

	public XMLContainer withStandardPrefix() {
		withPrefix("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
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
