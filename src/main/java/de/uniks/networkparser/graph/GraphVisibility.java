package de.uniks.networkparser.graph;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
public class GraphVisibility

{
	public static final GraphVisibility PUBLIC = new GraphVisibility("public");
	public static final GraphVisibility PACKAGE = new GraphVisibility("");
	public static final GraphVisibility PROTECTED = new GraphVisibility("protected");
	public static final GraphVisibility PRIVATE = new GraphVisibility("private");

	public static final GraphVisibility FINAL = new GraphVisibility(" final");
	public static final GraphVisibility ABSTRACT = new GraphVisibility(" abstract");
	public static final GraphVisibility STATIC = new GraphVisibility(" static");

	private String value;

	GraphVisibility(String value) {
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GraphVisibility withValue(String value) {
		this.value = value;
		return this;
	}

	public static GraphVisibility ref(String value) {
		return new GraphVisibility(value);
	}

	public static GraphVisibility ref(GraphVisibility... value) {
		GraphVisibility first = PUBLIC;
		String seconds = "";
		for (GraphVisibility item : value) {
			if (item == PUBLIC || item == PACKAGE || item == PROTECTED
					|| item == PRIVATE) {
				first = item;
				continue;
			}
			seconds += item.getValue();
		}
		return new GraphVisibility(first + seconds);
	}

	public boolean same(GraphVisibility other) {
		return this.getValue().equalsIgnoreCase(other.getValue());
	}

	public boolean has(GraphVisibility other) {
		return this.getValue().contains(other.getValue());
	}

	@Override
	public String toString() {
		return this.value;
	}
}
