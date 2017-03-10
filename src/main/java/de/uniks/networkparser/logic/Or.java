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
import java.util.ArrayList;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
/**
 * Or Clazz for Or Conditions.
 *
 * @author Stefan Lindel
 */

public class Or implements UpdateListener, SendableEntityCreator {
	/** Constant of CHILD. */
	public static final String CHILD = "childs";
	/** Variable of Conditions. */
	private ArrayList<UpdateListener> list = new ArrayList<UpdateListener>();

	/**
	 * Static Method for instance a new Instance of Or Object.
	 *
	 * @param conditions	All Conditions.
	 * @return 			The new Instance
	 */
	public static Or create(UpdateListener... conditions) {
		return new Or().with(conditions);
	}
	
	/**
	 * Add a new UpdateListener to logic
	 *
	 * @param conditions	All Conditions.
	 * @return Or Instance
	 */
	public Or with(UpdateListener... conditions) {
		if(conditions == null) {
			return this;
		}
		for (UpdateListener condition : conditions) {
			this.list.add(condition);
		}
		return this;
	}

	/** @return List of Condition. */
	private ArrayList<UpdateListener> getList() {
		return list;
	}

	@Override
	public boolean update(Object evt) {
		boolean result = true;
		for (UpdateListener condition : list) {
			if (condition.update(evt) == false) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		CharacterBuffer sb = new CharacterBuffer();
		for (UpdateListener condition : list) {
			sb.with("[", condition.toString(), " ");
		}
		sb.trim();
		sb.with("]");
		return sb.toString();
	}

	@Override
	public String[] getProperties() {
		return new String[] {CHILD };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Or();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			return ((Or) entity).getList();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			if(value instanceof Condition) {
				((Or) entity).with((UpdateListener) value);
			}
			return true;
		}
		return false;
	}
}
