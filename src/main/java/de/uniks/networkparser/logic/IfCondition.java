package de.uniks.networkparser.logic;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.parser.TemplateResultFragment;

/**
 * @author Stefan Lindel IfCondition Clazz
 */

public class IfCondition implements ParserCondition, SendableEntityCreator {
	public static final String TAG = "if";
	public static final String IFNOT = "ifnot";
	/** Constant for Expression. */
	public static final String EXPRESSION = "expression";
	/** Constant for TrueCase. */
	public static final String TRUECONDITION = "truecondition";
	/** Constant for False Case. */
	public static final String FALSECONDITION = "falsecondition";

	private String tag = TAG;

	/** Variable for Expression. */
	private ObjectCondition expression;
	/** Variable for True Case. */
	private ObjectCondition trueCondition;
	/** Variable for False Case. */
	private ObjectCondition falseCondition;

	private CharacterBuffer notifyBuffer = null;

	/**
	 * @param value Set the new Expression
	 * @return IfCondition Instance
	 */
	public IfCondition withExpression(ObjectCondition value) {
		this.expression = value;
		return this;
	}

	/** @return The Expression */
	public ObjectCondition getExpression() {
		return expression;
	}

	/**
	 * @param condition Set The True Case
	 * @return InstanceOf Instance
	 */
	public IfCondition withTrue(ObjectCondition condition) {
		this.trueCondition = condition;
		return this;
	}

	/** @return The True Case */
	public ObjectCondition getTrue() {
		return trueCondition;
	}

	/**
	 * @param condition Set the False Case
	 * @return IfCondition Instance
	 */
	public IfCondition withFalse(ObjectCondition condition) {
		this.falseCondition = condition;
		return this;
	}

	/** @return The False Case */
	public ObjectCondition getFalse() {
		return falseCondition;
	}

	@Override
	public boolean update(Object evt) {
		if (expression != null && expression.update(evt)) {
			if (trueCondition != null) {
				if (this.notifyBuffer == null || evt instanceof LocalisationInterface == false) {
					return trueCondition.update(evt);
				}
				LocalisationInterface li = (LocalisationInterface) evt;
				li.put(NOTIFY, this);
				notifyBuffer.clear();
				boolean success = trueCondition.update(evt);
				li.put(NOTIFY, null);
				/* NOTIFY */
				if (evt instanceof SendableEntityCreator) {
					GraphMember member = (GraphMember) ((SendableEntityCreator) evt).getValue(evt,
							TemplateResultFragment.PROPERTY_CURRENTMEMBER);
					if (member != null) {
						ObjectCondition oc = GraphUtil.getRole(member);
						if (oc != null) {
							oc.update(notifyBuffer);
						}
					}
				}
				return success;
			}
			return true;
		} else {
			if (falseCondition != null) {
				if (this.notifyBuffer == null || evt instanceof LocalisationInterface == false) {
					return falseCondition.update(evt);
				}
				LocalisationInterface li = (LocalisationInterface) evt;
				li.put(NOTIFY, this);
				notifyBuffer.clear();
				boolean success = trueCondition.update(evt);
				li.put(NOTIFY, null);
				/* NOTIFY */
				if (evt instanceof SendableEntityCreator) {
					GraphMember member = (GraphMember) ((SendableEntityCreator) evt).getValue(evt,
							TemplateResultFragment.PROPERTY_CURRENTMEMBER);
					if (member != null) {
						ObjectCondition oc = GraphUtil.getRole(member);
						if (oc != null) {
							oc.update(notifyBuffer);
						}
					}
				}
				return success;
			}
		}
		return false;
	}

	@Override
	public String[] getProperties() {
		return new String[] { EXPRESSION, TRUECONDITION, FALSECONDITION };
	}

	@Override
	public ParserCondition getSendableInstance(boolean prototyp) {
		return new IfCondition().withKey(this.tag);
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
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (ParserCondition.NOTIFY.equalsIgnoreCase(attribute)) {
			notifyBuffer.withObjects(value);
			return true;
		}
		if (EXPRESSION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withExpression((ObjectCondition) value);
			return true;
		}
		if (TRUECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withTrue((ObjectCondition) value);
			return true;
		}
		if (FALSECONDITION.equalsIgnoreCase(attribute)) {
			((IfCondition) entity).withFalse((ObjectCondition) value);
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	@Override
	public String getKey() {
		return tag;
	}

	public IfCondition withKey(String key) {
		this.tag = key;
		return this;
	}

	/** Create Condition
	*  Switch for If IfNot
	*  {{#if {{Variable}}}}
	*  {{#if Variable}}
	*  {{#if {{#feature}}}}
	*/
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		if(buffer == null) {
			return;
		}
		buffer.skip();
		ObjectCondition expression = parser.parsing(buffer, customTemplate, true, true, "?");

		if (this.tag.equalsIgnoreCase("ifnot")) {
			this.withExpression(Not.create(expression));
		} else {
			this.withExpression(expression);
		}
		if (buffer.skipIf(true, '?')) {
			/* Short IF */
			this.withTrue(parser.parsing(buffer, customTemplate, false, true, ":", "}"));
			if (buffer.skipIf(true, ':')) {
				this.withFalse(parser.parsing(buffer, customTemplate, false, true, "}"));
			}
			buffer.skipChar(SPLITEND);
			buffer.skipChar(SPLITEND);
			return;
		}
		/* CHECK ## */
		if (buffer.skipIf(false, '#', '#')) {
			this.notifyBuffer = new CharacterBuffer();
		}
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);

		/* Add Children */
		this.withTrue(parser.parsing(buffer, customTemplate, false, true, "else", "endif"));

		/* ELSE OR ENDIF */
		CharacterBuffer tokenPart = buffer.nextToken(false, SPLITEND);
		if ("else".equalsIgnoreCase(tokenPart.toString())) {
			buffer.skipChar(SPLITEND);
			buffer.skipChar(SPLITEND);
			this.withFalse(parser.parsing(buffer, customTemplate, false, true, "endif"));
			buffer.skipTo(SPLITEND, false);
		}
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String toString() {
		return "{{IF " + expression + "}}" + trueCondition + "{{#else}}" + falseCondition + "{{#ENDIF}}";
	}
}
