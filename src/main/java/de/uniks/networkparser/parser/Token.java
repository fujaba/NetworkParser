package de.uniks.networkparser.parser;

import de.uniks.networkparser.buffer.CharacterBuffer;

/*
NetworkParser
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

/**
 * The Class Token.
 *
 * @author Stefan
 */
public class Token {
	
	/**  Unknown. */
	public static final char UNKNOWN = '?';
	
	/**  Attribute. */
	public static final char ATTR = 'a';
	
	/**  Definition is. */
	public static final char DEFINITION = '=';
	
	/**  AttributeName. */
	public static final char ATTRNAME = 'k';
	
	/**  Ignore. */
	public static final char IGNORE = 'i';
	
	/**  Nomen. */
	public static final char NOMEN = 'n';
	
	/** The Constant POINT. */
	/* Point */
	public static final char POINT = '.';
	
	/** The Constant ATTRVALUE. */
	/* AttributeValue */
	public static final char ATTRVALUE = '0';
	
	/** The Constant ATTRTYPE. */
	/* AttributeValue */
	public static final char ATTRTYPE = '1';
	
	/** The Constant AND. */
	/* And */
	public static final char AND = '&'; 
	
	/** The Constant VERB. */
	/* Verb */
	public static final char VERB = 'v';
	
	/** The Constant VALUE. */
	public static final char VALUE = 'V';
	
	/** The Constant NUMERIC. */
	public static final char NUMERIC = '9';

	/** The Constant EOF. */
	public static final char EOF = Character.MIN_VALUE;
	
	/** The Constant LONG_COMMENT_START. */
	public static final char LONG_COMMENT_START = 'C';
	
	/** The Constant LONG_COMMENT_END. */
	public static final char LONG_COMMENT_END = 'c';
	
	/** The Constant COMMENT. */
	public static final char COMMENT = 'L';
	
	/** The Constant NEWLINE. */
	public static final char NEWLINE = '\n';

	/** The kind. */
	public char kind;
	
	/** The start pos. */
	public int startPos;
	
	/** The end pos. */
	public int endPos;

	/** The text. */
	public CharacterBuffer text = new CharacterBuffer();
	
	/** The original text. */
	public CharacterBuffer originalText = new CharacterBuffer();
	
	/** The value. */
	public double value;

	/**
	 * Name.
	 *
	 * @return the string
	 */
	public String name() {
		return this.text.toString();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return kind + " " + name();
	}

	/**
	 * With text.
	 *
	 * @param string the string
	 * @return the token
	 */
	public Token withText(CharSequence string) {
		text.add(string);
		originalText.with(string);
		return this;
	}

	/**
	 * Adds the kind.
	 *
	 * @param kind the kind
	 * @return the token
	 */
	public Token addKind(char kind) {
		this.kind = kind;
		return this;
	}

	/**
	 * Adds the text.
	 *
	 * @param value the value
	 * @return the token
	 */
	public Token addText(char value) {
		if (!Character.isWhitespace(value)) {
			text.with(value);
		}
		originalText.with(value);
		return this;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		this.text.clear();
		this.originalText.clear();
	}

	/**
	 * Length.
	 *
	 * @return the int
	 */
	public int length() {
		return text.size();
	}
}
