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

public class HTMLRegExRule extends RegExRule{
	/**
	   * Common HTML script RegExp.
	   */
	  public static final HTMLRegExRule phpScriptTags = new HTMLRegExRule("(?:&lt;|<)\\?=?", "\\?(?:&gt;|>)");
	  /**
	   * Common HTML script RegExp.
	   */
	  public static final HTMLRegExRule aspScriptTags = new HTMLRegExRule("(?:&lt;|<)%=?", "%(?:&gt;|>)");
	  /**
	   * Common HTML script RegExp.
	   */
	  public static final HTMLRegExRule scriptScriptTags = new HTMLRegExRule("(?:&lt;|<)\\s*script.*?(?:&gt;|>)", "(?:&lt;|<)\\/\\s*script\\s*(?:&gt;|>)");
	  /**
	   * The regular expression of the left tag.
	   */
	  protected String left;
	  /**
	   * The regular expression of the right tag.
	   */
	  protected String right;

	  /**
	   * Constructor.
	   * @param left the regular expression of the left tag, cannot be null
	   * @param right the regular expression of the right tag, cannot be null
	   */
	  public HTMLRegExRule(String left, String right) {
	    this.left = left;
	    this.right = right;
	  }

	  /**
	   * Get the pattern of this HTML script RegExp.
	   * It is a combination of left and right tag and some pattern to match the 
	   * in-between content. Group 1 is the left tag, group 2 is the inner content, 
	   * group 3 is the right tag.
	   * 
	   * @return the pattern with flags: CASE_INSENSITIVE and DOTALL
	   */
	  @Override
	public Pattern getPattern() {
	    return Pattern.compile("(" + left + ")(.*?)(" + right + ")", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	  }
	  
	  /**
	   * {@inheritDoc}
	   */
	  @Override
	  public String toString() {
	    StringBuilder sb = new StringBuilder();

	    sb.append(getClass().getName());
	    sb.append(":[");
	    sb.append("left: ");
	    sb.append(left);
	    sb.append("right: ");
	    sb.append(right);
	    sb.append("]");

	    return sb.toString();
	  }
}
