package de.uniks.networkparser.logic;

import java.util.Set;

/*
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
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Import;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

/**
 * The Class VariableCondition.
 *
 * @author Stefan
 */
public class VariableCondition implements ParserCondition {
	private CharSequence value;
	private boolean expression;
	private boolean defaultStringValue;

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value instanceof ObjectCondition) {
			return ((ObjectCondition) value).update(this);
		}
		if (value instanceof LocalisationInterface) {
			LocalisationInterface variables = (LocalisationInterface) value;
			Object object = getValue(variables);
			return object != null && !object.equals("");
		}
		if (this.value == null) {
			return value == null;
		}
		return this.value.equals(value);
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the variable condition
	 */
	public VariableCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}
	
	/**
	 *  GetValue
	 * key = Variable
	 * value = String
	 * variable = string
	 * Variable = String
	 * VARIABLE = STRING
	 * vAriable = nix
	 * Variable#Function.
	 *
	 * @param value text Dictionary
	 * @return The EvaluationValue
	 */
	public Object getValue(LocalisationInterface value) {
		if (value instanceof SendableEntityCreator) {
			SendableEntityCreator variables = (SendableEntityCreator) value;
			String key = this.value.toString();
			/* SWITCH FOR # */
			int pos = key.indexOf('#');
			String v = null;
			String format = null;
			boolean shortName = true;
			if (pos >= 0) {
				v = key.substring(0, pos);
				format = key.substring(pos + 1);
			} else {
				v = key;
			}
			pos = v.indexOf("(");
			String param = null;
			if (pos > 0) {
				param = v.substring(pos + 1, v.length() - 1);
				if(param.startsWith(")")) {
					shortName = false;
					param = v.substring(pos + 2, v.length());
				}else {
					shortName = Boolean.valueOf(param);
				}
				v = key.substring(0, pos);
			}
			Object object = variables.getValue(variables, v);

			if (object == null && (!this.expression || defaultStringValue)) {
				this.expression = false;
				return key;
			}
			if (object instanceof DataType) {
				object = ((DataType) object).getName(shortName);
			}
			if (object instanceof Annotation) {
				/* Check for Scope */
				Annotation anno = (Annotation) object;
				CharacterBuffer buffer = new CharacterBuffer();
				addAnnotation(anno, buffer, param, variables);
				while (anno.hasNext()) {
					anno = anno.next();
					if (buffer.length() > 0) {
						buffer.with(BaseItem.CRLF);
					}
					addAnnotation(anno, buffer, param, variables);
				}
				return buffer.toString();
			}
			if (object instanceof String) {
				return replaceText(v, format, (String) object);
			}
			if(object instanceof Integer && param != null) {
				Integer intValue = (Integer) object;
				if(param.startsWith(">")) {
					return intValue > Integer.parseInt(param.substring(1));
				}else if(param.startsWith("<")) {
					return intValue < Integer.parseInt(param.substring(1));
				}
			}
			if (object instanceof Set<?> && format != null) {
				/* Check for Contains */
				Set<?> items = (Set<?>) object;
				int endPos = format.indexOf(")");
				String temp = null;
				if (format.startsWith("contains(") && endPos > 0) {
					temp = format.substring(9, endPos);
				}
				if (temp != null) {
					for (Object child : items) {
						if (child instanceof GraphMember) {
							GraphMember member = (GraphMember) child;
							if (temp.equalsIgnoreCase(member.getName())) {
								return true;
							}
						}
					}
					return null;
				}
			}
			return object;
		}
		if (value != null && this.value != null) {
			return value.getText(this.value, null, null);
		}
		if (this.value == null) {
			return null;
		}
		return null;
	}

	/**
	 * Adds the annotation.
	 *
	 * @param anno the anno
	 * @param buffer the buffer
	 * @param param the param
	 * @param variables the variables
	 */
	public void addAnnotation(Annotation anno, CharacterBuffer buffer, String param, SendableEntityCreator variables) {
		if (anno == null || buffer == null) {
			return;
		}
		if (param == null && anno.getScope() != null) {
			return;
		}
		if (param != null && !param.equalsIgnoreCase(anno.getScope())) {
			return;
		}
		GraphSimpleSet children = GraphUtil.getChildren(anno);
		if (children != null) {
			for (Object item : children) {
				if (item instanceof Import) {
					variables.setValue(variables, "headers", ((Import) item).getClazz().getName(), "new");
				}
			}
		}
		buffer.with('@').with(anno.toString());
	}

	/**
	 * Replace text.
	 *
	 * @param name the name
	 * @param format the format
	 * @param value the value
	 * @return the string
	 */
	public String replaceText(String name, String format, String value) {
		if (name == null) {
			return name;
		}
		boolean upper = false;
		boolean firstUpper = false;
		boolean small = false;
		int startIndex;
		int i;
		/* other.NAME */
		startIndex = name.lastIndexOf('.');
		/* startIndex is last '.' therefore next proper index is startIndex + 1 */
		startIndex++;
		for (i = startIndex; i < name.length(); i++) {
			if (name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {
				upper = true;

				firstUpper = startIndex == i;
			} else if (name.charAt(i) >= 'a' && name.charAt(i) <= 'z') {
				small = true;
			}
		}
		if ("tolower".equalsIgnoreCase(format)) {
			return value.toLowerCase();
		}
		if ((!small && upper) || "toupper".equalsIgnoreCase(format)) {
			return value.toUpperCase();
		}
		if (firstUpper || "firstUpper".equalsIgnoreCase(format)) {
			return StringUtil.upFirstChar(value);
		}
		if (format == null) {
			return value;
		}
		if (format.startsWith("sub(")) {
			String substring = format.substring(4, format.length() - 1);
			String[] item = substring.split(",");
			int start = 0;
			int end = value.length() - 1;
			if (item.length > 0) {
				start = Integer.parseInt(item[0].trim());
			}
			if (item.length > 1) {
				int temp = Integer.parseInt(item[1].trim());
				;
				if (temp < end) {
					end = temp;
				}
			}
			return value.substring(start, end);
		}
		if (format.startsWith("contains(")) {
			String substring = format.substring(9, format.length() - 1);
			boolean boolValue = value.indexOf(substring) >= 0;
			if (boolValue) {
				return "true";
			}
			return "";
		}
		return value;
	}

	/**
	 * With expression.
	 *
	 * @param value the value
	 * @return the variable condition
	 */
	public VariableCondition withExpression(boolean value) {
		this.expression = value;
		return this;
	}

	/**
	 * Creates the.
	 *
	 * @param sequence the sequence
	 * @param expression the expression
	 * @return the variable condition
	 */
	public static VariableCondition create(CharSequence sequence, boolean expression) {
		return new VariableCondition().withValue(sequence).withExpression(expression);
	}

	/**
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		this.value = buffer.nextToken(true, ' ', '}');
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		return expression;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return null;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "{{" + this.value + "}}";
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public VariableCondition getSendableInstance(boolean prototyp) {
		return new VariableCondition();
	}

	/**
	 * With default string value.
	 *
	 * @param value the value
	 * @return the variable condition
	 */
	public VariableCondition withDefaultStringValue(boolean value) {
		this.defaultStringValue = value;
		return this;
	}
}
