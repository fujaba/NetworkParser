package de.uniks.networkparser.test.model.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.Student;

public class StudentCreator implements SendableEntityCreator {

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
		return new String[] { Student.PROPERTY_NAME, Student.PROPERTY_STUD_NO,
				Student.PROPERTY_IN, Student.PROPERTY_UNIVERSITY };
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Student();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((Student) entity).get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		return ((Student) entity).set(attribute, value);
	}
}
