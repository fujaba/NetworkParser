package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.test.model.SortedMsg;

public class SortedMsgCreator implements SendableEntityCreatorTag{

	@Override
	public String[] getProperties() {
		return new String[]{SortedMsg.PROPERTY_ID, SortedMsg.PROPERTY_CHILD, SortedMsg.PROPERTY_PARENT, SortedMsg.PROPERTY_MSG};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new SortedMsg();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((SortedMsg)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((SortedMsg)entity).set(attribute, value);
	}

	@Override
	public String getTag() {
		return new String(new byte[]{0x42});
	}

}
