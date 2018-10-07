package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Assignment;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.NumberList;

public class AssignmentSet extends SimpleSet<Assignment> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Assignment.PROPERTY_ROOM,
		Assignment.PROPERTY_STUDENTS,
		Assignment.PROPERTY_CONTENT,
		Assignment.PROPERTY_POINTS,
	};

	public static final AssignmentSet EMPTY_SET = new AssignmentSet().withFlag(AssignmentSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Assignment();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Assignment == false) {
			return null;
		}
		Assignment element = (Assignment)entity;
		if (Assignment.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			return element.getRoom();
		}

		if (Assignment.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return element.getStudents();
		}

		if (Assignment.PROPERTY_CONTENT.equalsIgnoreCase(attribute)) {
			return element.getContent();
		}

		if (Assignment.PROPERTY_POINTS.equalsIgnoreCase(attribute)) {
			return element.getPoints();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Assignment == false) {
			return false;
		}
		Assignment element = (Assignment)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Assignment.PROPERTY_ROOM.equalsIgnoreCase(attribute)) {
			element.setRoom((Room) value);
			return true;
		}

		if (Assignment.PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			element.withStudents((Student) value);
			return true;
		}

		if (Assignment.PROPERTY_CONTENT.equalsIgnoreCase(attribute)) {
			return element.setContent((String) value);
		}

		if (Assignment.PROPERTY_POINTS.equalsIgnoreCase(attribute)) {
			return element.setPoints((int) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Assignment.class;
	}

	@Override
	public AssignmentSet getNewList(boolean keyValue) {
		return new AssignmentSet();
	}


	public StringList getContent(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Assignment[] children = this.toArray(new Assignment[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getContent(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Assignment obj : this) {
				result.add(obj.getContent());
			}
		} else {
			for (Assignment obj : this) {
				String item = obj.getContent();
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
	public AssignmentSet filterContent(String minValue, String maxValue) {
		AssignmentSet result = new AssignmentSet();
		for(Assignment obj : this) {
			if (minValue.compareTo(obj.getContent()) <= 0 && maxValue.compareTo(obj.getContent()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public AssignmentSet withContent(String value) {
		for (Assignment obj : this) {
			obj.setContent(value);
		}
		return this;
	}
	public NumberList getPoints(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Assignment[] children = this.toArray(new Assignment[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getPoints(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Assignment obj : this) {
				result.add(obj.getPoints());
			}
		} else {
			for (Assignment obj : this) {
				int item = obj.getPoints();
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
	public AssignmentSet filterPoints(int minValue, int maxValue) {
		AssignmentSet result = new AssignmentSet();
		for(Assignment obj : this) {
			if (	minValue <= obj.getPoints() && maxValue >= obj.getPoints()) {
				result.add(obj);
			}
		}
		return result;
	}

	public AssignmentSet withPoints(int value) {
		for (Assignment obj : this) {
			obj.setPoints(value);
		}
		return this;
	}
	public RoomSet getRoom(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			Assignment[] children = this.toArray(new Assignment[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getRoom(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Assignment obj : this) {
				result.add(obj.getRoom());
			}
			return result;
		}
		for (Assignment obj : this) {
			Room item = obj.getRoom();
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


	public AssignmentSet withRoom(Room value) {
		for (Assignment obj : this) {
			obj.withRoom(value);
		}
		return this;
	}
	public StudentSet getStudents(Student... filter) {
		StudentSet result = new StudentSet();
		if(listener != null) {
			result.withListener(listener);
			Assignment[] children = this.toArray(new Assignment[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getStudents(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Assignment obj : this) {
				result.addAll(obj.getStudents());
			}
			return result;
		}
		for (Assignment obj : this) {
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


	public AssignmentSet withStudents(Student value) {
		for (Assignment obj : this) {
			obj.withStudents(value);
		}
		return this;
	}
}