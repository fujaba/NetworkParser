package de.uniks.networkparser.xml;

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
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleList;

public class XMLContainer extends XMLEntity {
	private SimpleList<String> prefix = new SimpleList<String>();

	public XMLContainer withPrefix(String value) {
		this.prefix.add(value);
		return this;
	}

	public XMLContainer withStandardPrefix() {
		withPrefix("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String item : prefix) {
			sb.append(item);
			sb.append(BaseItem.CRLF);
		}
		sb.append(super.toString());
		return sb.toString();
	}

	@Override
	public String toString(int indentFactor) {
		StringBuilder sb = new StringBuilder();
		for (String item : prefix) {
			sb.append(item);
			sb.append(BaseItem.CRLF);
		}
		sb.append(super.toString(indentFactor));
		return sb.toString();
	}
}
