package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepTitle implements ObjectCondition {
	private String title;

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		if(this.title != null) {
			element.withTitle(this.title);
			XMLEntity headerLine = element.createTag("h1", element.getBody());
			headerLine.withValue(this.title);
		}
		return true;
	}

	public String getTitle() {
		return this.title;
	}
}
