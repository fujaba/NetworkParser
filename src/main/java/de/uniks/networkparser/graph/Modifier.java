package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
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

	Modifier(String value) {
		this.setName(value);
	}

	@Override
	public Modifier with(String name) {
		super.with(name);
		return this;
	}

	public static Modifier create(String value) {
		return new Modifier(value);
	}

	public static Modifier create(Modifier... value) {
		Modifier mod=new Modifier("");
		for (Modifier item : value) {
			if (item.has(PUBLIC) || item.has(PACKAGE) || item.has(PROTECTED)
					|| item.has(PRIVATE)) {
				mod.with(item.getName());
				continue;
			}
			mod.withChildren(true, item);
		}
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
		return parentNode;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Modifier) {
			return this.has((Modifier) obj);
		}
		return super.equals(obj);
	}
}
