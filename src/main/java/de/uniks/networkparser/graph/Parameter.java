package de.uniks.networkparser.graph;
/*
NetworkParser
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

public class Parameter extends Value {
	private boolean isArray;
	Parameter() {

	}

	public Parameter(DataType type) {
		with(type);
	}

	public Parameter(Clazz value) {
		with(DataType.create(value));
	}

	@Override
	public Parameter withValue(String value) {
		super.withValue(value);
		return this;
	}

	public Method getMethod() {
		return (Method) this.parentNode;
	}

	// Redirect
	@Override
	public Parameter with(String string) {
		super.with(string);
		return this;
	}

	@Override
	public Parameter with(DataType value) {
		super.with(value);
		return this;
	}

	public Parameter withParent(Method value) {
		super.setParentNode(value);
		return this;
	}
	public static Parameter create(Object param) {
		if(param == null) {
			return null;
		}
		if(param instanceof DataType) {
			return new Parameter((DataType)param);
		}
		if(param instanceof Clazz) {
			return new Parameter((Clazz)param);
		}
		if(param instanceof String) {
			String value = (String)param;
			if(value.endsWith("...")) {
				Parameter newParam = new Parameter(DataType.create(value.substring(0, value.length() - 3)));
				newParam.isArray = true;
				return newParam;
			}
			return new Parameter(DataType.create(value));
		}
		return null;
	}

	public boolean isArray() {
		return isArray;
	}
}
