package de.uniks.networkparser.parser;

import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;

/*
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

public class LoCMetric implements ObjectCondition {
	private int linesOfCode = 0;
	private int commentCount = 0;
	private int methodheader = 0;
	private int emptyLine = 0;
	private int annotation = 0;

	public void finish(Method item) {
		String[] lines = item.getBody().split(BaseItem.CRLF);
		for (String line : lines) {
			String simple = line.trim();
			if (simple.length() < 1) {
				emptyLine++;
				continue;
			}
			if (simple.indexOf("/*") >= 0 || simple.indexOf("*/") >= 0 || simple.indexOf("//") >= 0
					|| simple.startsWith("*")) {
				commentCount++;
				continue;
			}
			if ("{}".indexOf(simple) >= 0) {
				methodheader++;
				continue;
			}
			if (simple.startsWith("@")) {
				annotation++;
				continue;
			}
			linesOfCode++;
		}
	}

	public int getLinesOfCode() {
		return linesOfCode;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public int getMethodheader() {
		return methodheader;
	}

	public int getEmptyLine() {
		return emptyLine;
	}

	public int getAnnotation() {
		return annotation;
	}

	@Override
	public String toString() {
		return "Line of File:" + getFullLines() + " - Lines of Code:" + linesOfCode;
	}

	public int getFullLines() {
		return (linesOfCode + commentCount + methodheader + emptyLine + annotation);
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof Method) {
			finish((Method) value);
			return true;
		}
		return false;
	}
}
