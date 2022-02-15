package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.SimpleEvent;

/**
 * The listener interface for receiving simpleUpdate events.
 * The class that is interested in processing a simpleUpdate
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSimpleUpdateListener</code> method. When
 * the simpleUpdate event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Stefan
 * 
 * @see SimpleEvent
 */
public interface SimpleUpdateListener extends Condition<SimpleEvent> {

}
