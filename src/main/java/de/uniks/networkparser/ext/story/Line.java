package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.list.ModelSet;

public class Line extends SendableItem {
	public static final String PROPERTY_CAPTION = "caption";
	public static final String PROPERTY_COLOR = "color";

	private String caption;

	public String getCaption() {
		return this.caption;
	}

	public boolean setCaption(String value) {
		if (this.caption != value) {
			String oldValue = this.caption;
			this.caption = value;
			firePropertyChange(PROPERTY_CAPTION, oldValue, value);
			return true;
		}
		return false;
	}

	public Line withCaption(String value) {
		setCaption(value);
		return this;
	}

	private String color;

	public String getColor() {
		return this.color;
	}

	public boolean setColor(String value) {
		if (this.caption != value) {
			String oldValue = this.color;
			this.color = value;
			firePropertyChange(PROPERTY_COLOR, oldValue, value);
			return true;
		}
		return false;
	}

	public Line withColor(String value) {
		setColor(value);
		return this;
	}
	
	public static final String PROPERTY_CHILDREN = "children";

	private ModelSet<Task> children = null;

	public ModelSet<Task> getChildren() {
		return this.children;
	}

	public Line withChildren(Task... value) {
		if (value == null) {
			return this;
		}
		for (Task item : value) {
			if (item != null) {
				if (this.children == null) {
					this.children = new ModelSet<Task>();
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

	public Line withOwner(StoryBook value) {
		this.setOwner(value);
		return this;
	}

	public StoryBook createOwner() {
		StoryBook value = new StoryBook();
		withOwner(value);
		return value;
	}
}