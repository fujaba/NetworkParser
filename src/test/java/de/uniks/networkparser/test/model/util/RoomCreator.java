package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.Room;

public class RoomCreator implements SendableEntityCreator {

	public static JsonIdMap createIdMap(String sessionID) {
		JsonIdMap jsonIdMap = new JsonIdMap();
		jsonIdMap.withSessionId(sessionID);

		jsonIdMap.with(new UniversityCreator());
		jsonIdMap.with(new RoomCreator());
		jsonIdMap.with(new StudentCreator());

		return jsonIdMap;
	}

	@Override
	public String[] getProperties() {
		return new String[] { Room.PROPERTY_NAME, Room.PROPERTY_SUBLOCATIONS,
				Room.PROPERTY_STUDENTS, Room.PROPERTY_PARENT,
				Room.PROPERTY_UNIVERSITY };
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Room();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Room) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Room) entity).set(attribute, value);
	}
}
