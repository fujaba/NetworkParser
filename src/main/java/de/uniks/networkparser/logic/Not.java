package de.uniks.networkparser.logic;

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
 * Not Clazz for neg. Condition.
 *
 * @author Stefan Lindel
 */

public class Not implements UpdateListener, SendableEntityCreator {
	/** Constant for ITEM. */
	public static final String ITEM = "item";
	/** Varibale for Condition. */
	private UpdateListener item;

	@Override
	public boolean update(Object evt) {
		if(item != null) {
			return !item.update(evt);
		}
		return false;
	}

	/**
	 * @return Not Condition
	 */
	public UpdateListener getItem() {
		return item;
	}

	/**
	 * @param value		for new Condition
	 * @return 			Not Instance
	 */
	public Not with(UpdateListener value) {
		this.item = value;
		return this;
	}

	@Override
	public String[] getProperties() {
		return new String[] {ITEM };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Not();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			return ((Not) entity).getItem();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (ITEM.equalsIgnoreCase(attribute)) {
			if(value instanceof UpdateListener) {
				((Not) entity).with((UpdateListener) value);
			}
		}
		return false;
	}

	public static Not create(UpdateListener condition) {
		return new Not().with(condition);
	}
}
