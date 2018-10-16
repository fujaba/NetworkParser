package de.uniks.networkparser.logic;

/*
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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.list.SimpleList;

public class StringCondition implements ParserCondition {
	private CharSequence value;

	@Override
	public boolean update(Object value) {
		if (value instanceof ObjectCondition) {
			return ((ObjectCondition) value).update(this);
		}
		return this.value != null;
	}

	public static final ObjectCondition createLogic(String sequence) {
		if(sequence.startsWith("-")) {
			return new Not().with(new StringCondition().withValue(sequence.substring(1)));
		}
		return new StringCondition().withValue(sequence);
	}
	public static final ObjectCondition createSearchLogic(CharacterBuffer sequence) {
		SimpleList<String> stringList = new SimpleList<String>();
		int start=0;
		for(int i=0;i<sequence.length();i++) {
			if(sequence.charAt(i)==' ') {
				stringList.add(sequence.substring(start, i));
				start=i+1;
				continue;
			}
			if(sequence.charAt(i)=='"' || sequence.charAt(i)=='\'') {
				i++;
				while(i<sequence.length()) {
					if(sequence.charAt(i)=='"' || sequence.charAt(i)=='\'') {
						i++;
						break;
					}
					i++;
				}
				stringList.add(sequence.substring(start, i));
				start=i+1;
			}
		}
		if(stringList.size()<1) {
			return null;
		}
		if(stringList.size()<2) {
			return createLogic(stringList.first());
		}
		Or or=new Or();
		for (int i=0;i<stringList.size();i++){
			or.add(createLogic(stringList.get(i)));
		}
		return or;
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
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	@Override
	public StringCondition getSendableInstance(boolean prototyp) {
		return new StringCondition();
	}
}
