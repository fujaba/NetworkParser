package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.XMLTestEntity;

public class MyXMLEntityCreator implements SendableEntityCreator, SendableEntityCreatorTag {
	@Override
	public String[] getProperties() {
		return new String[] { XMLTestEntity.PROPERTY_SENDER,
				XMLTestEntity.PROPERTY_TEXT};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new XMLTestEntity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((XMLTestEntity) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((XMLTestEntity) entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return "chatmsg";
	}
}