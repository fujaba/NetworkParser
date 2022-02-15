package de.uniks.networkparser.interfaces;

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
import java.util.Comparator;

// TODO: Auto-generated Javadoc
/**
 * The Interface EntityList.
 *
 * @author Stefan
 */
public interface EntityList extends BaseItem {
	
	/**
	 * Return a Element from list.
	 *
	 * @param index index of Element
	 * @return a Element
	 */
	public BaseItem getChild(int index);

	/**
	 * Make a prettyprinted Text of this Entity.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation of
	 *         the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public String toString(int indentFactor);

	/**
	 * Checks if is comparator.
	 *
	 * @return true, if is comparator
	 */
	public boolean isComparator();

	/**
	 * Comparator.
	 *
	 * @return the comparator
	 */
	public Comparator<Object> comparator();

	/**
	 * With value.
	 *
	 * @param values the values
	 * @return the base item
	 */
	public BaseItem withValue(BufferItem values);

	/**
	 * Size children.
	 *
	 * @return the int
	 */
	public int sizeChildren();
	
	/**
	 * First child.
	 *
	 * @return the base item
	 */
	public BaseItem firstChild();
	
	
}
