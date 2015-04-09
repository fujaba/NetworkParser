package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.test.model.ListItem;

public class ListItemCreator implements SendableEntityCreatorXML {

		@Override
		public String[] getProperties() {
			return new String[] { ListItem.PROPERTY_ENTITY};
		}

		@Override
		public Object getSendableInstance(boolean reference) {
			return new ListItem();
		}

		@Override
		public Object getValue(Object entity, String attribute) {
			return ((ListItem) entity).get(attribute);
		}

		@Override
		public boolean setValue(Object entity, String attribute, Object value, String typ) {
			return ((ListItem) entity).set(attribute, value);
		}

		@Override
		public String getTag() {
			return "listitem";
		}
	}