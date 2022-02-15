package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.SimpleObject;

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

/**
 * The Class Input.
 *
 * @author Stefan
 * @param <T> the generic type
 */
public class Input<T> extends Control {
	
	/** The Constant INPUT. */
	/* constants */
	public static final String INPUT = "input";
	
	/** The Constant TYPE. */
	public static final String TYPE = "type";
	
	/** The Constant VALUE. */
	public static final String VALUE = "value";

	/* variables */
	protected T value;
	protected String type;

	/**
	 * With element.
	 *
	 * @param blub the blub
	 * @return the input
	 */
	public Input<T> withElement(SimpleObject blub) {
		return this;
	}

	/**
	 * Instantiates a new input.
	 */
	public Input() {
		super();
		/* Set variables of parent class */
		this.className = INPUT;
		this.addBaseElements(VALUE);
		this.addBaseElements(TYPE);
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	@Override
	public Object getValue(String key) {
		if (VALUE.equals(key)) {
			return this.value;
		}
		if (TYPE.equals(key)) {
			return this.type;
		}
		return super.getValue(key);
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean setValue(String key, Object value) {
		key = key.trim();
		if (VALUE.equalsIgnoreCase(key)) {
			return this.setValue((T) value);
		}
		if (TYPE.equals(key)) {
			return this.setType("" + value);
		}
		return super.setValue(key, value);
	}

	/**
	 * Sets the type.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setType(String value) {
		String oldValue = this.type;
		this.type = value;
		return firePropertyChange(TYPE, oldValue, value);
	}

	/**
	 * Gets the type.
	 *
	 * @param value the value
	 * @return the type
	 */
	public String getType(String value) {
		return this.type;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 * @return success
	 */
	public boolean setValue(T value) {
		T oldValue = this.value;
		this.value = value;
		return firePropertyChange(VALUE, oldValue, value);
	}

	/**
	 * New instance.
	 *
	 * @return the input
	 */
	@Override
	public Input<T> newInstance() {
		return new Input<T>();
	}
}
