package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.Entity;
import de.uniks.networkparser.test.model.ListItem;
import de.uniks.networkparser.xml.XMLTokener;

public class ListItemCreator implements SendableEntityCreatorTag {

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
		public boolean setValue(Object entity, String attribute, Object value, String type) {
			if(XMLTokener.CHILDREN.equals(type)) {
				return ((ListItem) entity).getChild().add((Entity) value);
			}
			return ((ListItem) entity).set(attribute, value);
		}

		@Override
		public String getTag() {
			return "listitem";
		}
	}