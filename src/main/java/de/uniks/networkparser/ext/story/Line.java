package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.list.ModelSet;

/**
 * The Class Line.
 *
 * @author Stefan
 */
public class Line extends SendableItem {
	
	/** The Constant PROPERTY_CAPTION. */
	public static final String PROPERTY_CAPTION = "caption";
	
	/** The Constant PROPERTY_COLOR. */
	public static final String PROPERTY_COLOR = "color";

	private String caption;

	/**
	 * Gets the caption.
	 *
	 * @return the caption
	 */
	public String getCaption() {
		return this.caption;
	}

	/**
	 * Sets the caption.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setCaption(String value) {
		if (this.caption != value) {
			String oldValue = this.caption;
			this.caption = value;
			firePropertyChange(PROPERTY_CAPTION, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With caption.
	 *
	 * @param value the value
	 * @return the line
	 */
	public Line withCaption(String value) {
		setCaption(value);
		return this;
	}

	private String color;

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * Sets the color.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setColor(String value) {
		if (this.caption != value) {
			String oldValue = this.color;
			this.color = value;
			firePropertyChange(PROPERTY_COLOR, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With color.
	 *
	 * @param value the value
	 * @return the line
	 */
	public Line withColor(String value) {
		setColor(value);
		return this;
	}

	/** The Constant PROPERTY_CHILDREN. */
	public static final String PROPERTY_CHILDREN = "children";

	private ModelSet<Task> children = null;

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ModelSet<Task> getChildren() {
		return this.children;
	}

	/**
	 * With children.
	 *
	 * @param value the value
	 * @return the line
	 */
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

	/**
	 * Without children.
	 *
	 * @param value the value
	 * @return the line
	 */
	public Line withoutChildren(Task... value) {
		if(value != null) {
			for (Task item : value) {
				if (this.children != null && item != null) {
					this.children.remove(item);
				}
			}
		}
		return this;
	}

	/**
	 * Creates the children.
	 *
	 * @return the task
	 */
	public Task createChildren() {
		Task value = new Task();
		withChildren(value);
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
	 * @return the line
	 */
	public Line withOwner(StoryBook value) {
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