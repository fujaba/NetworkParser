package de.uniks.networkparser.parser;

import de.uniks.networkparser.graph.Method;
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

public class McCabe implements ObjectCondition {
	private int mcCabe = 1;

	public void finish(Method item) {
		String allText = item.toString();
		allText = allText.toLowerCase();
		allText = allText.replaceAll("\n", "");
		allText = allText.replaceAll("\t", "");
		allText = allText.replaceAll(" ", "");
		allText = allText.replaceAll("<", "(");
		allText = allText.replaceAll(">", ")");
		evaluate(allText);
	}

	public void evaluate(String allText) {
		int index = 0;
		for (int i = 0; i < allText.length(); i++) {
			index = allText.indexOf("if(", index);
			if (index != -1) {
				if (checkQuotes(allText, index)) {
					mcCabe++;
					index += 1;
				} else
					index += 1;
			} else
				i = allText.length();

		}

		index = 0;
		for (int i = 0; i < allText.length(); i++) {
			index = allText.indexOf("do{", index);
			if (index != -1) {
				if (checkQuotes(allText, index)) {
					mcCabe++;
					index += 1;
				} else
					index += 1;
			} else
				i = allText.length();

		}

		index = 0;
		for (int i = 0; i < allText.length(); i++) {
			index = allText.indexOf("while (", index);
			if (index != -1) {
				if (checkQuotes(allText, index)) {

					mcCabe++;
					index += 1;
				} else
					index += 1;
			} else
				i = allText.length();

		}

		index = 0;
		for (int i = 0; i < allText.length(); i++) {
			index = allText.indexOf("&&", index);
			if (index != -1) {
				if (checkQuotes(allText, index)) {

					mcCabe++;
					index += 1;
				} else
					index += 1;
			} else
				i = allText.length();

		}
	}

	public boolean checkQuotes(String allText, int index) {
		int quote = 0;
		for (int i = 0; i < index; i++) {
			char nextChar = allText.charAt(i);
			if (nextChar == '\"')
				quote++;
		}

		if (quote % 2 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getMcCabe() {
		return mcCabe;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof String) {
			evaluate((String) value);
			return true;
		}
		return false;
	}
}
