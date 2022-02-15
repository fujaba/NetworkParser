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

/**
 * The Class Label.
 *
 * @author Stefan
 */
public class Label extends Control {
	
	/** The Constant SPACER. */
	public static final String SPACER = "spacer";
	
	/** The Constant TITLE. */
	public static final String TITLE = "title";
	
	/** The Constant VALUE. */
	public static final String VALUE = "value";

	private String type;
	private String value;

	/**
	 * Instantiates a new label.
	 */
	public Label() {
		this.addBaseElements(VALUE);
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * With type.
	 *
	 * @param type the type
	 * @return the label
	 */
	public Label withType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the label
	 */
	public Label withValue(String value) {
		this.value = value;
		return this;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String value) {
		if (value != this.value) {
			this.value = value;
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Length.
	 *
	 * @return the int
	 */
	public int length() {
		if (this.value == null) {
			return 0;
		}
		return this.value.length();
	}

	/**
	 * New instance.
	 *
	 * @return the label
	 */
	@Override
	public Label newInstance() {
		return new Label();
	}
}
