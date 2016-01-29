package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;

public class UniversityCreator implements SendableEntityCreator, SendableEntityCreatorTag {
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
		return new String[] { University.PROPERTY_NAME,
				University.PROPERTY_STUDENTS, University.PROPERTY_ROOMS, University.PROPERTY_ICH, University.PROPERTY_USER, University.PROPERTY_VALUE};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new University();
	}

	@Override
	public Object getValue(Object entity, String attrName) {
		int pos = attrName.indexOf(".");
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}
		if (University.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return ((University) entity).getName();
		}
		if (University.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return ((University) entity).getStudents();
		}
		if (University.PROPERTY_ROOMS.equalsIgnoreCase(attribute)) {
			return ((University) entity).getRooms();
		}
		if (University.PROPERTY_ICH.equalsIgnoreCase(attribute)) {
			return ((University) entity).getIch();
		}
		if (University.PROPERTY_USER.equalsIgnoreCase(attribute)) {
			return ((University) entity).getUser();
		}
		if (University.PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return ((University) entity).getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attrName, Object value, String typ) {
		if (University.PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			((University)entity).setName((String) value);
			return true;
		}
		if (University.PROPERTY_STUDENTS.equalsIgnoreCase(attrName)) {
			((University)entity).addToStudents((Student) value);
			return true;
		}
		if (University.PROPERTY_ROOMS.equalsIgnoreCase(attrName)) {
			((University)entity).addToRooms((Room) value);
			return true;
		}
		if (University.PROPERTY_ICH.equalsIgnoreCase(attrName)) {
			((University)entity).setIch("" + value);
			return true;
		}
		if (University.PROPERTY_USER .equalsIgnoreCase(attrName)) {
			((University)entity).setUser("" + value);
			return true;
		}
		if (University.PROPERTY_VALUE.equalsIgnoreCase(attrName)) {
			((University) entity).setValue("" + value);
			return true;
		}
		return false;
	}

	@Override
	public String getTag() {
		return "uni";
	}
}
