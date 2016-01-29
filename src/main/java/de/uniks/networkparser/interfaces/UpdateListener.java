package de.uniks.networkparser.interfaces;

import java.beans.PropertyChangeEvent;

/**
* The listener interface for receiving mapUpdate events. The class that is
* interested in processing a mapUpdate event implements this interface, and the
* object created with that class is registered with a component using the
* component's <code>addMapUpdateListener</code> method. When the mapUpdate
* event occurs, that object's appropriate method is invoked.
*/
@FunctionalInterface
public interface UpdateListener  {
	/**
	 * Send update msg.
	 *
	 * @param typ
	 *			the typ of Message: NEW UPDATE, REMOVE or SENDUPDATE
	 * @param source
	 *			the Source Element
	 * @param event
	 *			The Change in a PropertyChangeEvent @see java.beans.PropertyChangeEvent
	 *
	 * @return true, if successful
	 */
	public boolean update(String typ, BaseItem source, PropertyChangeEvent event);


}
