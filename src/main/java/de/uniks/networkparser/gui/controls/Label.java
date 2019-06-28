package de.uniks.networkparser.gui.controls;

/*
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

public class Label extends Control {
	public static final String SPACER = "spacer";
	public static final String TITLE = "title";
	public static final String VALUE = "value";

	private String type;
	private String value;

	public Label() {
		this.addBaseElements(VALUE);
	}

	public String getType() {
		return type;
	}

	public Label withType(String type) {
		this.type = type;
		return this;
	}

	public Label withValue(String value) {
		this.value = value;
		return this;
	}

	public boolean setValue(String value) {
		if (value != this.value) {
			this.value = value;
			return true;
		}
		return false;
	}

	public String getValue() {
		return this.value;
	}

	public int length() {
		if (this.value == null) {
			return 0;
		}
		return this.value.length();
	}

	@Override
	public Label newInstance() {
		return new Label();
	}
}
