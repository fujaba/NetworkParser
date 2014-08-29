package de.uniks.networkparser.event;

import de.uniks.networkparser.ArrayEntityList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.event.util.SoapCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.xml.XMLEntity;

public class SoapObject implements StringItem, BaseItem{
	public static final String PROPERTY_HEADER="Header";
	public static final String PROPERTY_BODY="BODY";
	private String namespace="s";
	private ArrayEntityList<String, String> headers;
	private XMLEntity body;
	private boolean visible = true;
	
	public SoapObject withBody(XMLEntity body){
		this.body = body;
		return this;
	}
	
	public XMLEntity getBody(){
		return body;
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
	
	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}
	
	public String toString(int indentFactor, int intent) {
		String spaces = "";
		if(indentFactor>0){
			spaces = "\r\n"+EntityUtil.repeat(' ', indentFactor);
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<"+namespace+":Envelope xmlns:xsi=\""+SoapCreator.XMLNS_XSI+"\" xmlns:xsd=\""+SoapCreator.XMLNS_XSD+"\"");
		sb.append(" xmlns:"+namespace+"=\""+SoapCreator.XMLNS_SOAP+"\"");
		sb.append(">");
		if(indentFactor>0){
			sb.append(spaces);
		}
		sb.append("<"+namespace+":Body>");
		
		if(body != null){
			sb.append(body.toString(indentFactor,indentFactor+indentFactor));
			sb.append(spaces);
		}
		sb.append("</"+namespace+":Body>");
		if(indentFactor>0){
			sb.append("\r\n");
		}
		sb.append("</" +namespace+":Envelope>");
		
		return sb.toString();
	}

	@Override
	public SoapObject withVisible(boolean value) {
		this.visible  = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public ArrayEntityList<String, String> getHeader() {
		return headers;
	}
}
