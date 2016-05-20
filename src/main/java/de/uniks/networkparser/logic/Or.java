package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
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
	 * @param conditions	All Conditions.
	 * @return Or Instance
	 */
	public Or add(UpdateListener... conditions) {
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
			if (!condition.update(evt)) {
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
				((Or) entity).add((UpdateListener) value);
			}
			return true;
		}
		return false;
	}
}
