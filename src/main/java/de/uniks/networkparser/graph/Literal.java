package de.uniks.networkparser.graph;

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
import de.uniks.networkparser.list.SimpleList;

public class Literal extends GraphMember {
	public static final String PROPERTY_VALUE = "value";
	private SimpleList<Object> values;

	public Literal(String name) {
		super.with(name);
	}

	@Override
	public Literal with(String name) {
		super.with(name);
		return this;
	}

	public Literal withValue(Object... values) {
		if (values == null) {
			return this;
		}
		for (Object value : values) {
			if (value != null) {
				if (this.values == null) {
					this.values = new SimpleList<Object>();
				}
				this.values.add(value);
			}
		}
		return this;
	}

	public Object getValue(String attribute) {
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attrName)) {
			if (pos > 0) {
				if (this.values == null) {
					return 0;
				}
				return this.values.getValue(attribute.substring(pos + 1));
			}
			return this.values;
		}
		return super.getValue(attribute);
	}

	public SimpleList<Object> getValues() {
		return values;
	}
}
