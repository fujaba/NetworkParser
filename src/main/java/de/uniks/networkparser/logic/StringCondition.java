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

public class StringCondition implements ParserCondition {
	public static final String EQUALS = "equals";
	public static final String EQUALSIGNORECASE = "equalsignoreCase";
	public static final String CONTAINS = "contains";

	private String attribute;
	private String type;
	private CharSequence value;

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

	public static final ObjectCondition createLogic(String sequence) {
		if (sequence.startsWith("-")) {
			return new Not().with(new StringCondition().withValue(sequence.substring(1)));
		}
		return new StringCondition().withValue(sequence);
	}

	/**
	 * Method for generate Search Logic "A B" = A or B "(A B) = A and B -A = Not A
	 * #Field Field for Searchign
	 * 
	 * @param sequence of String
	 * @return ObjectCondition
	 */
	public static final ObjectCondition createSearchLogic(CharacterBuffer sequence) {
		return createSearchIntern(sequence, new Or());
	}

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

	public StringCondition withValue(CharSequence value) {
		this.value = value;
		return this;
	}

	public CharSequence getValue(LocalisationInterface variables) {
		return value;
	}

	public static StringCondition create(CharSequence sequence) {
		return new StringCondition().withValue(sequence);
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		this.value = buffer;
	}

	@Override
	public boolean isExpression() {
		if (value == null) {
			return false;
		}
		CharacterBuffer item = CharacterBuffer.create(value);
		return item.equalsIgnoreCase("true");
	}

	@Override
	public String getKey() {
		return null;
	}

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

	@Override
	public StringCondition getSendableInstance(boolean prototyp) {
		return new StringCondition();
	}

	public StringCondition withFilter(String attribute, String value, String type) {
		this.value = value;
		this.attribute = attribute;
		this.type = type;
		return this;
	}

	public static StringCondition createEquals(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, EQUALS);
	}

	public static StringCondition createEqualsIgnoreCase(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, EQUALSIGNORECASE);
	}

	public static StringCondition createContains(String attribute, String value) {
		return new StringCondition().withFilter(attribute, value, CONTAINS);
	}

	public StringCondition clone(String otherValue) {
		return new StringCondition().withFilter(attribute, otherValue, this.type);
	}

	public static ObjectCondition Not(ObjectCondition condition) {
		return Not.create(condition);
	}
}
