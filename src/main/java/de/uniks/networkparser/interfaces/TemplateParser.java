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
 * The Interface TemplateParser.
 * @author Stefan
 */
public interface TemplateParser {
	
	/** The Constant DECLARATION. */
	public static final int DECLARATION = 0;

	/** The Constant PACKAGE. */
	public static final int PACKAGE = 1;

	/** The Constant IMPORT. */
	public static final int IMPORT = 2;

	/** The Constant TEMPLATE. */
	public static final int TEMPLATE = 3;

	/** The Constant FIELD. */
	public static final int FIELD = 4;

	/** The Constant VALUE. */
	public static final int VALUE = 5;

	/** The Constant METHOD. */
	public static final int METHOD = 6;

	/** The Constant TEMPLATEEND. */
	public static final int TEMPLATEEND = Integer.MAX_VALUE;
	
	/**
	 * Gets the last stop words.
	 *
	 * @return the last stop words
	 */
	String[] getLastStopWords();

	/**
	 * Parsing.
	 *
	 * @param template the template
	 * @param customTemplate the custom template
	 * @param isExpression the is expression
	 * @param allowSpace the allow space
	 * @param stopWords the stop words
	 * @return the object condition
	 */
	ObjectCondition parsing(CharacterBuffer template, LocalisationInterface customTemplate, boolean isExpression,
			boolean allowSpace, String... stopWords);
}
