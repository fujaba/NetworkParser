package de.uniks.networkparser.interfaces;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

/**
 * The Interface SendableEntity. Add this Interface to your Modellentity to
 * activate the propertyChange Notification
 */

public interface SendableEntity {
	/**
	 * Adds the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener     the listener
	 * @return boolean if add the PropertyChangeListener
	 */
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	public boolean addPropertyChangeListener(PropertyChangeListener listener);

	public boolean removePropertyChangeListener(PropertyChangeListener listener);

	public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
