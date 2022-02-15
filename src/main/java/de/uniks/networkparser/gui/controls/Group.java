package de.uniks.networkparser.gui.controls;

import java.util.Collection;

import de.uniks.networkparser.list.SimpleList;

/**
 * The Class Group.
 *
 * @author Stefan
 */
public class Group extends Control {
	
	/** The Constant DIV. */
	/* constants */
	public static final String DIV = "div";
	
	/** The Constant ORIENTATION. */
	public static final String ORIENTATION = "orientation";
	
	/** The Constant HORIZONTAL. */
	public static final String HORIZONTAL = "horizontal";
	
	/** The Constant VERTICAL. */
	public static final String VERTICAL = "vertical";

	private SimpleList<Control> elements;
	private String orientation = HORIZONTAL;

	/**
	 * Instantiates a new group.
	 */
	public Group() {
		super();
		/* Set variables of parent class */
		this.className = DIV;
		this.addBaseElements(PROPERTY_ELEMENTS);
		this.addBaseElements(ORIENTATION);
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public SimpleList<Control> getElements() {
		return elements;
	}

	/**
	 * With element.
	 *
	 * @param elements the elements
	 * @return the group
	 */
	public Group withElement(Control... elements) {
		addElement(elements);
		return this;
	}

	/**
	 * Adds the element.
	 *
	 * @param elements the elements
	 * @return true, if successful
	 */
	public boolean addElement(Control... elements) {
		if (elements == null) {
			return false;
		}
		boolean changed = false;
		if (this.elements == null) {
			this.elements = new SimpleList<Control>();
		}
		for (Control control : elements) {
			if (this.elements.add(control)) {
				changed = true;
				firePropertyChange(PROPERTY_ELEMENTS, null, control);
			}
		}
		return changed;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * Sets the orientation.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setOrientation(String value) {
		String oldValue = this.orientation;
		this.orientation = value;
		return firePropertyChange(ORIENTATION, oldValue, value);
	}

	/**
	 * With orientation.
	 *
	 * @param value the value
	 * @return the group
	 */
	public Group withOrientation(String value) {
		this.setOrientation(value);
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	@Override
	public Object getValue(String key) {
		if (ORIENTATION.equals(key)) {
			return this.getOrientation();
		} else if (PROPERTY_ELEMENTS.equals(key)) {
			return this.getElements();
		}
		return super.getValue(key);
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(String key, Object value) {
		if (ORIENTATION.equals(key)) {
			return this.setOrientation("" + value);
		} else if (PROPERTY_ELEMENTS.equals(key)) {
			if (value instanceof Control) {
				return this.addElement((Control) value);
			} else if (value instanceof Control[]) {
				return this.addElement((Control[]) value);
			} else if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				Control[] array = ((Collection<?>) value).toArray(new Control[list.size()]);
				return this.addElement(array);
			}
		}
		return super.setValue(key, value);
	}

	/**
	 * New instance.
	 *
	 * @return the group
	 */
	@Override
	public Group newInstance() {
		return new Group();
	}
}
