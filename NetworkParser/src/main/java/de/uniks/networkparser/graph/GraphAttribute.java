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

public class GraphAttribute extends GraphValue implements GraphMember {
	public static final String PROPERTY_CLAZZ = "clazz";
	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_VISIBILITY = "visibility";

	private GraphNode clazz = null;
	private String value = null;
	private GraphVisibility visibility = GraphVisibility.PRIVATE;

	public GraphAttribute() {
	}

	public GraphAttribute(String name, GraphDataType datatyp) {
		this.with(name);
		this.with(datatyp);
	}
	
	@Override
	public String getId() {
		return getName();
	}
	
	public String getValue() {
		return value;
	}

	public GraphAttribute withValue(String value) {
		this.value = value;
		return this;
	}

	public GraphVisibility getVisibility() {
		return visibility;
	}

	public GraphAttribute with(GraphVisibility visibility) {
		this.visibility = visibility;
		return this;
	}

	public GraphNode getClazz() {
		return clazz;
	}

	public void setClazz(GraphNode clazz) {
		this.clazz = clazz;
	}

	// Redirect
	@Override
	public GraphAttribute with(String value) {
		super.with(value);
		return this;
	}

	@Override
	public GraphAttribute with(GraphDataType value) {
		super.with(value);
		return this;
	}

	public GraphAttribute with(String name, GraphDataType typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	
	public String getValue(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return this.value;
		}
		return getType(shortName);
	}

}