package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.ext.petaf.SendableItem;

public abstract class StoryElement extends SendableItem {
	public abstract String getOutputFile();

	public abstract String getLabel();
}
