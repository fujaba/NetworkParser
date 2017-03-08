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
	public static final StringFilter<Parameter> NAME = new StringFilter<Parameter>(GraphMember.PROPERTY_NAME);
	public static final String PROPERTY_METHOD = "method";

	Parameter() {

	}

	public Parameter(DataType type) {
		with(type);
	}

	public Parameter(Clazz value) {
		with(value);
	}
	public Parameter(Clazz value, String name) {
		with(value);
		with(name);
	}

	public Parameter(Class<?> value) {
		with(value);
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

	@Override
	public Parameter with(Clazz value) {
		super.with(value);
		return this;
	}

	@Override
	public Parameter with(Class<?> value) {
		super.with(value);
		return this;
	}

	public Parameter withParent(Method value) {
		super.setParentNode(value);
		return this;
	}
}
