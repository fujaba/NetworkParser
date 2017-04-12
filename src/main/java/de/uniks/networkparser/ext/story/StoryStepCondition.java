package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepCondition implements StoryStep{
	private Condition<Object> condition;
	private Object value;
	private String message;

	public StoryStepCondition withCondition(String message, Object value,Condition<Object> condition) {
		this.condition = condition;
		this.value = value;
		this.message = message;
		return this;
	}
	
	public StoryStepCondition withCondition(Condition<Object> condition) {
		this.condition = condition;
		return this;
	}
	
	
	@Override
	public void finish() {
	}
	
	public boolean checkCondition() {
		if(this.condition != null) {
			return this.condition.update(value);
		}
		return true;
	}

	@Override
	public boolean dump(Story story, HTMLEntity element) {
		boolean success = checkCondition();
	
		XMLEntity div = element.createBodyTag("div");
		XMLEntity p = element.createBodyTag("p", div);
		p.withCloseTag();
		XMLEntity textnode = element.createBodyTag("div", div);
		textnode.add("class", "notify-text");
		if(success) {
			div.add("class", "notify notify-green");
			p.add("class", "symbol icon-tick");
		}else {
			div.add("class", "notify notify-red");
			p.add("class", "symbol icon-error");
		}
		textnode.withValueItem(this.message);
		return success;
	}

	public void withMessage(String message) {
		this.message = message;
		
	}

	public String getMessage() {
		return message;
	}
}
