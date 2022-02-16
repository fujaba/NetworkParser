package de.uniks.networkparser.ext.story;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class StoryStepCondition.
 *
 * @author Stefan
 */
public class StoryStepCondition implements ObjectCondition {
	private Condition<Object> condition;
	private Object value;
	private String message;

	/**
	 * With condition.
	 *
	 * @param message the message
	 * @param value the value
	 * @param condition the condition
	 * @return the story step condition
	 */
	public StoryStepCondition withCondition(String message, Object value, Condition<Object> condition) {
		this.condition = condition;
		this.value = value;
		this.message = message;
		return this;
	}

	/**
	 * With condition.
	 *
	 * @param condition the condition
	 * @return the story step condition
	 */
	public StoryStepCondition withCondition(Condition<Object> condition) {
		this.condition = condition;
		return this;
	}

	/**
	 * Check condition.
	 *
	 * @return true, if successful
	 */
	public boolean checkCondition() {
		if (this.condition != null) {
			return this.condition.update(value);
		}
		return true;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof SimpleEvent)) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		HTMLEntity element = (HTMLEntity) evt.getNewValue();
		boolean success = checkCondition();

		XMLEntity div = element.createChild("div", element.getBody());
		XMLEntity p = element.createChild("p", div);
		p.withCloseTag();
		XMLEntity textnode = element.createChild("div", div);
		textnode.add("class", "notify-text");
		if (success) {
			div.add("class", "notify notify-green");
			p.add("class", "symbol icon-tick");
		} else {
			div.add("class", "notify notify-red");
			p.add("class", "symbol icon-error");
		}
		textnode.withValueItem(this.message);
		return success;
	}

	/**
	 * With message.
	 *
	 * @param message the message
	 */
	public void withMessage(String message) {
		this.message = message;

	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
