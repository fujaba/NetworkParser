package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.list.ModelSet;

/**
 * The Class Task.
 *
 * @author Stefan
 */
public class Task extends SendableItem {
	
	/** The Constant PROPERTY_COMPLEXITY. */
	public static final String PROPERTY_COMPLEXITY = "complexity";

	private String complexity;

	/**
	 * Gets the complexity.
	 *
	 * @return the complexity
	 */
	public String getComplexity() {
		return this.complexity;
	}

	/**
	 * Sets the complexity.
	 *
	 * @param value the new complexity
	 */
	public void setComplexity(String value) {
		if (this.complexity != value) {
			String oldValue = this.complexity;
			this.complexity = value;
			firePropertyChange(PROPERTY_COMPLEXITY, oldValue, value);
		}
	}

	/**
	 * With complexity.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withComplexity(String value) {
		setComplexity(value);
		return this;
	}

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
	 * @return the task
	 */
	public Task withCreated(int value) {
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
	 * @return the task
	 */
	public Task withCreater(String value) {
		setCreater(value);
		return this;
	}

	/** The Constant PROPERTY_DESCRIPTION. */
	public static final String PROPERTY_DESCRIPTION = "description";

	private String description;

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param value the new description
	 */
	public void setDescription(String value) {
		if (this.description != value) {
			String oldValue = this.description;
			this.description = value;
			firePropertyChange(PROPERTY_DESCRIPTION, oldValue, value);
		}
	}

	/**
	 * With description.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withDescription(String value) {
		setDescription(value);
		return this;
	}

	/** The Constant PROPERTY_ESTIMATE. */
	public static final String PROPERTY_ESTIMATE = "estimate";

	private String estimate;

	/**
	 * Gets the estimate.
	 *
	 * @return the estimate
	 */
	public String getEstimate() {
		return this.estimate;
	}

	/**
	 * Sets the estimate.
	 *
	 * @param value the new estimate
	 */
	public void setEstimate(String value) {
		if (this.estimate != value) {
			String oldValue = this.estimate;
			this.estimate = value;
			firePropertyChange(PROPERTY_ESTIMATE, oldValue, value);
		}
	}

	/**
	 * With estimate.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withEstimate(String value) {
		setEstimate(value);
		return this;
	}

	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";

	private String name;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param value the new name
	 */
	public void setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	/**
	 * With name.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withName(String value) {
		setName(value);
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
	 * @return the task
	 */
	public Task withType(String value) {
		setType(value);
		return this;
	}

	/** The Constant PROPERTY_LIESON. */
	public static final String PROPERTY_LIESON = "liesOn";

	private Line liesOn = null;

	/**
	 * Gets the lies on.
	 *
	 * @return the lies on
	 */
	public Line getLiesOn() {
		return this.liesOn;
	}

	/**
	 * Sets the lies on.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setLiesOn(Line value) {
		boolean changed = false;
		if (this.liesOn != value) {
			Line oldValue = this.liesOn;
			this.liesOn = value;
			firePropertyChange(PROPERTY_LIESON, oldValue, value);
			changed = true;
		}
		return changed;
	}

	/**
	 * With lies on.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withLiesOn(Line value) {
		this.setLiesOn(value);
		return this;
	}

	/**
	 * Creates the lies on.
	 *
	 * @return the line
	 */
	public Line createLiesOn() {
		Line value = new Line();
		withLiesOn(value);
		return value;
	}

	/** The Constant PROPERTY_PART. */
	public static final String PROPERTY_PART = "part";

	private ModelSet<Task> part = null;

	/**
	 * Gets the part.
	 *
	 * @return the part
	 */
	public ModelSet<Task> getPart() {
		return this.part;
	}

	/**
	 * With part.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withPart(PartTask... value) {
		if (value == null) {
			return this;
		}
		for (PartTask item : value) {
			if (item != null) {
				if (this.part == null) {
					this.part = new ModelSet<Task>();
				}
				boolean changed = this.part.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_PART, null, item);
				}
			}
		}
		return this;
	}

	/**
	 * Without part.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withoutPart(PartTask... value) {
		if (value != null) {
			for (PartTask item : value) {
				if (this.part != null && item != null) {
					this.part.remove((Object) item);
				}
			}
		}
		return this;
	}

	/**
	 * Creates the part.
	 *
	 * @return the part task
	 */
	public PartTask createPart() {
		PartTask value = new PartTask();
		withPart(value);
		return value;
	}

	/** The Constant PROPERTY_UPDATE. */
	public static final String PROPERTY_UPDATE = "update";

	private ModelSet<LogEntry> update = null;

	/**
	 * Gets the update.
	 *
	 * @return the update
	 */
	public ModelSet<LogEntry> getUpdate() {
		return this.update;
	}

	/**
	 * With update.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withUpdate(LogEntry... value) {
		if (value == null) {
			return this;
		}
		for (LogEntry item : value) {
			if (item != null) {
				if (this.update == null) {
					this.update = new ModelSet<LogEntry>();
				}
				boolean changed = this.update.add(item);
				if (changed) {
					firePropertyChange(PROPERTY_UPDATE, null, item);
				}
			}
		}
		return this;
	}

	/**
	 * Without update.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withoutUpdate(LogEntry... value) {
		if (value != null) {
			for (LogEntry item : value) {
				if (this.update != null && item != null) {
					this.update.remove(item);
				}
			}
		}
		return this;
	}

	/**
	 * Creates the update.
	 *
	 * @return the log entry
	 */
	public LogEntry createUpdate() {
		LogEntry value = new LogEntry();
		withUpdate(value);
		return value;
	}

	/** The Constant PROPERTY_OWNER. */
	public static final String PROPERTY_OWNER = "owner";

	private StoryBook owner = null;

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public StoryBook getOwner() {
		return this.owner;
	}

	/**
	 * Sets the owner.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setOwner(StoryBook value) {
		boolean changed = false;
		if (this.owner != value) {
			StoryBook oldValue = this.owner;
			this.owner = value;
			firePropertyChange(PROPERTY_OWNER, oldValue, value);
			changed = true;
		}
		return changed;
	}

	/**
	 * With owner.
	 *
	 * @param value the value
	 * @return the task
	 */
	public Task withOwner(StoryBook value) {
		this.setOwner(value);
		return this;
	}

	/**
	 * Creates the owner.
	 *
	 * @return the story book
	 */
	public StoryBook createOwner() {
		StoryBook value = new StoryBook();
		withOwner(value);
		return value;
	}
}