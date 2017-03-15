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

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class And implements ObjectCondition, SendableEntityCreator {
	public static final String CHILD = "childs";
	private ArrayList<ObjectCondition> list = new ArrayList<ObjectCondition>();

	/**
	 * Static Method for instance a new Instance of And Object.
	 *
	 * @param conditions	All Conditions.
	 * @return 			The new Instance
	 */
	public static And create(ObjectCondition... conditions) {
		return new And().with(conditions);
	}
	
	public And with(ObjectCondition... conditions) {
		if(conditions == null) {
			return this;
		}
		for (ObjectCondition condition : conditions) {
			this.list.add(condition);
		}
		return this;
	}

	public ArrayList<ObjectCondition> getList() {
		return list;
	}

	@Override
	public boolean update(Object evt) {
		for (ObjectCondition condition : list) {
			if (!condition.update(evt)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String[] getProperties() {
		return new String[] {CHILD };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new And();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			return ((And) entity).getList();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			((And) entity).with((ObjectCondition) value);
			return true;
		}
		return false;
	}
}
