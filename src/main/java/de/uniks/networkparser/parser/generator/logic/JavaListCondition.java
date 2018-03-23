package de.uniks.networkparser.parser.generator.logic;

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
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.BooleanList;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.StringList;
import de.uniks.networkparser.logic.CustomCondition;

public class JavaListCondition extends CustomCondition<Attribute>{
	public static final String TAG="listType";

	@Override
	public String getKey() {
		return TAG;
	}

	@Override
	public ParserCondition getSendableInstance(boolean isExpression) {
		return new JavaListCondition();
	}

	@Override
	public Object getValue(SendableEntityCreator creator, Attribute member) {
		String attributeType = member.getType().getName(true);
		if (attributeType.equals("boolean")) {
			return addImport(creator, BooleanList.class);
		} else if (" long Long short Short int Integer byte Byte float Float double Double ".indexOf(" " + attributeType + " ") >= 0) {
			return addImport(creator, NumberList.class);
		} else if (attributeType.equals("String")) {
			return addImport(creator, StringList.class);
		} else if (attributeType.equals("char")) {
			addImport(creator, SimpleList.class);
			return "SimpleList<Character>";
		} else {
			addImport(creator, SimpleList.class);
			return "SimpleList<" + attributeType + ">";
		}
	}
}
