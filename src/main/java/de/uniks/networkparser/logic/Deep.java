package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

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
	 * @param value
	 *			The new Value
	 * @return Deep Instance
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
	public boolean update(PropertyChangeEvent evt) {
		if(evt instanceof SimpleMapEvent) {
			return ((SimpleMapEvent)evt).getDeep() <= this.deep;
		}
		return false;
	}

	/**
	 * @param value
	 *			Value of Deep
	 * @return a new Deep Instance
	 */
	public static Deep value(int value) {
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
