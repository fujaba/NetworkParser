package org.sdmlib.test.examples.studyrightWithAssignments.model.util;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Student;
import de.uniks.networkparser.list.SimpleSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Assignment;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.list.StringList;

public class StudentSet extends SimpleSet<Student> implements SendableEntityCreator {

	private final String[] properties = new String[] {
		Student.PROPERTY_DONE,
		Student.PROPERTY_IN,
		Student.PROPERTY_FRIENDS,
		Student.PROPERTY_UNIVERSITY,
		Student.PROPERTY_ASSIGNMENTPOINTS,
		Student.PROPERTY_CREDITS,
		Student.PROPERTY_ID,
		Student.PROPERTY_MOTIVATION,
		Student.PROPERTY_NAME,
	};

	public static final StudentSet EMPTY_SET = new StudentSet().withFlag(StudentSet.READONLY);

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Student();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Student == false) {
			return null;
		}
		Student element = (Student)entity;
		if (Student.PROPERTY_DONE.equalsIgnoreCase(attribute)) {
			return element.getDone();
		}

		if (Student.PROPERTY_IN.equalsIgnoreCase(attribute)) {
			return element.getIn();
		}

		if (Student.PROPERTY_FRIENDS.equalsIgnoreCase(attribute)) {
			return element.getFriends();
		}

		if (Student.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			return element.getUniversity();
		}

		if (Student.PROPERTY_ASSIGNMENTPOINTS.equalsIgnoreCase(attribute)) {
			return element.getAssignmentPoints();
		}

		if (Student.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			return element.getCredits();
		}

		if (Student.PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return element.getId();
		}

		if (Student.PROPERTY_MOTIVATION.equalsIgnoreCase(attribute)) {
			return element.getMotivation();
		}

		if (Student.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.getName();
		}

		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof Student == false) {
			return false;
		}
		Student element = (Student)entity;
		if (SendableEntityCreator.REMOVE.equals(type) && value != null) {
			attribute = attribute + type;
		}

		if (Student.PROPERTY_DONE.equalsIgnoreCase(attribute)) {
			element.withDone((Assignment) value);
			return true;
		}

		if (Student.PROPERTY_IN.equalsIgnoreCase(attribute)) {
			element.setIn((Room) value);
			return true;
		}

		if (Student.PROPERTY_FRIENDS.equalsIgnoreCase(attribute)) {
			element.withFriends((Student) value);
			return true;
		}

		if (Student.PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			element.setUniversity((University) value);
			return true;
		}

		if (Student.PROPERTY_ASSIGNMENTPOINTS.equalsIgnoreCase(attribute)) {
			return element.setAssignmentPoints((int) value);
		}

		if (Student.PROPERTY_CREDITS.equalsIgnoreCase(attribute)) {
			return element.setCredits((int) value);
		}

		if (Student.PROPERTY_ID.equalsIgnoreCase(attribute)) {
			return element.setId((String) value);
		}

		if (Student.PROPERTY_MOTIVATION.equalsIgnoreCase(attribute)) {
			return element.setMotivation((int) value);
		}

		if (Student.PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return element.setName((String) value);
		}

		return false;
	}

	public static IdMap createIdMap(String session) {
		return CreatorCreator.createIdMap(session);
	}
	public Class<?> getTypClass() {
		return Student.class;
	}

	@Override
	public StudentSet getNewList(boolean keyValue) {
		return new StudentSet();
	}


	public NumberList getAssignmentPoints(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getAssignmentPoints(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getAssignmentPoints());
			}
		} else {
			for (Student obj : this) {
				int item = obj.getAssignmentPoints();
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
	public StudentSet filterAssignmentPoints(int minValue, int maxValue) {
		StudentSet result = new StudentSet();
		for(Student obj : this) {
			if (minValue <= obj.getAssignmentPoints() && maxValue >= obj.getAssignmentPoints()) {
				result.add(obj);
			}
		}
		return result;
	}

	public StudentSet withAssignmentPoints(int value) {
		for (Student obj : this) {
			obj.setAssignmentPoints(value);
		}
		return this;
	}
	public NumberList getCredits(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getCredits(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getCredits());
			}
		} else {
			for (Student obj : this) {
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
	public StudentSet filterCredits(int minValue, int maxValue) {
		StudentSet result = new StudentSet();
		for(Student obj : this) {
			if (minValue <= obj.getCredits() && maxValue >= obj.getCredits()) {
				result.add(obj);
			}
		}
		return result;
	}

	public StudentSet withCredits(int value) {
		for (Student obj : this) {
			obj.setCredits(value);
		}
		return this;
	}
	public StringList getId(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getId(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getId());
			}
		} else {
			for (Student obj : this) {
				String item = obj.getId();
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
	public StudentSet filterId(String minValue, String maxValue) {
		StudentSet result = new StudentSet();
		for(Student obj : this) {
			if (minValue.compareTo(obj.getId()) <= 0 && maxValue.compareTo(obj.getId()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public StudentSet withId(String value) {
		for (Student obj : this) {
			obj.setId(value);
		}
		return this;
	}
	public NumberList getMotivation(int... filter) {
		NumberList result = new NumberList();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getMotivation(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getMotivation());
			}
		} else {
			for (Student obj : this) {
				int item = obj.getMotivation();
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
	public StudentSet filterMotivation(int minValue, int maxValue) {
		StudentSet result = new StudentSet();
		for(Student obj : this) {
			if (minValue <= obj.getMotivation() && maxValue >= obj.getMotivation()) {
				result.add(obj);
			}
		}
		return result;
	}

	public StudentSet withMotivation(int value) {
		for (Student obj : this) {
			obj.setMotivation(value);
		}
		return this;
	}
	public StringList getName(String... filter) {
		StringList result = new StringList();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getName(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getName());
			}
		} else {
			for (Student obj : this) {
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
	public StudentSet filterName(String minValue, String maxValue) {
		StudentSet result = new StudentSet();
		for(Student obj : this) {
			if (minValue.compareTo(obj.getName()) <= 0 && maxValue.compareTo(obj.getName()) >= 0) {
				result.add(obj);
			}
		}
		return result;
	}

	public StudentSet withName(String value) {
		for (Student obj : this) {
			obj.setName(value);
		}
		return this;
	}
	public AssignmentSet getDone(Assignment... filter) {
		AssignmentSet result = new AssignmentSet();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getDone(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.addAll(obj.getDone());
			}
			return result;
		}
		for (Student obj : this) {
			AssignmentSet item = obj.getDone();
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


	public StudentSet withDone(Assignment value) {
		for (Student obj : this) {
			obj.withDone(value);
		}
		return this;
	}
	public RoomSet getIn(Room... filter) {
		RoomSet result = new RoomSet();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getIn(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getIn());
			}
			return result;
		}
		for (Student obj : this) {
			Room item = obj.getIn();
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


	public StudentSet withIn(Room value) {
		for (Student obj : this) {
			obj.withIn(value);
		}
		return this;
	}
	public StudentSet getFriends(Student... filter) {
		StudentSet result = new StudentSet();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getFriends(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.addAll(obj.getFriends());
			}
			return result;
		}
		for (Student obj : this) {
			StudentSet item = obj.getFriends();
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


	public StudentSet withFriends(Student value) {
		for (Student obj : this) {
			obj.withFriends(value);
		}
		return this;
	}
	public UniversitySet getUniversity(University... filter) {
		UniversitySet result = new UniversitySet();
		if(listener != null) {
			result.withListener(listener);
			Student[] children = this.toArray(new Student[size()]);
			for(int i=0;i<children.length;i++) {
				listener.update(SimpleEvent.create(this, i, result, children[i], children[i].getUniversity(), filter));
			}
			return result;
		}
		if(filter == null || filter.length<1) {
			for (Student obj : this) {
				result.add(obj.getUniversity());
			}
			return result;
		}
		for (Student obj : this) {
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


	public StudentSet withUniversity(University value) {
		for (Student obj : this) {
			obj.withUniversity(value);
		}
		return this;
	}
}