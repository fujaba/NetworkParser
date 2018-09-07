package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.EntityUtil;
/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepText implements ObjectCondition {
	private String value;
	private String tag="p";
	private boolean isStep = true;

	public StoryStepText withText(String text) {
		this.value = text;
		return this;
	}

	public  StoryStepText withHTMLCode(String text) {
		this.value = EntityUtil.encode(text);
		this.tag = "pre";
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
		if(this.value != null && isStep) {
			int counter = story.getCounter();
			XMLEntity textItem = element.createTag(tag, element.getBody());
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

	public boolean setStep(boolean value) {
		if(this.isStep != value) {
			this.isStep = value;
			return true;
		}
		return false;
	}
}
