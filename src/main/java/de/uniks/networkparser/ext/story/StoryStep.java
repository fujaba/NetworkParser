package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;

public interface StoryStep {
	public void finish();
	
	public boolean dump(Story story, HTMLEntity element);
}
