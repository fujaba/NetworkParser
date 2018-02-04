package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepText implements ObjectCondition {
	private String value;
	
	public StoryStepText withText(String text) {
		this.value = text;
		return this;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		Story story = (Story) evt.getSource();
		if(this.value != null) {
			int counter = story.getCounter();
			XMLEntity textItem = element.createTag("p", element.getBody());
			textItem.add("class", "step");
			String textValue = "";
			if(counter>=0) {
				textValue = "Step "+ counter+": ";
			}
			textValue += this.value;
			
			textItem.withValueItem(textValue);
		}
		return true;
	}

	public String getText() {
		return this.value;
	}
}