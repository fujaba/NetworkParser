package de.uniks.networkparser.ext.story;

import de.uniks.simplescrum.model.BoardElement;
import de.uniks.simplescrum.model.util.TaskSet;

public class Line extends BoardElement {
	public static final String PROPERTY_CAPTION = "caption";

	private String caption;

	public String getCaption() {
		return this.caption;
	}

	public void setCaption(String value) {
		if (this.caption != value) {
			String oldValue = this.caption;
			this.caption = value;
			firePropertyChange(PROPERTY_CAPTION, oldValue, value);
		}
	}

	public Line withCaption(String value) {
		setCaption(value);
		return this;
	}

	public static final String PROPERTY_CHILDREN = "children";

	private TaskSet children = null;

	public TaskSet getChildren() {
		return this.children;
	}

	public Line withChildren(Task... value) {
		if (value == null) {
			return this;
		}
		for (Task item : value) {
			if (item != null) {
				if (this.children == null) {
					this.children = new TaskSet();
				}
				boolean changed = this.children.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_CHILDREN, null, item);
				}
			}
		}
		return this;
	}

	public Line withoutChildren(Task... value) {
		for (Task item : value) {
			if (this.children != null && item != null) {
				this.children.remove(item);
			}
		}
		return this;
	}

	public Task createChildren() {
		Task value = new Task();
		withChildren(value);
		return value;
	}

	public static final String PROPERTY_OWNER = "owner";

	private Board owner = null;

	public Board getOwner() {
		return this.owner;
	}

	public boolean setOwner(Board value) {
		boolean changed = false;
		if (this.owner != value) {
			Board oldValue = this.owner;
			this.owner = value;
			firePropertyChange(PROPERTY_OWNER, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public Line withOwner(Board value) {
		this.setOwner(value);
		return this;
	}

	public Board createOwner() {
		Board value = new Board();
		withOwner(value);
		return value;
	}
}