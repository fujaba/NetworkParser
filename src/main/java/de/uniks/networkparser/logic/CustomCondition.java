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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;

public abstract class CustomCondition<T> implements ParserCondition {
	protected static final String PROPERTY_MEMBER = "member";
	protected boolean isExpression;

	@Override
	public boolean isExpression() {
		return isExpression;
	}

	@SuppressWarnings("unchecked")
	public <ST extends CustomCondition<T>> ST withExpression(boolean value) {
		this.isExpression = value;
		return (ST) this;
	}

	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		skipEnd(buffer);
	}

	protected String addImport(Object value, Class<?> importName) {
		if (value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			creator.setValue(value, "headers", importName.getName(), SendableEntityCreator.NEW);
			return importName.getSimpleName();
		}
		return null;
	}

	protected boolean addImport(Object value, String importName) {
		if (value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			return creator.setValue(value, "headers", importName, SendableEntityCreator.NEW);
		}
		return false;
	}

	protected GraphMember getMember(Object value) {
		if (value instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) value;
			GraphMember member = (GraphMember) creator.getValue(creator, PROPERTY_MEMBER);
			return member;
		}
		return null;
	}

	protected void skipEnd(CharacterBuffer buffer) {
		buffer.skipTo(SPLITEND, true);
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof ObjectCondition) {
			return ((ObjectCondition) value).update(this);
		}
		if (value instanceof LocalisationInterface) {
			return getValue((LocalisationInterface) value) != null;
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getValue(LocalisationInterface variables) {
		if (variables instanceof SendableEntityCreator) {
			SendableEntityCreator creator = (SendableEntityCreator) variables;
			return getValue(creator, (T) getMember(variables));
		}
		return null;
	}

	public abstract Object getValue(SendableEntityCreator creator, T member);
}
