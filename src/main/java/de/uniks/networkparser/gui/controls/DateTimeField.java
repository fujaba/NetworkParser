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
 * The Class DateTimeField.
 *
 * @author Stefan
 */
public class DateTimeField extends Input<String> {
	/*
	 * Constants
	 */
	protected static final String TIME = "time";
	protected static final String DATE = "date";

	/**
	 * Instantiates a new date time field.
	 */
	public DateTimeField() {
		super();
		this.type = DATE;
	}

	/**
	 * Instantiates a new date time field.
	 *
	 * @param type the type
	 */
	public DateTimeField(String type) {
		super();
		this.type = type;
	}

	/**
	 * Creates the date field.
	 *
	 * @return the date time field
	 */
	public static DateTimeField createDateField() {
		return new DateTimeField();
	}

	/**
	 * Creates the time field.
	 *
	 * @return the date time field
	 */
	public static DateTimeField createTimeField() {
		return new DateTimeField(TIME);
	}

	/**
	 * New instance.
	 *
	 * @return the date time field
	 */
	@Override
	public DateTimeField newInstance() {
		return new DateTimeField();
	}
}
