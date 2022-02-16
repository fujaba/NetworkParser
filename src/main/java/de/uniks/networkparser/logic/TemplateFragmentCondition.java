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
import de.uniks.networkparser.interfaces.LocalisationInterface;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.ParserCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.TemplateParser;
import de.uniks.networkparser.parser.TemplateResultFragment;

/**
 * TemplateFragmentCondition.
 * {{#template PACKAGE {{CONDITION}}}}{{#endtemplate}}
 *
 * @author Stefan
 */
public class TemplateFragmentCondition implements ParserCondition {
	
	/** The Constant PROPERTY_CLONE. */
	public static final String PROPERTY_CLONE = "clone";
	
	/** The Constant PROPERTY_FILE. */
	public static final String PROPERTY_FILE = "file";
	
	/** The Constant PROPERTY_KEY. */
	public static final String PROPERTY_KEY = "key";
	
	/** The Constant PROPERTY_TEMPLATE. */
	public static final String PROPERTY_TEMPLATE = "template";
	
	/** The Constant TAG. */
	public static final String TAG = "template";

	private String id;
	private ObjectCondition condition;
	private ObjectCondition child;

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	@Override
	public boolean isExpression() {
		return true;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return TAG;
	}

	/**
	 * Gets the id key.
	 *
	 * @param id the id
	 * @return the id key
	 */
	public static int getIdKey(String id) {
		if ("PACKAGE".equalsIgnoreCase(id)) {
			return TemplateParser.PACKAGE;
		}
		if ("IMPORT".equalsIgnoreCase(id)) {
			return TemplateParser.IMPORT;
		}
		if ("TEMPLATE".equalsIgnoreCase(id)) {
			return TemplateParser.TEMPLATE;
		}
		if ("FIELD".equalsIgnoreCase(id)) {
			return TemplateParser.FIELD;
		}
		if ("VALUE".equalsIgnoreCase(id)) {
			return TemplateParser.VALUE;
		}
		if ("METHOD".equalsIgnoreCase(id)) {
			return TemplateParser.METHOD;
		}
		if ("TEMPLATEEND".equalsIgnoreCase(id)) {
			return TemplateParser.TEMPLATEEND;
		}
		return TemplateParser.DECLARATION;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (value instanceof SendableEntityCreator) {
			if (condition != null && !condition.update(value)) {
				return false;
			}
			SendableEntityCreator creator = (SendableEntityCreator) value;
			/* VODOO */
			SendableEntityCreator newInstance = (SendableEntityCreator) creator.getValue(creator, PROPERTY_CLONE);
			newInstance.setValue(newInstance, PROPERTY_KEY, TemplateFragmentCondition.getIdKey(id),
					SendableEntityCreator.NEW);
			newInstance.setValue(newInstance, PROPERTY_TEMPLATE, this.child, SendableEntityCreator.NEW);

			newInstance.setValue(newInstance, PROPERTY_FILE, creator.getValue(creator, PROPERTY_FILE),
					SendableEntityCreator.NEW);

			this.child.update(newInstance);
			newInstance.setValue(newInstance, TemplateResultFragment.FINISH_GENERATE, newInstance,
					SendableEntityCreator.NEW);
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	@Override
	public Object getValue(LocalisationInterface variables) {
		return null;
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
		CharacterBuffer id = buffer.nextToken(false, SPLITEND, SPACE);
		this.id = id.toString();
		buffer.nextClean(true);
		if (buffer.getCurrentChar() != SPLITEND) {
			/* Condition */
			this.condition = parser.parsing(buffer, customTemplate, true, true);
		}

		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
		this.child = parser.parsing(buffer, customTemplate, false, true, "endtemplate");
		/* Skip } */
		buffer.skip();

		buffer.skipTo(SPLITEND, true);
		buffer.skipChar(SPLITEND);
		buffer.skipChar(SPLITEND);
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public TemplateFragmentCondition getSendableInstance(boolean prototyp) {
		return new TemplateFragmentCondition();
	}
}
