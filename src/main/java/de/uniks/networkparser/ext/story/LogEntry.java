package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.petaf.SendableItem;

public class LogEntry extends SendableItem {
	public static final String PROPERTY_CREATED = "created";

	private int created;

	public int getCreated() {
		return this.created;
	}

	public void setCreated(int value) {
		if (this.created != value) {
			int oldValue = this.created;
			this.created = value;
			firePropertyChange(PROPERTY_CREATED, oldValue, value);
		}
	}

	public LogEntry withCreated(int value) {
		setCreated(value);
		return this;
	}

	public static final String PROPERTY_CREATER = "creater";

	private String creater;

	public String getCreater() {
		return this.creater;
	}

	public void setCreater(String value) {
		if (this.creater != value) {
			String oldValue = this.creater;
			this.creater = value;
			firePropertyChange(PROPERTY_CREATER, oldValue, value);
		}
	}

	public LogEntry withCreater(String value) {
		setCreater(value);
		return this;
	}

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

	public LogEntry withType(String value) {
		setType(value);
		return this;
	}

	public static final String PROPERTY_VALUE = "value";

	private int value;

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		if (this.value != value) {
			int oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
		}
	}

	public LogEntry withValue(int value) {
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

	public LogEntry withTask(Task value) {
		this.setTask(value);
		return this;
	}

	public Task createTask() {
		Task value = new Task();
		withTask(value);
		return value;
	}
}