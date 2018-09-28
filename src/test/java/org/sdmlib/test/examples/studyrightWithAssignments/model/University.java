package org.sdmlib.test.examples.studyrightWithAssignments.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.sdmlib.test.examples.studyrightWithAssignments.model.President;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.RoomSet;
import org.sdmlib.test.examples.studyrightWithAssignments.model.util.StudentSet;


public class University {
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

	public University withName(String value) {
		setName(value);
		return this;
	}


	public static final String PROPERTY_PRESIDENT = "president";

	private President president = null;

	public President getPresident() {
		return this.president;
	}

	public boolean setPresident(President value) {
		if (this.president == value) {
			return false;
		}
		President oldValue = this.president;
		this.president = value;
		firePropertyChange(PROPERTY_PRESIDENT, oldValue, value);
		return true;
	}

	public University withPresident(President value) {
		this.setPresident(value);
		return this;
	}

	public President createPresident() {
		President value = new President();
		withPresident(value);
		return value;
	}

	public static final String PROPERTY_ROOMS = "rooms";

	private RoomSet rooms = null;

	public RoomSet getRooms() {
		if(this.rooms == null) {
			return RoomSet.EMPTY_SET;
		}
		return this.rooms;
	}

	public University withRooms(Room... values) {
		if (values == null) {
			return this;
		}
		for (Room item : values) {
			if (item == null) {
				continue;
			}
			if (this.rooms == null) {
				this.rooms = new RoomSet();
			}
			this.rooms.withVisible(true);
			boolean changed = this.rooms.add(item);
			this.rooms.withVisible(false);
			if (changed) {
				firePropertyChange(PROPERTY_ROOMS, null, item);
			}
		}
		return this;
	}

	public University withoutRooms(Room... value) {
		if(this.rooms == null) {
			return this;
		}
		for (Room item : value) {
			if (item != null) {
				this.rooms.remove(item);
			}
		}
		return this;
	}

	public Room createRooms() {
		Room value = new Room();
		withRooms(value);
		return value;
	}

	public static final String PROPERTY_STUDENTS = "students";

	private StudentSet students = null;

	public StudentSet getStudents() {
		if(this.students == null) {
			return StudentSet.EMPTY_SET;
		}
		return this.students;
	}

	public University withStudents(Student... values) {
		if (values == null) {
			return this;
		}
		for (Student item : values) {
			if (item == null) {
				continue;
			}
			if (this.students == null) {
				this.students = new StudentSet();
			}
			this.students.withVisible(true);
			boolean changed = this.students.add(item);
			this.students.withVisible(false);
			if (changed) {
				item.setUniversity(this);
				firePropertyChange(PROPERTY_STUDENTS, null, item);
			}
		}
		return this;
	}

	public University withoutStudents(Student... value) {
		if(this.students == null) {
			return this;
		}
		for (Student item : value) {
			if (item != null) {
				if (this.students.remove(item)) {
					item.withUniversity(null);
				}
			}
		}
		return this;
	}

	public Student createStudents() {
		Student value = new Student();
		withStudents(value);
		return value;
	}
}