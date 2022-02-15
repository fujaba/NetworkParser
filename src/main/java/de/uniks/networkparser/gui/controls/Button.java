package de.uniks.networkparser.gui.controls;

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
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * The Class Button.
 *
 * @author Stefan
 */
public class Button extends Input<String> {
	/* Constants */
	protected static final String BUTTON = "button";

	private String actionType;

	/** The Constant MINIMIZE. */
	public static final String MINIMIZE = "minimize";

	/** The Constant CLOSE. */
	public static final String CLOSE = "close";

	/** The Constant MAXIMIZE. */
	public static final String MAXIMIZE = "maximize";

	/**
	 * Instantiates a new button.
	 */
	public Button() {
		super();
		/* Set variables of parent class */
		this.type = BUTTON;
	}

	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * With action type.
	 *
	 * @param graphicType the graphic type
	 * @return the button
	 */
	public Button withActionType(String graphicType) {
		this.actionType = graphicType;
		return this;
	}

	/**
	 * With action type.
	 *
	 * @param graphicType the graphic type
	 * @param conditon the conditon
	 * @return the button
	 */
	public Button withActionType(String graphicType, ObjectCondition conditon) {
		this.actionType = graphicType;
		this.addClickListener(conditon);
		return this;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the button
	 */
	public Button withValue(String value) {
		super.setValue(value);
		return this;
	}

	/**
	 * New instance.
	 *
	 * @return the button
	 */
	@Override
	public Button newInstance() {
		return new Button();
	}
}
