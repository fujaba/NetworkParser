package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepText implements StoryStep {
	private String value;
	
	public StoryStepText withText(String text) {
		this.value = text;
		return this;
	}

	@Override
	public boolean dump(Story story, HTMLEntity element) {
		if(this.value != null) {
			int counter = story.getCounter();
			XMLEntity textItem = element.createBodyTag("p");
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

	@Override
	public void finish() {
	}
}
