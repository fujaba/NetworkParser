package de.uniks.networkparser.gui;

/*
NetworkParser
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
 * The Interface CellEditorElement.
 * @author Stefan
 */
public interface CellEditorElement {
    /** The save. */
    public static final String SAVE ="SAVE";
    /** The tab. */
    public static final String TAB ="TAB";
    /** The enter. */
    public static final String ENTER ="ENTER";
    /** The focus. */
    public static final String FOCUS ="FOCUS";
	/**
	 * With column.
	 *
	 * @param column the column
	 * @return the cell editor element
	 */
	CellEditorElement withColumn(Column column);

	/**
	 * Cancel.
	 */
	void cancel();

	/**
	 * Sets the focus.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	boolean setFocus(boolean value);

	/**
	 * On active.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	boolean onActive(boolean value);

	/**
	 * Next focus.
	 *
	 * @return true, if successful
	 */
	boolean nextFocus();

	/**
	 * Apply.
	 *
	 * @param action the action
	 */
	void apply(String action);

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Gets the value.
	 *
	 * @param convert the convert
	 * @return the value
	 */
	Object getValue(boolean convert);

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the cell editor element
	 */
	CellEditorElement withValue(Object value);
}
