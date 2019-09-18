package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SendableItem;

public abstract class StoryElement extends SendableItem {
	public abstract String getOutputFile(boolean calculate);

	public abstract String getLabel();

	public abstract boolean writeToFile(String... fileName);
}
