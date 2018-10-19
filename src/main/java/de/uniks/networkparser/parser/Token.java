package de.uniks.networkparser.parser;

import de.uniks.networkparser.buffer.CharacterBuffer;

/*
NetworkParser
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

public class Token {
	public static final char UNKNOWN='?';	// Unknown

	public static final char ATTR='a';		// Attribute
	public static final char DEFINITION='=';		// Definition is
	public static final char ATTRNAME='k';		// AttributeName
	public static final char IGNORE='i';		// Ignore
	public static final char NOMEN='n';			// Nomen
	public static final char POINT='.';			// Point
	public static final char ATTRVALUE='0';		// AttributeValue
	public static final char AND='&';			// And 
	public static final char VERB='v';			// Verb
	public static final char VALUE='V';
	public static final char NUMERIC='9'; 
	
	public static final char EOF = Character.MIN_VALUE;
	public static final char LONG_COMMENT_START = 'C';
	public static final char LONG_COMMENT_END = 'c';
	public static final char COMMENT = '/';
	public static final char NEWLINE = '\n';

	public char kind;
	public int startPos;
	public int endPos;

	public CharacterBuffer text = new CharacterBuffer();
	public CharacterBuffer originalText = new CharacterBuffer();

//FIXME	public int preCommentStartPos;
//	public int preCommentEndPos;

	// SDMLib Value
	public double value;

	public String name() {
		return this.text.toString();
	}

	@Override
	public String toString() {
		return kind + " " + name();
	}

	public Token withText(CharSequence string) {
		text.add(string);
		originalText.with(string);
		return this;
	}
	
	public Token addKind(char kind) {
		this.kind = kind;
		return this;
	}

	public Token addText(char value) {
		if (Character.isWhitespace(value) == false) {
			text.with(value);
		}
		originalText.with(value);
		return this;
	}

	public void clear() {
		this.text.clear();
		this.originalText.clear();
	}

	public int length() {
		return text.size();
	}
}
