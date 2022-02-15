package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;

/**
 * The Class PartTask.
 *
 * @author Stefan
 */
public class PartTask extends SendableItem {
	
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
	 * @return the part task
	 */
	public PartTask withType(String value) {
		setType(value);
		return this;
	}

	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";

	private String value;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		if (this.value != value) {
			String oldValue = this.value;
			this.value = value;
			firePropertyChange(PROPERTY_VALUE, oldValue, value);
		}
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the part task
	 */
	public PartTask withValue(String value) {
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
	 * @return the part task
	 */
	public PartTask withTask(Task value) {
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