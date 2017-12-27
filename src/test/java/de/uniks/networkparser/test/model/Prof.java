/**
 * <pre>
 *		   0..n	 student1..1
 * Student ------------------------- University
 *		   student		&lt;	   university
 * </pre>
 */
package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class Prof extends Person implements SendableEntity{
	/**
	 * <pre>
	 *		   0..n	 students	 0..1
	 * Student ------------------------- University
	 *		   students		&lt;	   university
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
			}
			this.university = value;

			firePropertyChange(PROPERTY_UNIVERSITY,
					oldValue, value);
			changed = true;
		}
		return changed;
	}

	public University getUniversity() {
		return this.university;
	}

	public void removeYou() {
		/**
		 * <pre>
		 *		   0..n	 in	 0..1
		 * Student ------------------------- Room
		 *		   students		&gt;	   in
		 * </pre>
		 */
		this.setUniversity(null);
	}
	
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
		return true;
	}

	public boolean removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(property, listener);
		}
		return true;
	}

	public boolean set(String attrName, Object value) {
		if (PROPERTY_NAME.equalsIgnoreCase(attrName)) {
			setName((String) value);
			return true;
		}
		if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attrName)) {
			setUniversity((University) value);
			return true;
		}
		if (PROPERTY_FIRSTNAME.equalsIgnoreCase(attrName)) {
			setFirstName((String) value);
			return true;
		}
		if (PROPERTY_LASTNAME.equalsIgnoreCase(attrName)) {
			setLastName((String) value);
			return true;
		}
		if (PROPERTY_ITEM.equalsIgnoreCase(attrName)) {
			withItem((Item) value);
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
		}
		if (PROPERTY_UNIVERSITY.equalsIgnoreCase(attribute)) {
			if (pos > 0) {
				return new UniversityCreator().getValue(getUniversity(), attrName.substring(pos + 1));
			}
			return getUniversity();
		}
		if (PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)) {
			return getFirstName();
		}
		if (PROPERTY_LASTNAME.equalsIgnoreCase(attribute)) {
			return getLastName();
		}
		if (PROPERTY_ITEM.equalsIgnoreCase(attribute)) {
			return getItem();
		}
		return null;
	}

	public Prof withName(String newValue) {
		this.setName(newValue);
		return this;
	}

	public Prof withUniversity(University newValue) {
		this.setUniversity(newValue);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public void setName(String value) {
		if (value != null && value.equalsIgnoreCase(this.name) == false) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public String getName() {
		return this.name;
	}

	public static final String PROPERTY_FIRSTNAME = "firstName";

	private String firstName;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String value) {
		String oldValue = this.firstName;
		this.firstName = value;
		firePropertyChange(PROPERTY_FIRSTNAME, oldValue, value);
	}

	public Prof withFirstName(String value) {
		setFirstName(value);
		return this;
	}

	// ==========================================================================

	public static final String PROPERTY_LASTNAME = "lastName";

	private String lastName;

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String value) {

		String oldValue = this.lastName;
		this.lastName = value;
		firePropertyChange(PROPERTY_LASTNAME, oldValue, value);
	}

	public Prof withLastName(String value) {
		setLastName(value);
		return this;
	}

}
