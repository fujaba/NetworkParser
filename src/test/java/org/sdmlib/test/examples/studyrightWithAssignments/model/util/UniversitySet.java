package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.President;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.SimpleEvent;

public class UniversitySet extends SimpleSet<University> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		University.PROPERTY_PRESIDENT,
		University.PROPERTY_ROOMS,
		University.PROPERTY_STUDENTS,
		University.PROPERTY_NAME,
	};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new University();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof University == false) {
			return null;
		}
		University element = (University)entity;
		if (University.PROPERTY_PRESIDENT.equalsIgnoreCase(attribute)) {
			return element.getPresident();
		}

		if (University.PROPERTY_ROOMS.equalsIgnoreCase(attribute)) {
			return element.getRooms();
		}

		if (University.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return element.getStudents();
		}

		if (University.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
		return element.getName();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof University == false) {
			return false;
		}
		University element = (University)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (University.PROPERTY_PRESIDENT.equalsIgnoreCase(attribute)) {
			element.setPresident((President) value);
			return true;
		}

		if (University.PROPERTY_ROOMS.equalsIgnoreCase(attribute)) {
			element.withRooms((Room) value);
			return true;
		}

		if (University.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			element.withStudents((Student) value);
			return true;
		}

		if (University.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			element.setName((String) value);
			return true;
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}	public static final UniversitySet EMPTY_SET = new UniversitySet().withFlag(UniversitySet.READONLY);

	public Class<?> getTypClass() {
		return University.class;
	}

	@Override
	public UniversitySet getNewList(boolean keyValue) {
		return new UniversitySet();
	}


	public StringList getName(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getName(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (University obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (University obj : this) {
				String item = obj.getName();
				for(int i=0;i<filter.length;i++) {
					if (filter[i].equals(item)) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public UniversitySet filterName(String minValue, String maxValue) {
		UniversitySet result = new UniversitySet();
		for(University obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public UniversitySet withName(String value) {
		for (University obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public PresidentSet getPresident(President... filter) {
		PresidentSet result = new PresidentSet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getPresident(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (University obj : this) {
				result.add(obj.getPresident());
			}
			return result;
		}
		for (University obj : this) {
			President item = obj.getPresident();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.equals(filter[i])) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}


	public UniversitySet withPresident(President value) {
		for (University obj : this) {
			obj.withPresident(value);
		}
		return this;
	}
	public RoomSet getRooms(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getRooms(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (University obj : this) {
				result.addAll(obj.getRooms());
			}
			return result;
		}
		for (University obj : this) {
			RoomSet item = obj.getRooms();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.contains(filter[i])) {
						result.add(filter[i]);
						break;
					}
				}
			}
		}
		return result;
	}


	public UniversitySet withRooms(Room value) {
		for (University obj : this) {
			obj.withRooms(value);
		}
		return this;
	}
	public StudentSet getStudents(Student... filter) {
		StudentSet result = new StudentSet();
		if(listener != null) {
			result.withListener(listener);
			for(int i=0;i<size();i++) {
				listener.update(SimpleEvent.create(this, i, result, get(i), get(i).getStudents(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (University obj : this) {
				result.addAll(obj.getStudents());
			}
			return result;
		}
		for (University obj : this) {
			StudentSet item = obj.getStudents();
			if(item != null) {
				for(int i=0;i<filter.length;i++) {
					if (item.contains(filter[i])) {
						result.add(filter[i]);
						break;
					}
				}
			}
		}
		return result;
	}


	public UniversitySet withStudents(Student value) {
		for (University obj : this) {
			obj.withStudents(value);
		}
		return this;
	}
}