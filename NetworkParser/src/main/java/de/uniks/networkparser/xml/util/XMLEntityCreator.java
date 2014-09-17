package de.uniks.networkparser.xml.util;

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
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * @author Stefan Creator for XML Entity.
 */
public class XMLEntityCreator implements SendableEntityCreator, XMLGrammar {
	/** The properties. */
	private final String[] properties = new String[] {XMLEntity.PROPERTY_TAG,
			XMLEntity.PROPERTY_VALUE };

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new XMLEntity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
			return ((XMLEntity) entity).getTag();
		}
		if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((XMLEntity) entity).getValueItem();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
			((XMLEntity) entity).withTag("" + value);
			return true;
		}
		if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			((XMLEntity) entity).withValueItem("" + value);
			return true;
		}
		return false;
	}

	@Override
	public boolean parseChild(XMLEntity entity, XMLEntity child, Tokener value) {
		return false;
	}

	@Override
	public void addChildren(XMLEntity parent, XMLEntity child) {
		parent.addChild(child);
	}

	@Override
	public void endChild(String tag) {
	}
}
