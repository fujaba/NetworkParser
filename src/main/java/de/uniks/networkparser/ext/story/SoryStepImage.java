package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class SoryStepImage implements StoryStep{
	private String file;

	@Override
	public void finish() {
	}

	@Override
	public boolean dump(Story story, HTMLEntity element) {
		if(this.file != null) {
			XMLEntity image = element.createBodyTag("img");
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

	public StoryStep withFile(String value) {
		this.file = value;
		return this;
	}
}
