package org.sdmlib.test.examples.studyrightWithAssignments.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.AssignmentSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.StudentSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.University;


public class Student {
	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}
	@Override
	public String toString() {
		return this.getName();
	}
	public static final String PROPERTY_ASSIGNMENTPOINTS = "assignmentPoints";

	private int assignmentPoints;

	public int getAssignmentPoints() {
		return this.assignmentPoints;
	}

	public void setAssignmentPoints(int value) {
		if (this.assignmentPoints != value) {
			int oldValue = this.assignmentPoints;
			this.assignmentPoints = value;
			firePropertyChange(PROPERTY_ASSIGNMENTPOINTS, oldValue, value);
		}
	}

	public Student withAssignmentPoints(int value) {
		setAssignmentPoints(value);
		return this;
	}

	public static final String PROPERTY_CREDITS = "credits";

	private int credits;

	public int getCredits() {
		return this.credits;
	}

	public void setCredits(int value) {
		if (this.credits != value) {
			int oldValue = this.credits;
			this.credits = value;
			firePropertyChange(PROPERTY_CREDITS, oldValue, value);
		}
	}

	public Student withCredits(int value) {
		setCredits(value);
		return this;
	}

	public static final String PROPERTY_ID = "id";

	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String value) {
		if (this.id != value) {
			String oldValue = this.id;
			this.id = value;
			firePropertyChange(PROPERTY_ID, oldValue, value);
		}
	}

	public Student withId(String value) {
		setId(value);
		return this;
	}

	public static final String PROPERTY_MOTIVATION = "motivation";

	private int motivation;

	public int getMotivation() {
		return this.motivation;
	}

	public void setMotivation(int value) {
		if (this.motivation != value) {
			int oldValue = this.motivation;
			this.motivation = value;
			firePropertyChange(PROPERTY_MOTIVATION, oldValue, value);
		}
	}

	public Student withMotivation(int value) {
		setMotivation(value);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public Student withName(String value) {
		setName(value);
		return this;
	}


	public static final String PROPERTY_DONE = "done";

	private AssignmentSet done = null;

	public AssignmentSet getDone() {
		if(this.done == null) {
			return AssignmentSet.EMPTY_SET;
		}
		return this.done;
	}

	public Student withDone(Assignment... values) {
		if (values == null) {
			return this;
		}
		for (Assignment item : values) {
			if (item == null) {
				continue;
			}
			if (this.done == null) {
				this.done = new AssignmentSet();
			}
			this.done.withVisible(true);
			boolean changed = this.done.add(item);
			this.done.withVisible(false);
			if (changed) {
				item.withStudents(this);
				firePropertyChange(PROPERTY_DONE, null, item);
			}
		}
		return this;
	}

	public Student withoutDone(Assignment... value) {
		if(this.done == null) {
			return this;
		}
		for (Assignment item : value) {
			if (item != null) {
				if (this.done.remove(item)) {
					item.withoutStudents(this);
				}
			}
		}
		return this;
	}

	public Assignment createDone() {
		Assignment value = new Assignment();
		withDone(value);
		return value;
	}

	public static final String PROPERTY_IN = "in";

	private Room in = null;

	public Room getIn() {
		return this.in;
	}

	public boolean setIn(Room value) {
		if (this.in == value) {
			return false;
		}
		Room oldValue = this.in;
		if (this.in != null) {
			this.in = null;
			oldValue.withoutStudents(this);
		}
		this.in = value;
		if (value != null) {
			value.withStudents(this);
		}
		firePropertyChange(PROPERTY_IN, oldValue, value);
		return true;
	}

	public Student withIn(Room value) {
		this.setIn(value);
		return this;
	}

	public Room createIn() {
		Room value = new Room();
		withIn(value);
		return value;
	}

	public static final String PROPERTY_FRIENDS = "friends";

	private StudentSet friends = null;

	public StudentSet getFriends() {
		if(this.friends == null) {
			return StudentSet.EMPTY_SET;
		}
		return this.friends;
	}

	public Student withFriends(Student... values) {
		if (values == null) {
			return this;
		}
		for (Student item : values) {
			if (item == null) {
				continue;
			}
			if (this.friends == null) {
				this.friends = new StudentSet();
			}
			this.friends.withVisible(true);
			boolean changed = this.friends.add(item);
			this.friends.withVisible(false);
			if (changed) {
				item.withFriends(this);
				firePropertyChange(PROPERTY_FRIENDS, null, item);
			}
		}
		return this;
	}

	public Student withoutFriends(Student... value) {
		if(this.friends == null) {
			return this;
		}
		for (Student item : value) {
			if (item != null) {
				if (this.friends.remove(item)) {
					item.withoutFriends(this);
				}
			}
		}
		return this;
	}

	public Student createFriends() {
		Student value = new Student();
		withFriends(value);
		return value;
	}

	public static final String PROPERTY_UNIVERSITY = "university";

	private University university = null;

	public University getUniversity() {
		return this.university;
	}

	public boolean setUniversity(University value) {
		if (this.university == value) {
			return false;
		}
		University oldValue = this.university;
		if (this.university != null) {
			this.university = null;
			oldValue.withoutStudents(this);
		}
		this.university = value;
		if (value != null) {
			value.withStudents(this);
		}
		firePropertyChange(PROPERTY_UNIVERSITY, oldValue, value);
		return true;
	}

	public Student withUniversity(University value) {
		this.setUniversity(value);
		return this;
	}

	public University createUniversity() {
		University value = new University();
		withUniversity(value);
		return value;
	}
}