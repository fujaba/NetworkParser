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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class SoapObject implements BaseItem, SendableEntityCreatorTag {
	public static String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	public static String XMLNS_XSD = "http://www.w3.org/2001/XMLSchema";
	public static String XMLNS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
	private String nameSpace = "s";

	public static final String PROPERTY_HEADER = "Header";
	public static final String PROPERTY_BODY = "BODY";
	private SimpleKeyValueList<String, String> headers;
	protected XMLEntity children;

	public SoapObject withBody(XMLEntity body) {
		this.children = body;
		return this;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public SoapObject withNameSpace(String value) {
		this.nameSpace = value;
		return this;
	}

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	protected String parseItem(EntityStringConverter converter) {
		CharacterBuffer sb = new CharacterBuffer();
		sb.with("<", nameSpace, ":Envelope xmlns:xsi=\"", XMLNS_XSI,"\" xmlns:xsd=\"", XMLNS_XSD, "\"");
		sb.with(" xmlns:", nameSpace, "=\"", XMLNS_SOAP, "\">");
		converter.add();
		sb.with(converter.getPrefix());
		sb.with("<", nameSpace, ":Body>");

		if (children != null) {
			sb.with(children.toString(converter));
		}
		sb.with("</", nameSpace, ":Body>");
		converter.minus();
		sb.with(converter.getPrefix());
		sb.with("</", nameSpace, ":Envelope>");
		return sb.toString();
	}

	public SoapObject withHeader(String key, String value) {
		if(this.headers == null) {
			this.headers = new SimpleKeyValueList<String, String>();
		}
		this.headers.add(key, value);
		return this;
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
				withNameSpace((String) item);
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
		if(converter == null) {
			return null;
		}
		if(converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
		}
		return converter.encode(this);
	}
	
	@Override
	public String[] getProperties() {
		return new String[] {
				"." + nameSpace + ":" + SoapObject.PROPERTY_HEADER,
				"." + nameSpace + ":" + SoapObject.PROPERTY_BODY };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SoapObject();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute.endsWith(":" + SoapObject.PROPERTY_HEADER)) {
			return ((SoapObject) entity).getHeader();
		}
		if (attribute.endsWith(":" + SoapObject.PROPERTY_BODY)) {
			return ((SoapObject) entity).getBody();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(XMLTokener.CHILDREN.equals(type)) {
			((SoapObject) entity).with(value);
			return true;
		}
		if (attribute.toLowerCase().endsWith(
				":" + SoapObject.PROPERTY_BODY.toLowerCase())) {
			((SoapObject) entity).withBody(new XMLEntity().withValue("" + value));
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return nameSpace + ":Envelope";
	}
}
