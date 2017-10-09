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

public class Attribute extends Value {
	public static final StringFilter<Attribute> NAME = new StringFilter<Attribute>(GraphMember.PROPERTY_NAME);

	Attribute() {
	}

	public Attribute(String name, DataType datatyp) {
		this.with(name);
		this.with(datatyp);
	}

	@Override
	public Attribute with(Class<?> value) {
		super.with(value);
		return this;
	}

	@Override
	public Attribute withValue(String value) {
		super.withValue(value);
		return this;
	}

	@Override
	public Modifier getModifier() {
		Modifier modifier = super.getModifier();
		if(modifier == null) {
			modifier = new Modifier(Modifier.PRIVATE.getName());
			super.withChildren(modifier);
		}
		return modifier;
	}

	public Attribute with(Modifier... modifier) {
		super.withModifier(modifier);
		return this;
	}

	// Redirect
	@Override
	public Attribute with(String value) {
		super.with(value);
		return this;
	}

	@Override
	public Attribute with(DataType value) {
		super.with(value);
		return this;
	}

	@Override
	public Attribute with(Clazz value) {
		super.with(value);
		return this;
	}

	public Attribute with(String name, DataType typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	public Attribute with(String name, Clazz typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	public String getValue(String typ, boolean shortName) {
		if (typ.equals(GraphTokener.OBJECT)) {
			if(DataType.STRING == this.type && !this.value.startsWith("\"")){
				return "\""+ this.value + "\"";
			}
			return this.value;
		}
		return getType(shortName);
	}
	
	public Annotation getAnnotation() {
		return super.getAnnotation();
	}

	public Attribute with(Annotation value) {
		super.withAnnotaion(value);
		return this;
	}
	
	@Override
	public String toString() {
		CharacterBuffer sb = new CharacterBuffer();
		sb.with(getName());
		sb.with(':');
		sb.with(getType(true));
		if(getValue()!= null) {
			sb.with('=');
			sb.with(getValue());
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Attribute) {
			Attribute other = (Attribute) obj;
			String myName = this.getName();
			if(myName == null){
				return other.getName() == null;
			} else if(myName.equalsIgnoreCase(other.getName())){
				return true;
			}
		} else if(obj instanceof Association) {
			Association assoc = (Association) obj;
			String myName = this.getName();
			if(myName == null){
			} else if(myName.equalsIgnoreCase(assoc.getOther().getName())){
				return true;
			}
		}
		return super.equals(obj);
	}
}
