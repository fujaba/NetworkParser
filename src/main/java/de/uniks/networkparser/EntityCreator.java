package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class EntityCreator implements SendableEntityCreator, SendableEntityCreatorNoIndex, SendableEntityCreatorTag {
	private final static String VALUE = "VALUE";
	private BaseItem factory;
	/** The properties. */
	private String[] properties = new String[] { VALUE };
	private boolean keyValue;
	/** NameSpace of XML. */
	private String nameSpace = "";
	
	public static final EntityCreator createJson(boolean keyValue) {
		EntityCreator entity = new EntityCreator();
		entity.factory = new JsonArray();
		entity.keyValue = keyValue;
		return entity;
	}

	public static final EntityCreator createXML() {
		EntityCreator entity = new EntityCreator();
		entity.factory = new XMLEntity();
		entity.properties = new String[] { XMLEntity.PROPERTY_TAG, XMLEntity.PROPERTY_VALUE };
		return entity;
	}
	
	@Override
	public String[] getProperties() {
		return this.properties;
	}

	/**
	 * Set a Namespace for XSD Element
	 *
	 * @param namespace the NameSpace for XSD-Element
	 * @return Itself
	 */
	public EntityCreator withNameSpace(String namespace) {
		this.nameSpace = namespace;
		return this;
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		if(factory != null) {
			return factory.getNewList(keyValue);
		}
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(entity == null) {
			return null;
		}
		if(entity instanceof XMLEntity) {
			XMLEntity item = (XMLEntity) entity;
			if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
				return item.getTag();
			}
			if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
				return item.getValue();
			}
		}
		if(entity instanceof Entity) {
			return ((Entity) entity).getValue(attribute);
		}
		return entity.toString();
	}
	
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (SendableEntityCreator.REMOVE_YOU.equalsIgnoreCase(type) || entity == null) {
			return false;
		}
		if(entity instanceof XMLEntity) {
			XMLEntity item = (XMLEntity) entity;
			if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
				item.withType("" + value);
				return true;
			}
			if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
				String newValue = "" + value;
				if (newValue.length() > 0) {
					item.withValueItem(newValue);
				}
				return true;
			}
			if (XMLTokener.CHILDREN.equals(type) && value instanceof EntityList) {
				item.withChild((EntityList) value);
				return true;
			}
			if (attribute != null && attribute.isEmpty() == false) {
				item.add(attribute, value);
			}
			return true;
		}
		if(entity instanceof JsonArray) {
			JsonArray item = (JsonArray) entity;
			item.withValue((String) value);
			return true;
		}
		if(entity instanceof JsonObject) {
			JsonObject json = (JsonObject) entity;
	        if (VALUE.equals(attribute)) {
	        	json.withValue((String) value);
	        } else {
	            json.withKeyValue(attribute, value);
	        }
		}
		return false;
	}
	
	@Override
	public String getTag() {
		if (nameSpace != null) {
			return nameSpace + ":element";
		}
		return null;
	}
}
