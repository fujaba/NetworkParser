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

public interface ModelListenerInterface extends PropertyChangeListener {
	public Object getBean();

	public boolean setBean(Object value);

	public String getName();

	public void addListener(Object listener);

	public void removeListener(Object listener);

	public void setValue(Object value);

	public Object getValue();

	public Object getItemValue();

	public void executeCallBack();

	public boolean bind(Object value);

	public void unbind();

	public boolean isBound();

	public boolean bindBidirectional(Object value);

	public boolean unbindBidirectional(Object value);

	public ModelListenerInterface withCallBack(Condition<SimpleEvent> listener);

	public void invalidated(Object observable);

	public Object getProxy();
}
