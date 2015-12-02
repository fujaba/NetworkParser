package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.Condition;
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
/**
 * @author Stefan Lindel IfCondition Clazz
 */

public class IfCondition implements Condition<ValuesSimple>, SendableEntityCreator {
	/** Constant for Expression. */
	public static final String EXPRESSION = "expression";
	/** Constant for TrueCase. */
	public static final String TRUECONDITION = "truecondition";
	/** Constant for False Case. */
	public static final String FALSECONDITION = "falsecondition";

	/** Variable for Expression. */
	private Condition<ValuesSimple> expression;
	/** Variable for True Case. */
	private Condition<ValuesSimple> trueCondition;
	/** Variable for False Case. */
	private Condition<ValuesSimple> falseCondition;

	/**
	 * @param value
	 *            Set the new Expression
	 * @return IfCondition Instance
	 */
	public IfCondition withExpression(Condition<ValuesSimple> value) {
		this.expression = value;
		return this;
	}

	/** @return The Expression */
	public Condition<ValuesSimple> getExpression() {
		return expression;
	}

	/**
	 * @param condition
	 *            Ste The True Case
	 * @return InstanceOf Instance
	 */
	public IfCondition withTrue(Condition<ValuesSimple> condition) {
		this.trueCondition = condition;
		return this;
	}

	/** @return The True Case */
	public Condition<ValuesSimple> getTrue() {
		return trueCondition;
	}

	/**
	 * @param condition
	 *            Set the False Case
	 * @return IfCondition Instance
	 */
	public IfCondition withFalse(Condition<ValuesSimple> condition) {
		this.falseCondition = condition;
		return this;
	}

	/** @return The False Case */
	public Condition<ValuesSimple> getFalse() {
		return falseCondition;
	}

	@Override
	public boolean check(ValuesSimple values) {
		if (expression.check(values)) {
			if (trueCondition != null) {
				return trueCondition.check(values);
			}
		} else {
			if (falseCondition != null) {
				return falseCondition.check(values);
			}
		}
		return false;
	}

	@Override
	public String[] getProperties() {
		return new String[] {EXPRESSION, TRUECONDITION, FALSECONDITION };
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new IfCondition();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (EXPRESSION.equalsIgnoreCase(attribute)) {
			return ((IfCondition) entity).getExpression();
		}
		if (TRUECONDITION.equalsIgnoreCase(attribute)) {
			return ((IfCondition) entity).getTrue();
		}
		if (FALSECONDITION.equalsIgnoreCase(attribute)) {
			return ((IfCondition) entity).getFalse();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (EXPRESSION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withExpression((Condition<ValuesSimple>) value);
			return true;
		}
		if (TRUECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withTrue((Condition<ValuesSimple>) value);
			return true;
		}
		if (FALSECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withFalse((Condition<ValuesSimple>) value);
			return true;
		}
		return false;
	}
}
