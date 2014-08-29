package de.uniks.networkparser.event.util;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import de.uniks.networkparser.event.SoapObject;
import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.xml.XMLEntity;

public class SoapCreator implements SendableEntityCreatorXML{
	public static String XMLNS_XSI="http://www.w3.org/2001/XMLSchema-instance";
	public static String XMLNS_XSD="http://www.w3.org/2001/XMLSchema";
	public static String XMLNS_SOAP="http://schemas.xmlsoap.org/soap/envelope/";
	private String nameSpace="s";
		
	@Override
	public String[] getProperties() {
		return new String[]{"&" +nameSpace+ ":" +SoapObject.PROPERTY_HEADER, "&" +nameSpace+ ":" +SoapObject.PROPERTY_BODY};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SoapObject();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute.endsWith(":" +SoapObject.PROPERTY_HEADER)) {
			return ((SoapObject)entity).getHeader();
		}
		if (attribute.endsWith(":" +SoapObject.PROPERTY_BODY)) {
			return ((SoapObject)entity).getBody();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute.toLowerCase().endsWith(":" +SoapObject.PROPERTY_BODY.toLowerCase())) {
			((SoapObject)entity).withBody( new XMLEntity().withValue("" + value));
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return nameSpace+ ":Envelope";
	}

	public SoapCreator withNamespace(String value) {
		this.nameSpace = value;
		return this;
	}
}
