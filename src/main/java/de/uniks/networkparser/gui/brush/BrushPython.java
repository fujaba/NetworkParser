package de.uniks.networkparser.gui.brush;

/*
 java-syntax-highlighter
 Copyright (c) 2011 Chan Wai Shing

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.regex.Pattern;

/**
 * Python brush.
 *
 * @author Chan Wai Shing (cws1989@gmail.com)
 */
public class BrushPython extends Brush {

	public BrushPython() {
		super();

		// Contributed by Gheorghe Milas and Ahmad Sherif

		String keywords = "and assert break class continue def del elif else "
				+ "except exec finally for from global if import in is "
				+ "lambda not or pass print raise return try yield while";
		String funcs = "__import__ abs all any apply basestring bin bool buffer callable "
				+ "chr classmethod cmp coerce compile complex delattr dict dir "
				+ "divmod enumerate eval execfile file filter float format frozenset "
				+ "getattr globals hasattr hash help hex id input int intern "
				+ "isinstance issubclass iter len list locals long map max min next "
				+ "object oct open ord pow print property range raw_input reduce "
				+ "reload repr reversed round set setattr slice sorted staticmethod "
				+ "str sum super tuple type type unichr unicode vars xrange zip";
		String special = "None True False self cls class_";

		addRule(new RegExpressions(RegExpressions.singleLinePerlComments,
				"comments"));
		addRule(new RegExpressions("^\\s*@\\w+ ", Pattern.MULTILINE, "color2"));
		addRule(new RegExpressions("(['\\\"]{3})([^['\\\"]{3}])*?['\\\"]{3}",
				Pattern.MULTILINE, "comments"));
		addRule(new RegExpressions("\"(?!\")(?:\\.|\\\\\\\"|[^\\\"\"\\n])*\"",
				Pattern.MULTILINE, "string"));
		addRule(new RegExpressions("'(?!')(?:\\.|(\\\\\\')|[^\\''\\n])*'",
				Pattern.MULTILINE, "string"));
		addRule(new RegExpressions("\\+|\\-|\\*|\\/|\\%|=|==",
				Pattern.MULTILINE, "keyword"));
		addRule(new RegExpressions("\\b\\d+\\.?\\w*", "value"));
		addRule(new RegExpressions(getKeywords(funcs), Pattern.MULTILINE
				| Pattern.CASE_INSENSITIVE, "functions"));
		addRule(new RegExpressions(getKeywords(keywords), Pattern.MULTILINE,
				"keyword"));
		addRule(new RegExpressions(getKeywords(special), Pattern.MULTILINE,
				"color1"));

		addRule(HTMLRegExRule.aspScriptTags);

		setCommonFileExtensionList("py");
	}
}
