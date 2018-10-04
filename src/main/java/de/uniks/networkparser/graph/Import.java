package de.uniks.networkparser.graph;

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

public class Import extends GraphMember {
	public static Import create(Clazz value) {
		return new Import().withChildren(value);
	}

	public static Import create(String value) {
		return new Import().withChildren(new Clazz(value));
	}

	public static Import create(Class<?> type) {
		return new Import().withChildren(new Clazz(type));
	}

	@Override
	public Import with(String name) {
		super.with(name);
		return this;
	}

	protected Import withChildren(GraphMember... values) {
		super.withChildren(values);
		return this;
	}

	public Clazz getClazz() {
		if (this.children != null && this.children instanceof Clazz) {
			return (Clazz) this.children;
		}
		return null;
	}
}
