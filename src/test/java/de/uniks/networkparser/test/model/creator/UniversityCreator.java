package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.University;

public class UniversityCreator implements SendableEntityCreator {
	public static JsonIdMap createIdMap(String sessionID) {
		JsonIdMap jsonIdMap = new JsonIdMap();
		jsonIdMap.withSessionId(sessionID);

		jsonIdMap.withCreator(new UniversityCreator());
		jsonIdMap.withCreator(new RoomCreator());
		jsonIdMap.withCreator(new StudentCreator());

		return jsonIdMap;
	}

	@Override
	public String[] getProperties() {
		return new String[] { University.PROPERTY_NAME,
				University.PROPERTY_STUDENTS, University.PROPERTY_ROOMS };
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new University();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((University) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((University) entity).set(attribute, value);
	}
}
