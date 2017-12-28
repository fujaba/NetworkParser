package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Prof;
import de.uniks.networkparser.test.model.Student;

public class ProfCreator implements SendableEntityCreator {

	public static IdMap createIdMap(String sessionID) {
		IdMap jsonIdMap = new IdMap();
		jsonIdMap.withSession(sessionID);

		jsonIdMap.with(new UniversityCreator());
		jsonIdMap.with(new RoomCreator());
		jsonIdMap.with(new ProfCreator());

		return jsonIdMap;
	}

	@Override
	public String[] getProperties() {
		return new String[] { Prof.PROPERTY_NAME,
				Prof.PROPERTY_UNIVERSITY,
				Prof.PROPERTY_FIRSTNAME, Prof.PROPERTY_LASTNAME, 
				Prof.PROPERTY_ITEM
		};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Prof();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Prof) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Prof) entity).set(attribute, value);
	}
}
