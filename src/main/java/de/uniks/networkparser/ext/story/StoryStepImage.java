package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepImage implements ObjectCondition {
	private String file;

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		if(this.file != null) {
			XMLEntity image = element.createTag("img", element.getBody());
			image.put("src", file);
			//			int counter = story.getCounter();
//			XMLEntity textItem = element.createBodyTag("p");
//			textItem.add("class", "step");
//			String textValue = "";
//			if(counter>=0) {
//				textValue = "Step "+ counter+": ";
//			}
//			textValue += this.value;
//			textItem.withValueItem(textValue);
		}
		return true;
	}

	public StoryStepImage withFile(String value) {
		this.file = value;
		return this;
	}
}
