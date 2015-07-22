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
public class GraphModifier

{
	public static final GraphModifier PUBLIC = new GraphModifier("public");
	public static final GraphModifier PACKAGE = new GraphModifier("");
	public static final GraphModifier PROTECTED = new GraphModifier("protected");
	public static final GraphModifier PRIVATE = new GraphModifier("private");

	public static final GraphModifier FINAL = new GraphModifier(" final");
	public static final GraphModifier ABSTRACT = new GraphModifier(" abstract");
	public static final GraphModifier STATIC = new GraphModifier(" static");

	private String value;

	GraphModifier(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GraphModifier withValue(String value) {
		this.value = value;
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
			seconds += item.getValue();
		}
		return new GraphModifier(first + seconds);
	}

	public boolean same(GraphModifier other) {
		return this.getValue().equalsIgnoreCase(other.getValue());
	}

	public boolean has(GraphModifier other) {
		return this.getValue().contains(other.getValue());
	}

	@Override
	public String toString() {
		return this.value;
	}
}
