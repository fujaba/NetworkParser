package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.list.ModelSet;

public class Task extends SendableItem {
	public static final String PROPERTY_COMPLEXITY = "complexity";

	private String complexity;

	public String getComplexity() {
		return this.complexity;
	}

	public void setComplexity(String value) {
		if (this.complexity != value) {
			String oldValue = this.complexity;
			this.complexity = value;
			firePropertyChange(PROPERTY_COMPLEXITY, oldValue, value);
		}
	}

	public Task withComplexity(String value) {
		setComplexity(value);
		return this;
	}

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

	public Task withCreated(int value) {
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

	public Task withCreater(String value) {
		setCreater(value);
		return this;
	}

	public static final String PROPERTY_DESCRIPTION = "description";

	private String description;

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String value) {
		if (this.description != value) {
			String oldValue = this.description;
			this.description = value;
			firePropertyChange(PROPERTY_DESCRIPTION, oldValue, value);
		}
	}

	public Task withDescription(String value) {
		setDescription(value);
		return this;
	}

	public static final String PROPERTY_ESTIMATE = "estimate";

	private String estimate;

	public String getEstimate() {
		return this.estimate;
	}

	public void setEstimate(String value) {
		if (this.estimate != value) {
			String oldValue = this.estimate;
			this.estimate = value;
			firePropertyChange(PROPERTY_ESTIMATE, oldValue, value);
		}
	}

	public Task withEstimate(String value) {
		setEstimate(value);
		return this;
	}

	public static final String PROPERTY_NAME = "name";

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public Task withName(String value) {
		setName(value);
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

	public Task withType(String value) {
		setType(value);
		return this;
	}

	public static final String PROPERTY_LIESON = "liesOn";

	private Line liesOn = null;

	public Line getLiesOn() {
		return this.liesOn;
	}

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

	public Task withLiesOn(Line value) {
		this.setLiesOn(value);
		return this;
	}

	public Line createLiesOn() {
		Line value = new Line();
		withLiesOn(value);
		return value;
	}

	public static final String PROPERTY_PART = "part";

	private ModelSet<Task> part = null;

	public ModelSet<Task> getPart() {
		return this.part;
	}

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

	public PartTask createPart() {
		PartTask value = new PartTask();
		withPart(value);
		return value;
	}

	public static final String PROPERTY_UPDATE = "update";

	private ModelSet<LogEntry> update = null;

	public ModelSet<LogEntry> getUpdate() {
		return this.update;
	}

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

	public LogEntry createUpdate() {
		LogEntry value = new LogEntry();
		withUpdate(value);
		return value;
	}

	public static final String PROPERTY_OWNER = "owner";

	private StoryBook owner = null;

	public StoryBook getOwner() {
		return this.owner;
	}

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

	public Task withOwner(StoryBook value) {
		this.setOwner(value);
		return this;
	}

	public StoryBook createOwner() {
		StoryBook value = new StoryBook();
		withOwner(value);
		return value;
	}
}