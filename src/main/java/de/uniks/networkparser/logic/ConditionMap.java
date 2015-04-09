package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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

/**
 * @author Stefan Lindel ConditionMap Clazz
 */
public abstract class ConditionMap implements Condition<ValuesSimple> {
	@Override
	public boolean check(ValuesSimple values) {
		if (values instanceof ValuesMap) {
			return check((ValuesMap) values);
		}
		return false;
	}

	/**
	 * @param values
	 *            The Value of Match-Question
	 * @return booelan if match
	 */
	public abstract boolean check(ValuesMap values);
}
