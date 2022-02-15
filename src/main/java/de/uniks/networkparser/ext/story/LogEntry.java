package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;

/**
 * The Class LogEntry.
 *
 * @author Stefan
 */
public class LogEntry extends SendableItem {
	
	/** The Constant PROPERTY_CREATED. */
	public static final String PROPERTY_CREATED = "created";

	private int created;

	/**
	 * Gets the created.
	 *
	 * @return the created
	 */
	public int getCreated() {
		return this.created;
	}

	/**
	 * Sets the created.
	 *
	 * @param value the new created
	 */
	public void setCreated(int value) {
		if (this.created != value) {
			int oldValue = this.created;
			this.created = value;
			firePropertyChange(PROPERTY_CREATED, oldValue, value);
		}
	}

	/**
	 * With created.
	 *
	 * @param value the value
	 * @return the log entry
	 */
	public LogEntry withCreated(int value) {
		setCreated(value);
		return this;
	}

	/** The Constant PROPERTY_CREATER. */
	public static final String PROPERTY_CREATER = "creater";

	private String creater;

	/**
	 * Gets the creater.
	 *
	 * @return the creater
	 */
	public String getCreater() {
		return this.creater;
	}

	/**
	 * Sets the creater.
	 *
	 * @param value the new creater
	 */
	public void setCreater(String value) {
		if (this.creater != value) {
			String oldValue = this.creater;
			this.creater = value;
			firePropertyChange(PROPERTY_CREATER, oldValue, value);
		}
	}

	/**
	 * With creater.
	 *
	 * @param value the value
	 * @return the log entry
	 */
	public LogEntry withCreater(String value) {
		setCreater(value);
		return this;
	}

	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";

	private String type;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 *
	 * @param value the new type
	 */
	public void setType(String value) {
		if (this.type != value) {
			String oldValue = this.type;
			this.type = value;
			firePropertyChange(PROPERTY_TYPE, oldValue, value);
		}
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the log entry
	 */
	public LogEntry withType(String value) {
		setType(value);
		return this;
	}

	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";

	private int value;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(int value) {
		if (this.value != value) {
			int oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
		}
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the log entry
	 */
	public LogEntry withValue(int value) {
		setValue(value);
		return this;
	}

	/** The Constant PROPERTY_TASK. */
	public static final String PROPERTY_TASK = "task";

	private Task task = null;

	/**
	 * Gets the task.
	 *
	 * @return the task
	 */
	public Task getTask() {
		return this.task;
	}

	/**
	 * Sets the task.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
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

	/**
	 * With task.
	 *
	 * @param value the value
	 * @return the log entry
	 */
	public LogEntry withTask(Task value) {
		this.setTask(value);
		return this;
	}

	/**
	 * Creates the task.
	 *
	 * @return the task
	 */
	public Task createTask() {
		Task value = new Task();
		withTask(value);
		return value;
	}
}