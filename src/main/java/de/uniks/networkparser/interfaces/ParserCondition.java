package de.uniks.networkparser.interfaces;

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

// TODO: Auto-generated Javadoc
/**
 * The Interface ParserCondition.
 * @author Stefan
 */
public interface ParserCondition extends ObjectCondition {
	
	/** The Constant SPLITEND. */
	public static final char SPLITEND = '}';
	
	/** The Constant SPLITSTART. */
	public static final char SPLITSTART = '{';
	
	/** The Constant ENTER. */
	public static final char ENTER = '=';
	
	/** The Constant SPACE. */
	public static final char SPACE = ' ';
	
	/** The Constant NOTIFY. */
	public static final String NOTIFY = "notify";

	/**
	 * Gets the value.
	 *
	 * @param variables the variables
	 * @return the value
	 */
	Object getValue(LocalisationInterface variables);

	/**
	 * Creates the.
	 *
	 * @param buffer the buffer
	 * @param parser the parser
	 * @param customTemplate the custom template
	 */
	void create(CharacterBuffer buffer, TemplateParser parser, LocalisationInterface customTemplate);

	/**
	 * Checks if is expression.
	 *
	 * @return true, if is expression
	 */
	boolean isExpression();

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	String getKey();

	/**
	 * Gets the sendable instance.
	 *
	 * @param isExpression the is expression
	 * @return the sendable instance
	 */
	Object getSendableInstance(boolean isExpression);
}
