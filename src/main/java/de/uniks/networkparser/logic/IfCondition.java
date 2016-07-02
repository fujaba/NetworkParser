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
 * @author Stefan Lindel IfCondition Clazz
 */

public class IfCondition implements UpdateListener, SendableEntityCreator {
	/** Constant for Expression. */
	public static final String EXPRESSION = "expression";
	/** Constant for TrueCase. */
	public static final String TRUECONDITION = "truecondition";
	/** Constant for False Case. */
	public static final String FALSECONDITION = "falsecondition";

	/** Variable for Expression. */
	private UpdateListener expression;
	/** Variable for True Case. */
	private UpdateListener trueCondition;
	/** Variable for False Case. */
	private UpdateListener falseCondition;

	/**
	 * @param value		Set the new Expression
	 * @return 			IfCondition Instance
	 */
	public IfCondition withExpression(UpdateListener value) {
		this.expression = value;
		return this;
	}

	/** @return The Expression */
	public UpdateListener getExpression() {
		return expression;
	}

	/**
	 * @param condition		Set The True Case
	 * @return 				InstanceOf Instance
	 */
	public IfCondition withTrue(UpdateListener condition) {
		this.trueCondition = condition;
		return this;
	}

	/** @return The True Case */
	public UpdateListener getTrue() {
		return trueCondition;
	}

	/**
	 * @param condition		Set the False Case
	 * @return 				IfCondition Instance
	 */
	public IfCondition withFalse(UpdateListener condition) {
		this.falseCondition = condition;
		return this;
	}

	/** @return The False Case */
	public UpdateListener getFalse() {
		return falseCondition;
	}

	@Override
	public boolean update(Object evt) {
		if (expression != null && expression.update(evt)) {
			if (trueCondition != null) {
				return trueCondition.update(evt);
			}
		} else {
			if (falseCondition != null) {
				return falseCondition.update(evt);
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

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (EXPRESSION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withExpression((UpdateListener) value);
			return true;
		}
		if (TRUECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withTrue((UpdateListener) value);
			return true;
		}
		if (FALSECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withFalse((UpdateListener) value);
			return true;
		}
		return false;
	}
}
