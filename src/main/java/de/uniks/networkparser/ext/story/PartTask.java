package de.uniks.networkparser.ext.story;

import de.uniks.simplescrum.model.BoardElement;

public class PartTask extends BoardElement {
	public static final String PROPERTY_TYPE = "type";

	private String type;

	public String getType() {
		return this.type;
	}

	public void setType(String value) {
		if (this.type != value) {
			String oldValue = this.type;
			this.type = value;
			firePropertyChange(PROPERTY_TYPE, oldValue, value);
		}
	}

	public PartTask withType(String value) {
		setType(value);
		return this;
	}

	public static final String PROPERTY_VALUE = "value";

	private String value;

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		if (this.value != value) {
			String oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
		}
	}

	public PartTask withValue(String value) {
		setValue(value);
		return this;
	}

	public static final String PROPERTY_TASK = "task";

	private Task task = null;

	public Task getTask() {
		return this.task;
	}

	public boolean setTask(Task value) {
		boolean changed = false;
		if (this.task != value) {
			Task oldValue = this.task;
			this.task = value;
			firePropertyChange(PROPERTY_TASK, oldValue, value);
			changed = true;
		}
		return changed;
	}

	public PartTask withTask(Task value) {
		this.setTask(value);
		return this;
	}

	public Task createTask() {
		Task value = new Task();
		withTask(value);
		return value;
	}
}