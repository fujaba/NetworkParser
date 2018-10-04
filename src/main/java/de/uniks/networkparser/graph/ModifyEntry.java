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

public class ModifyEntry extends GraphMember {
	public static final String TYPE_DELETE = "delete";
	public static final String TYPE_MODIFIER = "modifier";
	private String type;

	public ModifyEntry withEntry(GraphMember child) {
		super.withChildren(child);
		return this;
	}

	public ModifyEntry withModifier(String type) {
		this.type = type;
		return this;
	}

	public String getType() {
		return type;
	}

	public static ModifyEntry createDelete(GraphMember child) {
		ModifyEntry result = new ModifyEntry();
		result.withModifier(TYPE_DELETE);
		result.withEntry(child);
		return result;
	}

	public static ModifyEntry createModifier(GraphMember child) {
		ModifyEntry result = new ModifyEntry();
		result.withModifier(TYPE_MODIFIER);
		result.withEntry(child);
		return result;
	}

	public GraphMember getEntry() {
		if (children == null) {
			return null;
		}
		if (this.children instanceof GraphSimpleSet) {
			GraphSimpleSet set = (GraphSimpleSet) this.children;
			return set.first();
		}
		return (GraphMember) children;
	}
}
