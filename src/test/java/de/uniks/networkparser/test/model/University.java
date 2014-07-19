package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

// should become a JSON Parser
// should become a JSON Parser
public class University {
	/**
	 * <pre>
	 *           0..1     students     0..n
	 * University ------------------------- Student
	 *           university        &gt;       students
	 * </pre>
	 */

	public static final String PROPERTY_STUDENTS = "students";
	private LinkedHashSet<Student> students;

	public boolean addToStudents(Student value) {
		boolean changed = false;

		if (value != null) {
			if (this.students == null) {
				this.students = new LinkedHashSet<Student>();

			}
			changed = this.students.add(value);
			if (changed) {
				value.setUniversity(this);
			}
		}
		return changed;
	}

	public boolean removeFromStudents(Student value) {
		boolean changed = false;

		if ((this.students != null) && (value != null)) {
			changed = this.students.remove(value);
			if (changed) {
				value.setUniversity(null);
			}
		}
		return changed;
	}

	public void removeAllFromStudents() {
		Student tmpValue;
		Iterator<Student> iter = this.iteratorOfStudents();

		while (iter.hasNext()) {
			tmpValue = (Student) iter.next();
			this.removeFromStudents(tmpValue);
		}
	}

	public boolean hasInStudents(Student value) {
		return ((this.students != null) && (value != null) && this.students
				.contains(value));
	}

	public Iterator<Student> iteratorOfStudents() {
		return ((this.students == null) ? null : this.students.iterator());

	}

	public int sizeOfStudents() {
		return ((this.students == null) ? 0 : this.students.size());
	}

	/**
	 * <pre>
	 *           1..1     rooms     0..n
	 * University ------------------------- Room
	 *           university        &gt;       rooms
	 * </pre>
	 */

	public static final String PROPERTY_ROOMS = "rooms";
	private LinkedHashSet<Room> rooms;

	public boolean addToRooms(Room value) {
		boolean changed = false;

		if (value != null) {
			if (this.rooms == null) {
				this.rooms = new LinkedHashSet<Room>();

			}
			changed = this.rooms.add(value);
			if (changed) {
				value.setUniversity(this);
			}
		}
		return changed;
	}

	public boolean removeFromRooms(Room value) {
		boolean changed = false;

		if ((this.rooms != null) && (value != null)) {
			changed = this.rooms.remove(value);
			if (changed) {
				value.setUniversity(null);
			}
		}
		return changed;
	}

	public void removeAllFromRooms() {
		Room tmpValue;
		Iterator<Room> iter = this.iteratorOfRooms();

		while (iter.hasNext()) {
			tmpValue = (Room) iter.next();
			this.removeFromRooms(tmpValue);
		}
	}

	public boolean hasInRooms(Room value) {
		return ((this.rooms != null) && (value != null) && this.rooms
				.contains(value));
	}

	public Iterator<Room> iteratorOfRooms() {
		return ((this.rooms == null) ? null : this.rooms.iterator());

	}

	public int sizeOfRooms() {
		return ((this.rooms == null) ? 0 : this.rooms.size());
	}

	public void removeYou() {
		this.removeAllFromStudents();
		for (Iterator<Room> iterRooms = this.iteratorOfRooms(); iterRooms
				.hasNext();) {
			((Room) iterRooms.next()).removeYou();
		}
	}

	protected final PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport()
				.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(property,
				listener);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}

	public boolean set(String attrName, Object value) {
		if (PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			setName((String) value);
			return true;
		} else if (PROPERTY_STUDENTS.equalsIgnoreCase(attrName)) {
			addToStudents((Student) value);
			return true;
		} else if (PROPERTY_ROOMS.equalsIgnoreCase(attrName)) {
			addToRooms((Room) value);
			return true;
		}
		return false;
	}

	public Object get(String attrName) {
		int pos = attrName.indexOf(".");
		String attribute = attrName;

		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return getName();
		} else if (PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return getStudents();
		} else if (PROPERTY_ROOMS.equalsIgnoreCase(attribute)) {
			return getRooms();
		}
		return null;
	}

	public University withName(String newValue) {
		this.setName(newValue);
		return this;
	}

	public Set<Student> getStudents() {
		if (this.students == null) {
			this.students = new LinkedHashSet<Student>();
		}

		return Collections.unmodifiableSet(this.students);
	}

	public University withStudents(Student newValue) {
		this.addToStudents(newValue);
		return this;
	}

	public University withoutStudents(Student newValue) {
		this.removeFromStudents(newValue);
		return this;
	}

	public Set<Room> getRooms() {
		if (this.rooms == null) {
			this.rooms = new LinkedHashSet<Room>();
		}

		return Collections.unmodifiableSet(this.rooms);
	}

	public University withRooms(Room newValue) {
		this.addToRooms(newValue);
		return this;
	}

	public University withoutRooms(Room newValue) {
		this.removeFromRooms(newValue);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public void setName(String value) {
		if (value != null && !value.equalsIgnoreCase(this.name)) {
			String oldValue = this.name;
			this.name = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME,
					oldValue, value);
		}
	}

	public String getName() {
		return this.name;
	}

}
