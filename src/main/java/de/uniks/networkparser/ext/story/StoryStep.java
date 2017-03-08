package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;

public interface StoryStep {
	public void finish();
	
	public void dump(HTMLEntity element);
}
