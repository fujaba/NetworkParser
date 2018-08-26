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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.parser.TemplateResultFragment;

// {{#template PACKAGE {{CONDITION}}}}{{#endtemplate}}
	public class TemplateFragmentCondition implements ParserCondition{
	public static final String PROPERTY_CLONE="clone";
	public static final String PROPERTY_FILE="file";
	public static final String PROPERTY_KEY="key";
	public static final String PROPERTY_TEMPLATE="template";
	public static final String TAG="template";

	private String id;
	private ObjectCondition condition;
	private ObjectCondition child;

	@Override
	public boolean isExpression() {
		return true;
	}

	@Override
	public String getKey() {
		return TAG;
	}

	public static int getIdKey(String id) {
		if("PACKAGE".equalsIgnoreCase(id)) {
			return TemplateParser.PACKAGE;
		}
		if("IMPORT".equalsIgnoreCase(id)) {
			return TemplateParser.IMPORT;
		}
		if("TEMPLATE".equalsIgnoreCase(id)) {
			return TemplateParser.TEMPLATE;
		}
		if("FIELD".equalsIgnoreCase(id)) {
			return TemplateParser.FIELD;
		}
		if("VALUE".equalsIgnoreCase(id)) {
			return TemplateParser.VALUE;
		}
		if("METHOD".equalsIgnoreCase(id)) {
			return TemplateParser.METHOD;
		}
		if("TEMPLATEEND".equalsIgnoreCase(id)) {
			return TemplateParser.TEMPLATEEND;
		}
		return TemplateParser.DECLARATION;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SendableEntityCreator) {
			if(condition != null) {
				if(condition.update(value) == false) {
					return false;
				}
			}
			SendableEntityCreator creator = (SendableEntityCreator) value;
			// VODOO
			SendableEntityCreator newInstance = (SendableEntityCreator) creator.getValue(creator, PROPERTY_CLONE);
			newInstance.setValue(newInstance, PROPERTY_KEY, TemplateFragmentCondition.getIdKey(id), SendableEntityCreator.NEW);
			newInstance.setValue(newInstance, PROPERTY_TEMPLATE, this.child, SendableEntityCreator.NEW);

			newInstance.setValue(newInstance, PROPERTY_FILE, creator.getValue(creator, PROPERTY_FILE), SendableEntityCreator.NEW);

			this.child.update(newInstance);
			newInstance.setValue(newInstance, TemplateResultFragment.FINISH_GENERATE, newInstance, SendableEntityCreator.NEW);
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(LocalisationInterface variables) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate) {
		CharacterBuffer id = buffer.nextToken(false, SPLITEND, SPACE);
		this.id = id.toString();
		buffer.nextClean(true);
		if(buffer.getCurrentChar() != SPLITEND) {
			// Condition
			this.condition = parser.parsing(buffer, customTemplate, true, true);
		}

		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
		this.child = parser.parsing(buffer, customTemplate, false, true, "endtemplate");
		//Skip }
		buffer.skip();

		buffer.skipTo(SPLITEND, true);
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
	}

	public String getId() {
		return id;
	}

	@Override
	public TemplateFragmentCondition getSendableInstance(boolean prototyp) {
		return new TemplateFragmentCondition();
	}
}
