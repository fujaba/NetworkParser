package de.uniks.networkparser;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
/**
 * DeepCondition.
 *
 * @author Stefan Lindel
 */

public class Deep implements SendableEntityCreator, UpdateListener {
	/** Constant of Deep. */
	public static final String DEEP = "deep";
	/** Variable of Deep. */
	private int deep;

	/**
	 * @param value		The new Value
	 * @return 			Deep Instance
	 */
	public Deep withDeep(int value) {
		this.deep = value;
		return this;
	}

	/** @return The Current Deep Value */
	public int getDeep() {
		return deep;
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof SimpleEvent) {
			return ((SimpleEvent)evt).getDeep() <= this.deep;
		}
		return false;
	}

	/**
	 * Create a new DeepFilter and return a new Instance
	 *
	 * @param value		Value of Deep
	 * @return 			a new Deep Instance
	 */
	public static Deep create(int value) {
		return new Deep().withDeep(value);
	}

	@Override
	public String[] getProperties() {
		return new String[] {DEEP };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Deep();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (DEEP.equalsIgnoreCase(attribute)) {
			return ((Deep) entity).getDeep();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (DEEP.equalsIgnoreCase(attribute)) {
			((Deep) entity).withDeep(Integer.parseInt("" + value));
			return true;
		}
		return false;
	}
}
