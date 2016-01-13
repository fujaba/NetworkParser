package de.uniks.networkparser.test.ant.ikvm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.tools.ant.BuildException;

public class Regex implements Filter {
	private Pattern suppressPattern;
	private Matcher suppressMatcher;

	public void addText (String filter) {
		try {
			suppressPattern = Pattern.compile(filter);
		} catch (PatternSyntaxException e) {
			throw new BuildException ("Error parsing output filter \"" + filter + "\":" + e.getMessage(), e);
		}
	}

	public boolean suppress (String output) {
		if (suppressMatcher == null)
			suppressMatcher = suppressPattern.matcher(output);
		else
			suppressMatcher.reset(output);

		return suppressMatcher.matches();
	}

}
