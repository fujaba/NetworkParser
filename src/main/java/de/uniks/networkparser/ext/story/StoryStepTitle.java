package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepTitle implements StoryStep{
	private String title;
	
	public StoryStepTitle(String title) {
		this.title = title;
	}
	
	@Override
	public void finish() {
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void dump(HTMLEntity element) {
		element.withTitle(this.title);
		XMLEntity headerLine = element.createBodyTag("h1");
		headerLine.withValue(this.title);
	}
}
