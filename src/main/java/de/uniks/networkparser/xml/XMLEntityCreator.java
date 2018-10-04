package de.uniks.networkparser.xml;

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
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

/** @author Stefan Creator for XML Entity. */

public class XMLEntityCreator implements SendableEntityCreatorTag {
	/** NameSpace of XML. */
	private String nameSpace = "";

	/** The properties. */
	private final String[] properties = new String[] { XMLEntity.PROPERTY_TAG, XMLEntity.PROPERTY_VALUE };

	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Set a Namespace for XSD Element
	 *
	 * @param namespace the NameSpace for XSD-Element
	 * @return Itself
	 */
	public XMLEntityCreator withNameSpace(String namespace) {
		this.nameSpace = namespace;
		return this;
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
			return ((XMLEntity) entity).getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
			((XMLEntity) entity).withType("" + value);
			return true;
		}
		if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			String newValue = "" + value;
			if (newValue.length() > 0) {
				((XMLEntity) entity).withValueItem(newValue);
			}
			return true;
		}
		if (XMLTokener.CHILDREN.equals(type) && value instanceof EntityList) {
			((XMLEntity) entity).withChild((EntityList) value);
		} else if (attribute != null && attribute.isEmpty() == false) {
			((XMLEntity) entity).add(attribute, value);
		}
		return true;
	}

	@Override
	public String getTag() {
		if (nameSpace != null) {
			return nameSpace + ":element";
		}
		return null;
	}
}
