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

import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class And implements Condition<ValuesSimple>, SendableEntityCreator {
	public static final String CHILD = "childs";
	private ArrayList<Condition<ValuesSimple>> list = new ArrayList<Condition<ValuesSimple>>();

	@SuppressWarnings("unchecked")
	public And add(Condition<ValuesSimple>... conditions) {
		for (Condition<ValuesSimple> condition : conditions) {
			this.list.add(condition);
		}
		return this;
	}

	public ArrayList<Condition<ValuesSimple>> getList() {
		return list;
	}

	@Override
	public boolean check(ValuesSimple values) {
		for (Condition<ValuesSimple> condition : list) {
			if (!condition.check(values)) {
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (CHILD.equalsIgnoreCase(attribute)) {
			((And) entity).add((Condition<ValuesSimple>) value);
			return true;
		}
		return false;
	}
}
