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
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * The listener interface for receiving methodCallback events.
 * The class that is interested in processing a methodCallback
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addMethodCallbackListener<code> method. When
 * the methodCallback event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Stefan
 */
public class MethodCallbackListener implements ObjectCondition {
	private Object element;
	private String methodName;

	/**
	 * Instantiates a new method callback listener.
	 *
	 * @param element the element
	 * @param methodName the method name
	 */
	public MethodCallbackListener(Object element, String methodName) {
		this.element = element;
		this.methodName = methodName;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		ReflectionLoader.call(element, this.methodName, value);
		return true;
	}
}
