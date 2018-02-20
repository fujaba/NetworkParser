package de.uniks.networkparser.ext.story;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class StoryStepCondition implements ObjectCondition {
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

	public boolean checkCondition() {
		if(this.condition != null) {
			return this.condition.update(value);
		}
		return true;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		boolean success = checkCondition();

		XMLEntity div = element.createTag("div", element.getBody());
		XMLEntity p = element.createTag("p", div);
		p.withCloseTag();
		XMLEntity textnode = element.createTag("div", div);
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
