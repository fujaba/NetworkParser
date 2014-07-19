/**
 * <pre>
 *           0..n     student1..1
 * Student ------------------------- University
 *           student        &lt;       university
 * </pre>
 */
package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// should become a JSON Parser
// should become a JSON Parser
public class Student {
	/**
	 * <pre>
	 *           0..n     students     0..1
	 * Student ------------------------- University
	 *           students        &lt;       university
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
				oldValue.removeFromStudents(this);
			}
			this.university = value;

			if (value != null) {
				value.addToStudents(this);
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
	 *           0..n     in     0..1
	 * Student ------------------------- Room
	 *           students        &gt;       in
	 * </pre>
	 */

	public static final String PROPERTY_IN = "in";
	private Room in;

	public boolean setIn(Room value) {
		boolean changed = false;

		if (this.in != value) {
			Room oldValue = this.in;
			if (this.in != null) {
				this.in = null;
				oldValue.removeFromStudents(this);
			}
			this.in = value;

			if (value != null) {
				value.addToStudents(this);
			}
			getPropertyChangeSupport().firePropertyChange(PROPERTY_IN,
					oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Room getIn() {
		return this.in;
	}

	public void removeYou() {
		/**
		 * <pre>
		 *           0..n     in     0..1
		 * Student ------------------------- Room
		 *           students        &gt;       in
		 * </pre>
		 */
		this.setUniversity(null);
		this.setIn(null);
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
		} else if (PROPERTY_STUD_NO.equalsIgnoreCase(attrName)) {
			setStudNo((String) value);
			return true;
		} else if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attrName)) {
			setUniversity((University) value);
			return true;
		} else if (PROPERTY_IN.equalsIgnoreCase(attrName)) {
			setIn((Room) value);
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
		} else if (PROPERTY_STUD_NO.equalsIgnoreCase(attribute)) {
			return getStudNo();
		} else if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return getUniversity().get(attrName.substring(pos + 1));
			}

			return getUniversity();
		} else if (PROPERTY_IN.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return getIn().get(attrName.substring(pos + 1));
			}

			return getIn();
		}
		return null;
	}

	public Student withName(String newValue) {
		this.setName(newValue);
		return this;
	}

	public Student withStudNo(String newValue) {
		this.setStudNo(newValue);
		return this;
	}

	public Student withUniversity(University newValue) {
		this.setUniversity(newValue);
		return this;
	}

	public Student withIn(Room newValue) {
		this.setIn(newValue);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public void setName(String value) {
		if (value != null && value.equalsIgnoreCase(this.name)) {
			String oldValue = this.name;
			this.name = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME,
					oldValue, value);
		}
	}

	public String getName() {
		return this.name;
	}

	public static final String PROPERTY_STUD_NO = "studNo";

	private String studNo;

	public void setStudNo(String value) {
		if (value != null && value.equalsIgnoreCase(this.studNo)) {
			String oldValue = this.studNo;
			this.studNo = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_STUD_NO,
					oldValue, value);
		}
	}

	public String getStudNo() {
		return this.studNo;
	}

}
