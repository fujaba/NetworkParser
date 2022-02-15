package de.uniks.networkparser.interfaces;

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
import java.beans.PropertyChangeListener;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.UpdateCondition;
import de.uniks.networkparser.list.SimpleList;

/**
 * The listener interface for receiving map events.
 * The class that is interested in processing a map
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMapListener</code> method. When
 * the map event occurs, that object's appropriate
 * method is invoked.
 * @author Stefan
 * @see UpdateCondition
 */
public interface MapListener extends PropertyChangeListener {
	
	/**
	 * Execute.
	 *
	 * @param updateMessage the update message
	 * @param filter the filter
	 * @return the object
	 */
	Object execute(Entity updateMessage, Filter filter);

	/**
	 * With filter.
	 *
	 * @param filter the filter
	 * @return the map listener
	 */
	MapListener withFilter(Filter filter);

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	Filter getFilter();

	/**
	 * Suspend notification.
	 *
	 * @param accumulates the accumulates
	 * @return true, if successful
	 */
	boolean suspendNotification(UpdateCondition... accumulates);

	/**
	 * Reset notification.
	 *
	 * @return the simple list
	 */
	SimpleList<UpdateCondition> resetNotification();

	/**
	 * Gets the tokener.
	 *
	 * @return the tokener
	 */
	Tokener getTokener();
}
