package de.uniks.networkparser.gui.controls;

import java.util.Collection;

import de.uniks.networkparser.list.SimpleList;

public class Group extends Control {
	/* constants */
	public static final String DIV = "div";
	public static final String ORIENTATION = "orientation";
	public static final String HORIZONTAL = "horizontal";
	public static final String VERTICAL = "vertical";

	private SimpleList<Control> elements;
	private String orientation = HORIZONTAL;

	public Group() {
		super();
		/* Set variables of parent class */
		this.className = DIV;
		this.addBaseElements(PROPERTY_ELEMENTS);
		this.addBaseElements(ORIENTATION);
	}

	public SimpleList<Control> getElements() {
		return elements;
	}

	public Group withElement(Control... elements) {
		addElement(elements);
		return this;
	}

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

	public String getOrientation() {
		return orientation;
	}

	public boolean setOrientation(String value) {
		String oldValue = this.orientation;
		this.orientation = value;
		return firePropertyChange(ORIENTATION, oldValue, value);
	}

	public Group withOrientation(String value) {
		this.setOrientation(value);
		return this;
	}

	@Override
	public Object getValue(String key) {
		if (ORIENTATION.equals(key)) {
			return this.getOrientation();
		} else if (PROPERTY_ELEMENTS.equals(key)) {
			return this.getElements();
		}
		return super.getValue(key);
	}

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

	@Override
	public Group newInstance() {
		return new Group();
	}
}
