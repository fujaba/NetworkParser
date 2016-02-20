package de.uniks.networkparser.xml.util;

import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;
/**
 * @author Stefan Creator for XML Entity.
 */

public class XMLEntityCreator implements SendableEntityCreatorTag {
	/** NameSpace of XML. */
	private String nameSpace = "";
	
	/** The properties. */
	private final String[] properties = new String[] {XMLEntity.PROPERTY_TAG,
			XMLEntity.PROPERTY_VALUE };

	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * @param namespace
	 *			the NameSpace for xsd
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
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (XMLEntity.PROPERTY_TAG.equalsIgnoreCase(attribute)) {
			((XMLEntity) entity).withTag("" + value);
			return true;
		}
		if (XMLEntity.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			((XMLEntity) entity).setValueItem("" + value);
			return true;
		}
		if(XMLTokener.CHILDREN.equals(type) && value instanceof EntityList){
			((XMLEntity) entity).withChild((EntityList)value);
		}else if(attribute != null && attribute.isEmpty() == false) {
			((XMLEntity) entity).add(attribute, value);
		}
		return true;
	}

	@Override
	public String getTag() {
		if(nameSpace != null) {
			return nameSpace + ":element";
		}
		return null;
	}
}
