package de.uniks.networkparser.gui.brush;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.util.regex.Pattern;
import de.uniks.networkparser.gui.Style;

public class RegExpRule extends RegExRule {
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern multiLineCComments = Pattern.compile(
			"\\/\\*[\\s\\S]*?\\*\\/", Pattern.MULTILINE);
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern singleLineCComments = Pattern.compile(
			"\\/\\/.*$", Pattern.MULTILINE);
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern singleLinePerlComments = Pattern.compile(
			"#.*$", Pattern.MULTILINE);
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern doubleQuotedString = Pattern
			.compile("\"([^\\\\\"\\n]|\\\\.)*\"");
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern singleQuotedString = Pattern
			.compile("'([^\\\\'\\n]|\\\\.)*'");
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern multiLineDoubleQuotedString = Pattern.compile(
			"\"([^\\\\\"]|\\\\.)*\"", Pattern.DOTALL);
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern multiLineSingleQuotedString = Pattern.compile(
			"'([^\\\\']|\\\\.)*'", Pattern.DOTALL);
	/**
	 * Common regular expression rule.
	 */
	public static final Pattern xmlComments = Pattern
			.compile("\\w+:\\/\\/[\\w-.\\/?%&=:@;]*");

	/**
	 * Constructor.
	 * 
	 * @param regExp
	 *            the regular expression for this rule
	 * @param styleKey
	 *            the style key, the style to apply to the matched result
	 */
	public RegExpRule(String regExp, String styleKey) {
		this.pattern = Pattern.compile(regExp, 0);
		setStyleKey(styleKey);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param regExp
	 *            the regular expression for this rule
	 * @param styleKey
	 *            the style key, the style to apply to the matched result
	 */
	public RegExpRule(Pattern regExp, String styleKey) {
		this.pattern = regExp;
		setStyleKey(styleKey);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param regExp
	 *            the regular expression for this rule
	 * @param regFlags
	 *            the flags for the regular expression, see the flags in
	 *            {@link java.util.regex.Pattern}
	 * @param styleKey
	 *            the style key, the style to apply to the matched result
	 */
	public RegExpRule(String regExp, int regFlags) {
		this.pattern = Pattern.compile(regExp, regFlags);
	}

	/**
	 * Constructor.
	 * 
	 * @param regExp
	 *            the regular expression for this rule
	 * @param regFlags
	 *            the flags for the regular expression, see the flags in
	 *            {@link java.util.regex.Pattern}
	 * @param styleKey
	 *            the style key, the style to apply to the matched result
	 */
	public RegExpRule(String regExp, int regFlags, String styleKey) {
		this.pattern = Pattern.compile(regExp, regFlags);
		setStyleKey(styleKey);
	}
	
	  /**
	   * Constructor.
	   * @param regExp the regular expression for this rule
	   * @param styleKey the style key, the style to apply to the matched result
	   */
	public RegExpRule(String regExp, String styleKey, Style style) {
		this.pattern = Pattern.compile(regExp, 0);
		this.style = style;
		setStyleKey(styleKey);
	}

	/**
	 * Constructor.
	 * 
	 * @param regExp
	 *            the regular expression for this rule
	 * @param regFlags
	 *            the flags for the regular expression, see the flags in
	 *            {@link java.util.regex.Pattern}
	 * @param styleKey
	 *            the style key, the style to apply to the matched result
	 */
	public RegExpRule(String regExp, int regFlags, String styleKey, Style style) {
		this.pattern = Pattern.compile(regExp, regFlags);
		this.style = style;
		setStyleKey(styleKey);
	}

}
