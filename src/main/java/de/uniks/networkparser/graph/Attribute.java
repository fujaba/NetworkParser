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
 * The Class Attribute.
 *
 * @author Stefan
 */
public class Attribute extends Value {
	Attribute() {
	}

	/**
	 * Instantiates a new attribute.
	 *
	 * @param name the name
	 * @param datatyp the datatyp
	 */
	public Attribute(String name, DataType datatyp) {
		this.with(name);
		this.with(datatyp);
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the attribute
	 */
	@Override
	public Attribute withValue(String value) {
		super.withValue(value);
		return this;
	}

	/**
	 * Gets the modifier.
	 *
	 * @return the modifier
	 */
	@Override
	public Modifier getModifier() {
		Modifier modifier = super.getModifier();
		if (modifier == null) {
			modifier = new Modifier(Modifier.PRIVATE.getName());
			super.withChildren(modifier);
		}
		return modifier;
	}

	/**
	 * With.
	 *
	 * @param modifier the modifier
	 * @return the attribute
	 */
	public Attribute with(Modifier... modifier) {
		super.withModifier(modifier);
		return this;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the attribute
	 */
	/* Redirect */
	@Override
	public Attribute with(String value) {
		super.with(value);
		return this;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the attribute
	 */
	@Override
	public Attribute with(DataType value) {
		super.with(value);
		return this;
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @param typ the typ
	 * @return the attribute
	 */
	public Attribute with(String name, DataType typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @param typ the typ
	 * @param shortName the short name
	 * @return the value
	 */
	public String getValue(String typ, boolean shortName) {
		if (GraphTokener.OBJECTDIAGRAM.equals(typ)) {
			if (DataType.STRING == this.type && this.value != null && this.value.startsWith("\"") == false) {
				return "\"" + this.value + "\"";
			}
			return this.value;
		}
		if (this.type != null) {
			return this.type.getName(shortName);
		}
		return null;
	}

	/**
	 * Gets the annotation.
	 *
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the attribute
	 */
	public Attribute with(Annotation value) {
		super.withAnnotation(value);
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		CharacterBuffer sb = new CharacterBuffer();
		sb.with(getName());
		sb.with(':');
		if (this.type != null) {
			sb.with(this.type.getName(true));
		}
		if (getValue(GraphTokener.OBJECTDIAGRAM, false) != null) {
			sb.with('=');
			sb.with(getValue(GraphTokener.OBJECTDIAGRAM, false));
		}
		return sb.toString();
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute other = (Attribute) obj;
			String myName = this.getName();
			if (myName == null) {
				return other.getName() == null;
			} else if (myName.equalsIgnoreCase(other.getName())) {
				return true;
			}
		} else if (obj instanceof Association) {
			Association assoc = (Association) obj;
			String myName = this.getName();
			if (myName == null) {
			} else if (myName.equalsIgnoreCase(assoc.getOther().getName())) {
				return true;
			}
		}
		return super.equals(obj);
	}
}
