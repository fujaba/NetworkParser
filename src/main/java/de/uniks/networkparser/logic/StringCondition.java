package de.uniks.networkparser.logic;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.graph.GraphMember;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class StringCondition.
 *
 * @author Stefan
 */
public class StringCondition implements ParserCondition {
	
	/** The Constant EQUALS. */
	public static final String EQUALS = "equals";
	
	/** The Constant EQUALSIGNORECASE. */
	public static final String EQUALSIGNORECASE = "equalsignoreCase";
	
	/** The Constant CONTAINS. */
	public static final String CONTAINS = "contains";

	private String attribute;
	private String type;
	private CharSequence value;

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (type != null) {
			Object itemValue = null;
			if (value instanceof GraphMember) {
				itemValue = ((GraphMember) value).getValue(attribute);
			}
			if (value == null) {
				return itemValue == null;
			}
			if (itemValue instanceof String) {
				if (this.type == EQUALS) {
					return itemValue.equals(this.value);
				} else if (this.type == EQUALSIGNORECASE) {
					return ((String) itemValue).equalsIgnoreCase(""+this.value);
				} else if (this.type == CONTAINS) {
					return ((String) itemValue).contains("" + this.value);
				}
			}
			return false;
		}
		if (value instanceof ObjectCondition) {
			return ((ObjectCondition) value).update(this);
		}
		return this.value != null;
	}

	/**
	 * Creates the logic.
	 *
	 * @param sequence the sequence
	 * @return the object condition
	 */
	public static final ObjectCondition createLogic(String sequence) {
		if (sequence.startsWith("-")) {
			return new Not().with(new StringCondition().withValue(sequence.substring(1)));
		}
		return new StringCondition().withValue(sequence);
	}

	/**
	 * Method for generate Search Logic "A B" = A or B "(A B) = A and B -A = Not A
	 * #Field Field for Searchign.
	 *
	 * @param sequence of String
	 * @return ObjectCondition
	 */
	public static final ObjectCondition createSearchLogic(CharacterBuffer sequence) {
		return createSearchIntern(sequence, new Or());
	}

	/**
	 * Creates the search intern.
	 *
	 * @param sequence the sequence
	 * @param container the container
	 * @return the object condition
	 */
	public static final ObjectCondition createSearchIntern(CharacterBuffer sequence, ListCondition container) {
		if (sequence == null) {
			return null;
		}
		sequence.withPosition(0);
		SimpleList<ObjectCondition> conditionList = new SimpleList<ObjectCondition>();
		int start = 0;
		char item = sequence.getChar();
		while (sequence.isEnd() == false) {
			if (item == '(') {
				/* Sub Sequence */
				conditionList.add(createSearchIntern(sequence, new And()));
				item = sequence.getChar();
				continue;
			}
			if (item == ')' && container != null) {
				/* End SubSequence */
				break;
			}
			if (item == ' ') {
				int pos = sequence.position();
				conditionList.add(createLogic(sequence.substring(start, pos)));
				start = pos + 1;
				item = sequence.getChar();
				continue;
			}
			/* Check for Equals */
			if (item == '#' && start == sequence.position()) {
				int pos = sequence.indexOf(':', start);
				if (pos > 1) {
					String propString = sequence.substring(start + 1, pos);
					int end = sequence.indexOf(' ', pos);
					String value = sequence.substring(pos + 1, end);
					Equals equals2 = new Equals();
					equals2.withLeft(VariableCondition.create(propString, true));
					equals2.withRight(createLogic(value));
					conditionList.add(equals2);

					sequence.withPosition(end);
					start = end + 1;
					item = sequence.getChar();
				}
			}
			if (item == '"' || item == '\'') {
				item = sequence.getChar();
				while (sequence.isEnd() == false) {
					if (item == '"' || item == '\'') {
						break;
					}
					item = sequence.getChar();
				}
				int pos = sequence.position();
				conditionList.add(createLogic(sequence.substring(start, pos)));
				start = pos + 1;
			}
			item = sequence.getChar();
		}
		if (start < sequence.length()) {
			conditionList.add(createLogic(sequence.substring(start)));
		}
		if (conditionList.size() < 1) {
			return null;
		}
		if (conditionList.size() < 2) {
			return conditionList.first();
		}
		if (container == null) {
			return null;
		}
		for (int i = 0; i < conditionList.size(); i++) {
			container.add(conditionList.get(i));
		}
		return container;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the string condition
	 */
	public StringCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	public CharSequence getValue(LocalisationInterface variables) {
		return value;
	}

	/**
	 * Creates the.
	 *
	 * @param sequence the sequence
	 * @return the string condition
	 */
	public static StringCondition create(CharSequence sequence) {
		return new StringCondition().withValue(sequence);
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
		this.value = buffer;
	}

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		if (value == null) {
			return false;
		}
		CharacterBuffer item = CharacterBuffer.create(value);
		return item.equalsIgnoreCase("true");
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
		if (attribute != null) {
			return attribute;

		}
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public StringCondition getSendableInstance(boolean prototyp) {
		return new StringCondition();
	}

	/**
	 * With filter.
	 *
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return the string condition
	 */
	public StringCondition withFilter(String attribute, String value, String type) {
		this.value = value;
		this.attribute = attribute;
		this.type = type;
		return this;
	}

	/**
	 * Creates the equals.
	 *
	 * @param attribute the attribute
	 * @param value the value
	 * @return the string condition
	 */
	public static StringCondition createEquals(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, EQUALS);
	}

	/**
	 * Creates the equals ignore case.
	 *
	 * @param attribute the attribute
	 * @param value the value
	 * @return the string condition
	 */
	public static StringCondition createEqualsIgnoreCase(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, EQUALSIGNORECASE);
	}

	/**
	 * Creates the contains.
	 *
	 * @param attribute the attribute
	 * @param value the value
	 * @return the string condition
	 */
	public static StringCondition createContains(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, CONTAINS);
	}

	/**
	 * Clone.
	 *
	 * @param otherValue the other value
	 * @return the string condition
	 */
	public StringCondition clone(String otherValue) {
		return new StringCondition().withFilter(attribute, otherValue, this.type);
	}

	/**
	 * Not.
	 *
	 * @param condition the condition
	 * @return the object condition
	 */
	public static ObjectCondition Not(ObjectCondition condition) {
		return Not.create(condition);
	}
}
