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
 * Java FX brush.
 *
 * @author Chan Wai Shing (cws1989@gmail.com)
 */
public class BrushJavaFX extends Brush {

	public BrushJavaFX() {
		super();

		// Contributed by Patrick Webster
		// http://patrickwebster.blogspot.com/2009/04/javafx-brush-for-syntaxhighlighter.html

		String datatypes = "Boolean Byte Character Double Duration "
				+ "Float Integer Long Number Short String Void";
		String keywords = "abstract after and as assert at before bind bound break catch class "
				+ "continue def delete else exclusive extends false finally first for from "
				+ "function if import in indexof init insert instanceof into inverse last "
				+ "lazy mixin mod nativearray new not null on or override package postinit "
				+ "protected public public-init public-read replace return reverse sizeof "
				+ "step super then this throw true try tween typeof var where while with "
				+ "attribute let private readonly static trigger";

		addRule(new RegExpressions(RegExpressions.singleLineCComments,
				"comments"));
		addRule(new RegExpressions(RegExpressions.multiLineCComments,
				"comments"));
		addRule(new RegExpressions(RegExpressions.singleQuotedString, "string"));
		addRule(new RegExpressions(RegExpressions.doubleQuotedString, "string"));
		addRule(new RegExpressions(
				"(-?\\.?)(\\b(\\d*\\.?\\d+|\\d+\\.?\\d*)(e[+-]?\\d+)?|0x[a-f\\d]+)\\b\\.?",
				Pattern.CASE_INSENSITIVE, "color2")); // numbers
		addRule(new RegExpressions(getKeywords(datatypes), Pattern.MULTILINE,
				"variable")); // datatypes
		addRule(new RegExpressions(getKeywords(keywords), Pattern.MULTILINE,
				"keyword"));

		addRule(HTMLRegExRule.aspScriptTags);

		setCommonFileExtensionList("fx");
	}
}
