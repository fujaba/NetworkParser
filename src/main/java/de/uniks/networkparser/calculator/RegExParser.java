package de.uniks.networkparser.calculator;

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

public class RegExParser {
	public static Boolean regex(String str, String pattern) {
		if (str == null || str.isEmpty() || pattern == null || pattern.isEmpty()) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			if (match(str.substring(i), pattern)) {
				return true;
			}
		}

		return false;
	}

	private static Boolean match(String str, String pattern) {
		if (pattern == null || str == null) {
			return false;
		}
		if (pattern.length() == 2 && pattern.charAt(1) == '*') {
			return true;
		} else if (str.isEmpty() || pattern.isEmpty()) {
			return false;
		} else {
			if ((pattern.length() > 1 && pattern.charAt(1) == '*')) {
				int index = 0;
				while (index < str.length() && (pattern.charAt(0) == str.charAt(index) || pattern.charAt(0) == '.')) {
					if (match(str.substring(index + 1), pattern)) {
						return true;
					}
					index++;
				}
				return match(str, pattern.substring(2)) || pattern.length() == 2;
			} else if ((pattern.length() > 1 && pattern.charAt(1) == '+')) {
				int index = 0;
				boolean match = false;
				while (index < str.length() && (pattern.charAt(0) == str.charAt(index) || pattern.charAt(0) == '.')) {
					match = true;
					if (match(str.substring(index + 1), pattern)) {
						return true;
					}
					index++;
				}
				return match && match(str.substring(1), pattern.substring(2)) || match && pattern.length() == 2;

			} else if (pattern.charAt(0) == str.charAt(0) || pattern.charAt(0) == '.') {
				if (pattern.length() == 1) {
					return true;
				} else {
					return match(str.substring(1), pattern.substring(1));
				}
			} else {
				return false;
			}
		}
	}
}
