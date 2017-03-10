package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepTitle implements StoryStep{
	private String title;
	
	@Override
	public void finish() {
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean dump(Story story, HTMLEntity element) {
		if(this.title != null) {
			element.withTitle(this.title);
			XMLEntity headerLine = element.createBodyTag("h1");
			headerLine.withValue(this.title);
		}
		return true;
	}

	public String getTitle() {
		return this.title;
	}
}
