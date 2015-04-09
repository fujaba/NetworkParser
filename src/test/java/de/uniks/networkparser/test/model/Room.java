package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.uniks.networkparser.test.model.util.UniversityCreator;

public class Room {
	/**
	 * <pre>
	 *           0..n     rooms     1..1
	 * Room ------------------------- University
	 *           rooms        &lt;       university
	 * </pre>
	 */

	public static final String PROPERTY_UNIVERSITY = "university";
	private University university;

	public boolean setUniversity(University value) {
		boolean changed = false;

		if (this.university != value) {
			University oldValue = this.university;
			if (this.university != null) {
				this.university = null;
				oldValue.removeFromRooms(this);
			}
			this.university = value;

			if (value != null) {
				value.addToRooms(this);
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_UNIVERSITY,
					oldValue, value);
			changed = true;
		}
		return changed;
	}

	public University getUniversity() {
		return this.university;
	}

	/**
	 * <pre>
	 *           0..1     in     0..n
	 * Room ------------------------- Student
	 *           in        &lt;       students
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
				value.setIn(this);
			}
		}
		return changed;
	}

	public boolean removeFromStudents(Student value) {
		boolean changed = false;

		if ((this.students != null) && (value != null)) {
			changed = this.students.remove(value);
			if (changed) {
				value.setIn(null);
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

	public void removeYou() {
		this.setUniversity(null);
		this.removeAllFromStudents();
		this.removeAllFromSublocations();
		/**
		 * <pre>
		 *           0..1     in     0..n
		 * Room ------------------------- Student
		 *           in        &lt;       students
		 * </pre>
		 */
		this.setParent(null);
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
		} else if (PROPERTY_PARENT.equalsIgnoreCase(attrName)) {
			setParent((Room) value);
			return true;
		} else if (PROPERTY_SUBLOCATIONS.equalsIgnoreCase(attrName)) {
			addToSublocations((Room) value);
			return true;
		} else if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attrName)) {
			setUniversity((University) value);
			return true;
		} else if (PROPERTY_STUDENTS.equalsIgnoreCase(attrName)) {
			addToStudents((Student) value);
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
		} else if (PROPERTY_PARENT.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return getParent().get(attrName.substring(pos + 1));
			}

			return getParent();
		} else if (PROPERTY_SUBLOCATIONS.equalsIgnoreCase(attribute)) {
			return getSublocations();
		} else if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return new UniversityCreator().getValue(getUniversity(), attrName.substring(pos + 1));
			}

			return getUniversity();
		} else if (PROPERTY_STUDENTS.equalsIgnoreCase(attribute)) {
			return getStudents();
		}
		return null;
	}

	public Room withName(String newValue) {
		this.setName(newValue);
		return this;
	}

	public Room withParent(Room newValue) {
		this.setParent(newValue);
		return this;
	}

	public Set<Room> getSublocations() {
		if (this.sublocations == null) {
			this.sublocations = new LinkedHashSet<Room>();
		}

		return Collections.unmodifiableSet(this.sublocations);
	}

	public Room withSublocations(Room newValue) {
		this.addToSublocations(newValue);
		return this;
	}

	public Room withoutSublocations(Room newValue) {
		this.removeFromSublocations(newValue);
		return this;
	}

	public Room withUniversity(University newValue) {
		this.setUniversity(newValue);
		return this;
	}

	public Set<Student> getStudents() {
		if (this.students == null) {
			this.students = new LinkedHashSet<Student>();
		}

		return Collections.unmodifiableSet(this.students);
	}

	public Room withStudents(Student newValue) {
		this.addToStudents(newValue);
		return this;
	}

	public Room withoutStudents(Student newValue) {
		this.removeFromStudents(newValue);
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

	/**
	 * <pre>
	 *           0..n     rooms0..1
	 * Room ------------------------- University
	 *           rooms        &lt;       university
	 * </pre>
	 */
	/**
	 * <pre>
	 *           0..n     parent     0..1
	 * Room ------------------------- Room
	 *           sublocations        &lt;       parent
	 * </pre>
	 */

	public static final String PROPERTY_PARENT = "parent";
	private Room parent;

	public boolean setParent(Room value) {
		boolean changed = false;

		if (this.parent != value) {
			Room oldValue = this.parent;
			if (this.parent != null) {
				this.parent = null;
				oldValue.removeFromSublocations(this);
			}
			this.parent = value;

			if (value != null) {
				value.addToSublocations(this);
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_PARENT,
					oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Room getParent() {
		return this.parent;
	}

	/**
	 * <pre>
	 *           0..1     parent     0..n
	 * Room ------------------------- Room
	 *           parent        &gt;       sublocations
	 * </pre>
	 */

	public static final String PROPERTY_SUBLOCATIONS = "sublocations";
	private LinkedHashSet<Room> sublocations;

	public boolean addToSublocations(Room value) {
		boolean changed = false;

		if (value != null) {
			if (this.sublocations == null) {
				this.sublocations = new LinkedHashSet<Room>();

			}
			changed = this.sublocations.add(value);
			if (changed) {
				value.setParent(this);
			}
		}
		return changed;
	}

	public boolean removeFromSublocations(Room value) {
		boolean changed = false;

		if ((this.sublocations != null) && (value != null)) {
			changed = this.sublocations.remove(value);
			if (changed) {
				value.setParent(null);
			}
		}
		return changed;
	}

	public void removeAllFromSublocations() {
		Room tmpValue;
		Iterator<Room> iter = this.iteratorOfSublocations();

		while (iter.hasNext()) {
			tmpValue = (Room) iter.next();
			this.removeFromSublocations(tmpValue);
		}
	}

	public boolean hasInSublocations(Room value) {
		return ((this.sublocations != null) && (value != null) && this.sublocations
				.contains(value));
	}

	public Iterator<Room> iteratorOfSublocations() {
		return ((this.sublocations == null) ? null : this.sublocations
				.iterator());

	}

	public int sizeOfSublocations() {
		return ((this.sublocations == null) ? 0 : this.sublocations.size());
	}

}
