package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreatorByte;
import de.uniks.networkparser.test.model.FullAssocs;

public class FullAssocsCreator implements SendableEntityCreatorByte{

	@Override
	public String[] getProperties() {
		return new String[] { FullAssocs.PROPERTY_PERSONS,
				FullAssocs.PROPERTY_PASSWORDS, FullAssocs.PROPERTY_ANSWER, FullAssocs.PROPERTY_FULLMAP, FullAssocs.PROPERTY_MESSAGE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new FullAssocs();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((FullAssocs)entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((FullAssocs)entity).set(attribute, value);
	}

	@Override
	public byte getEventTyp() {
		return 0x42;
	}

}
