package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.ObjectCondition;
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
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * DeepCondition.
 *
 * @author Stefan Lindel
 */

public class Deep implements SendableEntityCreator, ObjectCondition {
	/** Constant of Deep. */
	public static final String DEPTH = "depth";
	/** Variable of Deep. */
	private int depth;

	/**
	 * @param value The new Value
	 * @return Deep Instance
	 */
	public Deep withDepth(int value) {
		this.depth = value;
		return this;
	}

	/** @return The Current Deep Value */
	public int getDepth() {
		return depth;
	}

	@Override
	public boolean update(Object evt) {
		if (evt instanceof SimpleEvent) {
			return ((SimpleEvent) evt).getDepth() <= this.depth;
		}
		return false;
	}

	/**
	 * Create a new DeepFilter and return a new Instance
	 *
	 * @param value Value of depth
	 * @return a new depth Instance
	 */
	public static Deep create(int value) {
		return new Deep().withDepth(value);
	}

	@Override
	public String[] getProperties() {
		return new String[] { DEPTH };
	}

	@Override
	public Object getSendableInstance(boolean prototype) {
		return new Deep();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (DEPTH.equalsIgnoreCase(attribute)) {
			return ((Deep) entity).getDepth();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (DEPTH.equalsIgnoreCase(attribute)) {
			((Deep) entity).withDepth(Integer.parseInt("" + value));
			return true;
		}
		return false;
	}
}
