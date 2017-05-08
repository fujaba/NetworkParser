package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;

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
/**
 * Modifier for Methods and Class
 * @author Stefan Lindel
 */

public class Modifier extends GraphMember {
	public static final StringFilter<Modifier> NAME = new StringFilter<Modifier>(GraphMember.PROPERTY_NAME);

	public static final Modifier PUBLIC = new Modifier("public");
	public static final Modifier PACKAGE = new Modifier("");
	public static final Modifier PROTECTED = new Modifier("protected");
	public static final Modifier PRIVATE = new Modifier("private");

	public static final Modifier FINAL = new Modifier("final");
	public static final Modifier ABSTRACT = new Modifier("abstract");
	public static final Modifier STATIC = new Modifier("static");
	public static final Modifier DEFAULT = new Modifier("default");

	Modifier(String value) {
		this.setName(value);
	}
	Modifier(Modifier value) {
		this.setName(value.getName());
	}

	@Override
	public Modifier with(String name) {
		super.with(name);
		return this;
	}

	public static Modifier create(String value) {
		return new Modifier(value);
	}

	public static Modifier create(Modifier... values) {
		if(values == null || values.length < 1) {
			return null;
		}
		Modifier mod=new Modifier(values[0].getName());
		mod.withModifier(values);
		return mod;
	}

	public boolean has(Modifier other) {
		if(this.getName().equals(other.getName())) {
			return true;
		}
		if(this.children != null) {
			for(GraphMember member : this.getChildren()) {
				if((member instanceof Modifier) == false) {
					continue;
				}
				if(((Modifier)member).has(other)) {
					return true;
				}
			}
		}
		return false;

	}

	public GraphMember getParent() {
		return (GraphMember) parentNode;
	}

	@Override
	public String toString() {
		CharacterBuffer buffer=new CharacterBuffer();
		String name = this.getName();
		GraphSimpleSet list = this.getChildren();
		if(name != null && name.length()>0) {
			buffer.with(name);
			if(list.size()>0) {
				buffer.with(" ");
			}
		}
		for(int i=0;i<list.size();i++) {
			GraphMember member = list.get(i);
			if((member instanceof Modifier) == false) {
				continue;
			}
			name = member.getName();
			if(name != null && name.length()>0) {
				buffer.with(name);
				if((i+1)<list.size()) {
					buffer.with(" ");
				}
			}
		}
		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj.hashCode() == this.hashCode()) {
			return true;
		}
		if(obj instanceof Modifier) {
			return this.has((Modifier) obj);
		}
		return super.equals(obj);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
