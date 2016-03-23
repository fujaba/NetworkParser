package de.uniks.networkparser.event;

import de.uniks.networkparser.buffer.CharacterBuffer;
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
		return parseItem(new EntityStringConverter());
	}

	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}
	

	protected String parseItem(EntityStringConverter converter) {
		CharacterBuffer sb = new CharacterBuffer();
		sb.with("<", namespace, ":Envelope xmlns:xsi=\"", SoapCreator.XMLNS_XSI,"\" xmlns:xsd=\"", SoapCreator.XMLNS_XSD, "\"");
		sb.with(" xmlns:", namespace, "=\"", SoapCreator.XMLNS_SOAP, "\">");
		converter.add();
		sb.with(converter.getPrefix());
		sb.with("<", namespace, ":Body>");

		if (children != null) {
			sb.with(children.toString(converter));
		}
		sb.with("</", namespace, ":Body>");
		converter.minus();
		sb.with(converter.getPrefix());
		sb.with("</", namespace, ":Envelope>");
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
		if(converter == null) {
			return null;
		}
		if(converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
		}
		return converter.encode(this);
	}
}
