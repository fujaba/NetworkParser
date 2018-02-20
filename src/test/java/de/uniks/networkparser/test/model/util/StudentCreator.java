package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;

public class StudentCreator implements SendableEntityCreator {

	public static IdMap createIdMap(String sessionID) {
		IdMap jsonIdMap = new IdMap();
		jsonIdMap.withSession(sessionID);

		jsonIdMap.with(new UniversityCreator());
		jsonIdMap.with(new RoomCreator());
		jsonIdMap.with(new StudentCreator());

		return jsonIdMap;
	}

	@Override
	public String[] getProperties() {
		return new String[] { Student.PROPERTY_NAME, Student.PROPERTY_STUD_NO,
				Student.PROPERTY_IN, Student.PROPERTY_UNIVERSITY,
				 Student.PROPERTY_FIRSTNAME, Student.PROPERTY_LASTNAME,
				 Student.PROPERTY_CREDITS, Student.PROPERTY_FRIENDS, Student.PROPERTY_ITEM
		};
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Student();
	}

	@Override
	public Object getValue(Object entity, String attrName) {
		if(entity instanceof Student == false) {
			return null;
		}
		Student student = (Student) entity;
		int pos = attrName.indexOf(".");
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}
		if (Student.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return student.getName();
		}
		if (Student.PROPERTY_STUD_NO.equalsIgnoreCase(attribute)) {
			return student.getStudNo();
		}
		if (Student.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return new UniversityCreator().getValue(student.getUniversity(), attrName.substring(pos + 1));
			}
			return student.getUniversity();
		}
		if (Student.PROPERTY_IN.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return student.getIn().get(attrName.substring(pos + 1));
			}
			return student.getIn();
		}
		if (Student.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)) {
			return student.getFirstName();
		}
		if (Student.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)) {
			return student.getLastName();
		}
		if (Student.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			return student.getCredits();
		}
		if (Student.PROPERTY_ITEM.equalsIgnoreCase(attribute)) {
			return student.getItem();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String typ) {
		if(entity instanceof Student == false) {
			return false;
		}
		Student student = (Student) entity;
		if (Student.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			student.setName((String) value);
			return true;
		}
		if (Student.PROPERTY_STUD_NO.equalsIgnoreCase(attribute)) {
			student.setStudNo((String) value);
			return true;
		}
		if (Student.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			student.setUniversity((University) value);
			return true;
		}
		if (Student.PROPERTY_IN.equalsIgnoreCase(attribute)) {
			student.setIn((Room) value);
			return true;
		}
		if (Student.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)) {
			student.setFirstName((String) value);
			return true;
		}
		if (Student.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)) {
			student.setLastName((String) value);
			return true;
		}
		if (Student.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			student.setCredits((int) value);
			return true;
		}
		if (Student.PROPERTY_ITEM.equalsIgnoreCase(attribute)) {
			student.withItem((Item) value);
			return true;
		}
		return false;
	}
}