package de.uniks.networkparser.parser;

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
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
/**
* Add so for Adding some Code for example getValue:Attribute or CreatorCreator
* @author Stefan Lindel
*/
public class MethodAddCondition implements ParserCondition, SendableEntityCreator {
	public static final String PROPERTY_CHILD = "child";
	protected ObjectCondition child;

	@Override
	public boolean update(Object value) {
		return false;
	}

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
	}

	@Override
	public boolean isExpression() {
		return false;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public Object getSendableInstance(boolean isExpression) {
		return null;
	}
}
