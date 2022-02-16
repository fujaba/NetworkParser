package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;

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
/**
 * Modifier for Methods and Class.
 *
 * @author Stefan Lindel
 */

public class Modifier extends GraphMember {
	
	/** The Constant PUBLIC. */
	public static final Modifier PUBLIC = new Modifier("public");
	
	/** The Constant PACKAGE. */
	public static final Modifier PACKAGE = new Modifier("");
	
	/** The Constant PROTECTED. */
	public static final Modifier PROTECTED = new Modifier("protected");
	
	/** The Constant PRIVATE. */
	public static final Modifier PRIVATE = new Modifier("private");

	/** The Constant FINAL. */
	public static final Modifier FINAL = new Modifier("final");
	
	/** The Constant ABSTRACT. */
	public static final Modifier ABSTRACT = new Modifier("abstract");
	
	/** The Constant STATIC. */
	public static final Modifier STATIC = new Modifier("static");
	
	/** The Constant DEFAULT. */
	public static final Modifier DEFAULT = new Modifier("default");

	Modifier(String value) {
		this.setName(value);
	}

	Modifier(Modifier value) {
		this.setName(value.getName());
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @return the modifier
	 */
	@Override
	public Modifier with(String name) {
		super.with(name);
		return this;
	}

	/**
	 * Creates the.
	 *
	 * @param value the value
	 * @return the modifier
	 */
	public static Modifier create(String value) {
		return new Modifier(value);
	}

	/**
	 * Creates the.
	 *
	 * @param values the values
	 * @return the modifier
	 */
	public static Modifier create(Modifier... values) {
		if (values == null || values.length < 1 || values[0] == null) {
			return null;
		}
		Modifier mod = new Modifier(values[0].getName());
		mod.withModifier(values);
		return mod;
	}

	/**
	 * Checks for.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean has(Modifier other) {
		if (this.getName() == null || other == null) {
			return false;
		}
		if (this.getName().equals(other.getName())) {
			return true;
		}
		if (this.children != null) {
			for (GraphMember member : this.getChildren()) {
				if (!(member instanceof Modifier)) {
					continue;
				}
				if (((Modifier) member).has(other)) {
					return true;
				}
			}
		}
		return false;

	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public GraphMember getParent() {
		return (GraphMember) parentNode;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		CharacterBuffer buffer = new CharacterBuffer();
		String name = this.getName();
		GraphSimpleSet list = this.getChildren();
		if (name != null && name.length() > 0) {
			buffer.with(name);
			if (list.size() > 0) {
				buffer.with(" ");
			}
		}
		for (int i = 0; i < list.size(); i++) {
			GraphMember member = list.get(i);
			if (!(member instanceof Modifier)) {
				continue;
			}
			name = member.getName();
			if (name != null && name.length() > 0) {
				buffer.with(name);
				if ((i + 1) < list.size()) {
					buffer.with(" ");
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.hashCode() == this.hashCode()) {
			return true;
		}
		if (obj instanceof Modifier) {
			return this.has((Modifier) obj);
		}
		return super.equals(obj);
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
