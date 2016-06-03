package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.uniks.networkparser.interfaces.SendableEntity;

// should become a JSON Parser
public class University implements SendableEntity {
	public static final String PROPERTY_USER = ".fg.user.";
	public static final String PROPERTY_ICH = ".child.value.";
	public static final String PROPERTY_VALUE = ".fg.value";

	private String user;
	private String ich;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getIch() {
		return ich;
	}

	public void setIch(String ich) {
		this.ich = ich;
	}

	/**
	 * <pre>
	 *		0..1	 students	 0..n
	 * University ------------------------- Student
	 *		university		&gt;	students
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
				getPropertyChangeSupport().firePropertyChange(PROPERTY_STUDENTS,
						null, value);
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
	 *		   1..1	 rooms	 0..n
	 * University ------------------------- Room
	 *		   university		&gt;	   rooms
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

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport()
				.addPropertyChangeListener(property, listener);
		return true;
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(property,
				listener);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
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

	public University withStudents(Student... values) {
		if(values == null) {
			return this;
		}
		for(Student student : values) {
			this.addToStudents(student);
		}
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

	public static final String PROPERTY_UNIID = "uniId";

	private int uniId;

	public int getUniId() {
		return this.uniId;
	}

	public void setUniId(int value) {
		if (this.uniId != value) {

			int oldValue = this.uniId;
			this.uniId = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_UNIID, oldValue, value);
		}
	}

	public University withUniId(int value) {
		setUniId(value);
		return this;
	}
}
