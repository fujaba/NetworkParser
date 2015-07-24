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
public class GraphModifier implements GraphMember{
	public static final GraphModifier PUBLIC = new GraphModifier("public");
	public static final GraphModifier PACKAGE = new GraphModifier("");
	public static final GraphModifier PROTECTED = new GraphModifier("protected");
	public static final GraphModifier PRIVATE = new GraphModifier("private");

	public static final GraphModifier FINAL = new GraphModifier(" final");
	public static final GraphModifier ABSTRACT = new GraphModifier(" abstract");
	public static final GraphModifier STATIC = new GraphModifier(" static");

	private String id;
	private GraphNode parentNode;

	GraphModifier(String value) {
		this.setValue(value);
	}


	public boolean setValue(String value) {
		if(value != this.id) {
			this.id = value;
			return true;
		}
		return false;
	}

	public GraphModifier withId(String value) {
		this.id = value;
		return this;
	}

	public static GraphModifier ref(String value) {
		return new GraphModifier(value);
	}

	public static GraphModifier ref(GraphModifier... value) {
		GraphModifier first = PUBLIC;
		String seconds = "";
		for (GraphModifier item : value) {
			if (item == PUBLIC || item == PACKAGE || item == PROTECTED
					|| item == PRIVATE) {
				first = item;
				continue;
			}
			seconds += item.getId();
		}
		return new GraphModifier(first + seconds);
	}

	public boolean same(GraphModifier other) {
		return this.getId().equalsIgnoreCase(other.getId());
	}

	public boolean has(GraphModifier other) {
		return this.getId().contains(other.getId());
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public GraphModifier withParent(GraphNode value) {
		if (this.parentNode != value) {
			GraphNode oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.with(this);
			}
		}
		return this;
	}
}
