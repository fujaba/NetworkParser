package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.XMLTestItem;

public class XMLTestItemCreator implements SendableEntityCreatorXML{

	@Override
	public String[] getProperties() {
		return new String[]{XMLTestItem.PROPERTY_ID, XMLTestItem.PROPERTY_USER, XMLTestItem.PROPERTY_BODY, XMLTestItem.PROPERTY_VALUE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new XMLTestItem();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (XMLTestItem.PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return ((XMLTestItem) entity).getId();
		}
		if (XMLTestItem.PROPERTY_USER.equalsIgnoreCase(attribute)) {
			return ((XMLTestItem) entity).getUser();
		}
		if (XMLTestItem.PROPERTY_BODY.equalsIgnoreCase(attribute)) {
			return ((XMLTestItem) entity).getBody();
		}
		if (XMLTestItem.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((XMLTestItem) entity).getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (XMLTestItem.PROPERTY_ID.equalsIgnoreCase(attribute)) {
			((XMLTestItem)entity).setId(Integer.valueOf(""+ value));
			return true;
		}
		if (XMLTestItem.PROPERTY_USER.equalsIgnoreCase(attribute)) {
			((XMLTestItem)entity).setUser((String) value);
			return true;
		}
		if (XMLTestItem.PROPERTY_BODY.equalsIgnoreCase(attribute)) {
			((XMLTestItem)entity).setBody((String) value);
			return true;
		}
		if (XMLTestItem.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			((XMLTestItem)entity).setValue((String) value);
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return "item";
	}
}
