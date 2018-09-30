package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Assignment;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import org.sdmlib.test.examples.studyrightWithAssignments.model.TeachingAssistant;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.StringList;

public class RoomSet extends SimpleSet<Room> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Room.PROPERTY_ASSIGNMENTS,
		Room.PROPERTY_DOORS,
		Room.PROPERTY_STUDENTS,
		Room.PROPERTY_TAS,
		Room.PROPERTY_UNIVERSITY,
		Room.PROPERTY_CREDITS,
		Room.PROPERTY_NAME,
		Room.PROPERTY_TOPIC,
	};

	public static final RoomSet EMPTY_SET = new RoomSet().withFlag(RoomSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Room();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Room == false) {
			return null;
		}
		Room element = (Room)entity;
		if (Room.PROPERTY_ASSIGNMENTS.equalsIgnoreCase(attribute)) {
			return element.getAssignments();
		}

		if (Room.PROPERTY_DOORS.equalsIgnoreCase(attribute)) {
			return element.getDoors();
		}

		if (Room.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return element.getStudents();
		}

		if (Room.PROPERTY_TAS.equalsIgnoreCase(attribute)) {
			return element.getTas();
		}

		if (Room.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			return element.getUniversity();
		}

		if (Room.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			return element.getCredits();
		}

		if (Room.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.getName();
		}

		if (Room.PROPERTY_TOPIC.equalsIgnoreCase(attribute)) {
			return element.getTopic();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Room == false) {
			return false;
		}
		Room element = (Room)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Room.PROPERTY_ASSIGNMENTS.equalsIgnoreCase(attribute)) {
			element.withAssignments((Assignment) value);
			return true;
		}

		if (Room.PROPERTY_DOORS.equalsIgnoreCase(attribute)) {
			element.withDoors((Room) value);
			return true;
		}

		if (Room.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			element.withStudents((Student) value);
			return true;
		}

		if (Room.PROPERTY_TAS.equalsIgnoreCase(attribute)) {
			element.withTas((TeachingAssistant) value);
			return true;
		}

		if (Room.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			element.setUniversity((University) value);
			return true;
		}

		if (Room.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			return element.setCredits((int) value);
		}

		if (Room.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.setName((String) value);
		}

		if (Room.PROPERTY_TOPIC.equalsIgnoreCase(attribute)) {
			return element.setTopic((String) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Room.class;
	}

	@Override
	public RoomSet getNewList(boolean keyValue) {
		return new RoomSet();
	}


	public NumberList getCredits(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getCredits(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.add(obj.getCredits());
			}
		} else {
			for (Room obj : this) {
				int item = obj.getCredits();
				for(int i=0;i<filter.length;i++) {
					if (filter[i] == item) {
						result.add(item);
						break;
					}
				}
			}
		}
		return result;
	}
	public RoomSet filterCredits(int minValue, int maxValue) {
		RoomSet result = new RoomSet();
		for(Room obj : this) {
			if (minValue <= obj.getCredits() && maxValue >= obj.getCredits()) {
				result.add(obj);
			}
		}
		return result;
	}

	public RoomSet withCredits(int value) {
		for (Room obj : this) {
			obj.setCredits(value);
		}
		return this;
	}
	public StringList getName(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getName(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (Room obj : this) {
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
	public RoomSet filterName(String minValue, String maxValue) {
		RoomSet result = new RoomSet();
		for(Room obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public RoomSet withName(String value) {
		for (Room obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public StringList getTopic(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getTopic(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.add(obj.getTopic());
			}
		} else {
			for (Room obj : this) {
				String item = obj.getTopic();
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
	public RoomSet filterTopic(String minValue, String maxValue) {
		RoomSet result = new RoomSet();
		for(Room obj : this) {
			if (minValue.compareTo(obj.getTopic()) <= 0 && maxValue.compareTo(obj.getTopic()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public RoomSet withTopic(String value) {
		for (Room obj : this) {
			obj.setTopic(value);
		}
		return this;
	}
	public AssignmentSet getAssignments(Assignment... filter) {
		AssignmentSet result = new AssignmentSet();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getAssignments(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.addAll(obj.getAssignments());
			}
			return result;
		}
		for (Room obj : this) {
			AssignmentSet item = obj.getAssignments();
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


	public RoomSet withAssignments(Assignment value) {
		for (Room obj : this) {
			obj.withAssignments(value);
		}
		return this;
	}
	public RoomSet getDoors(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getDoors(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.addAll(obj.getDoors());
			}
			return result;
		}
		for (Room obj : this) {
			RoomSet item = obj.getDoors();
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


	public RoomSet withDoors(Room value) {
		for (Room obj : this) {
			obj.withDoors(value);
		}
		return this;
	}
	public StudentSet getStudents(Student... filter) {
		StudentSet result = new StudentSet();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getStudents(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.addAll(obj.getStudents());
			}
			return result;
		}
		for (Room obj : this) {
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


	public RoomSet withStudents(Student value) {
		for (Room obj : this) {
			obj.withStudents(value);
		}
		return this;
	}
	public TeachingAssistantSet getTas(TeachingAssistant... filter) {
		TeachingAssistantSet result = new TeachingAssistantSet();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getTas(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.addAll(obj.getTas());
			}
			return result;
		}
		for (Room obj : this) {
			TeachingAssistantSet item = obj.getTas();
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


	public RoomSet withTas(TeachingAssistant value) {
		for (Room obj : this) {
			obj.withTas(value);
		}
		return this;
	}
	public UniversitySet getUniversity(University... filter) {
		UniversitySet result = new UniversitySet();
		if(listener != null) {
			result.withListener(listener);
			Room[] children = this.toArray(new Room[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getUniversity(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Room obj : this) {
				result.add(obj.getUniversity());
			}
			return result;
		}
		for (Room obj : this) {
			University item = obj.getUniversity();
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


	public RoomSet withUniversity(University value) {
		for (Room obj : this) {
			obj.withUniversity(value);
		}
		return this;
	}
	public RoomSet findPath(int motivation) {
		return RoomSet.EMPTY_SET;
	}

}