package de.uniks.networkparser.event;

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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.event.util.SoapCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class SoapObject implements BaseItem {
	public static final String PROPERTY_HEADER = "Header";
	public static final String PROPERTY_BODY = "BODY";
	private String namespace = "s";
	private SimpleKeyValueList<String, String> headers;
	protected XMLEntity children;

	public SoapObject withBody(XMLEntity body) {
		this.children = body;
		return this;
	}

	public String getNamespace() {
		return namespace;
	}

	public SoapObject withNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	@Override
	public String toString() {
		return toString(0, 0);
	}

	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	protected String toString(int indentFactor, int indent) {
		String spaces = "";
		if (indentFactor > 0) {
			spaces = "\r\n" + EntityUtil.repeat(' ', indentFactor);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<" + namespace + ":Envelope xmlns:xsi=\""
				+ SoapCreator.XMLNS_XSI + "\" xmlns:xsd=\""
				+ SoapCreator.XMLNS_XSD + "\"");
		sb.append(" xmlns:" + namespace + "=\"" + SoapCreator.XMLNS_SOAP + "\"");
		sb.append(">");
		if (indentFactor > 0) {
			sb.append(spaces);
		}
		sb.append("<" + namespace + ":Body>");

		if (children != null) {
			sb.append(children.toString(new EntityStringConverter(indentFactor, indent + indentFactor)));
			sb.append(spaces);
		}
		sb.append("</" + namespace + ":Body>");
		if (indentFactor > 0) {
			sb.append("\r\n");
		}
		sb.append("</" + namespace + ":Envelope>");

		return sb.toString();
	}

	public SimpleKeyValueList<String, String> getHeader() {
		return headers;
	}

	@Override
	public BaseItem with(Object... values) {
		if(values==null) {
			return this;
		}
		for(Object item : values) {
			if(item instanceof String) {
				withNamespace((String) item);
			} else if(item instanceof XMLEntity) {
				withBody((XMLEntity) item);
			}
		}
		return null;
	}

	@Override
	public SoapObject getNewList(boolean keyValue) {
		return new SoapObject();
	}

	public Object getValue(Object key) {
		if(PROPERTY_HEADER.equals(key)){
			return headers;
		}
		if(PROPERTY_BODY.equals(key)){
			return children;
		}
		return null;
	}
	
	public XMLEntity getBody() {
		return children;
	}

	@Override
	public String toString(Converter converter) {
		if(converter instanceof EntityStringConverter) {
			EntityStringConverter item = (EntityStringConverter)converter;
			return toString(item.getIndentFactor(), item.getIndent());
		}
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	@Override
	public int size() {
		return children.size();
	}
}
