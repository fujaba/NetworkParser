package de.uniks.networkparser.ext.gui;

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

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Condition;

/**
 * The Interface ModelListenerInterface.
 *
 * @author Stefan
 */
public interface ModelListenerInterface extends PropertyChangeListener {
	
	/**
	 * Gets the bean.
	 *
	 * @return the bean
	 */
	public Object getBean();

	/**
	 * Sets the bean.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setBean(Object value);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(Object listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	public void removeListener(Object listener);

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value);

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue();

	/**
	 * Gets the item value.
	 *
	 * @return the item value
	 */
	public Object getItemValue();

	/**
	 * Execute call back.
	 */
	public void executeCallBack();

	/**
	 * Bind.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean bind(Object value);

	/**
	 * Unbind.
	 */
	public void unbind();

	/**
	 * Checks if is bound.
	 *
	 * @return true, if is bound
	 */
	public boolean isBound();

	/**
	 * Bind bidirectional.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean bindBidirectional(Object value);

	/**
	 * Unbind bidirectional.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean unbindBidirectional(Object value);

	/**
	 * With call back.
	 *
	 * @param listener the listener
	 * @return the model listener interface
	 */
	public ModelListenerInterface withCallBack(Condition<SimpleEvent> listener);

	/**
	 * Invalidated.
	 *
	 * @param observable the observable
	 */
	public void invalidated(Object observable);

	/**
	 * Gets the proxy.
	 *
	 * @return the proxy
	 */
	public Object getProxy();
}
