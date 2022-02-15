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
import java.util.Comparator;

/**
 * The listener interface for receiving jacocoColumn events.
 * The class that is interested in processing a jacocoColumn
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addJacocoColumnListener<code> method. When
 * the jacocoColumn event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Stefan
 */
public interface JacocoColumnListener {
	
	/**
	 * Inits the.
	 *
	 * @param items the items
	 * @param total the total
	 * @return true, if successful
	 */
	public boolean init(Object items, Object total);

	/**
	 * Footer.
	 *
	 * @param td the td
	 * @param total the total
	 * @param resources the resources
	 * @param base the base
	 */
	public void footer(Object td, Object total, Object resources, Object base);

	/**
	 * Item.
	 *
	 * @param td the td
	 * @param item the item
	 * @param resources the resources
	 * @param base the base
	 */
	public void item(Object td, Object item, Object resources, Object base);

	/**
	 * Gets the comparator.
	 *
	 * @return the comparator
	 */
	public Comparator<Object> getComparator();
}
