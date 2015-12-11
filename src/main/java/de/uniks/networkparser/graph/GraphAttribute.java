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

public class GraphAttribute extends GraphValue {
	public static final String PROPERTY_CLAZZ = "clazz";
	public static final String PROPERTY_VALUE = "value";
	public static final String PROPERTY_VISIBILITY = "visibility";

	private GraphModifier visibility = GraphModifier.PRIVATE;

	public GraphAttribute() {
	}

	public GraphAttribute(String name, GraphDataType datatyp) {
		this.with(name);
		this.with(datatyp);
	}
	
	@Override
	public GraphAttribute withValue(String value) {
		super.withValue(value);
		return this;
	}
	
	public GraphModifier getVisibility() {
		return visibility;
	}

	public GraphAttribute with(GraphModifier visibility) {
		this.visibility = visibility;
		return this;
	}

	public GraphClazz getClazz() {
		return (GraphClazz) parentNode;
	}
	
	public GraphAttribute withParent(GraphClazz parent) {
		super.setParent(parent);
		return this;
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
	
	@Override
	public GraphAttribute with(GraphClazz value) {
		super.with(value);
		return this;
	}

	public GraphAttribute with(String name, GraphDataType typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	public GraphAttribute with(String name, GraphClazz typ) {
		this.with(typ);
		this.with(name);
		return this;
	}

	
	public String getValue(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			if(GraphDataType.STRING == this.type && !this.value.startsWith("\"")){
				return "\""+ this.value + "\"";
			}
			return this.value;
		}
		return getType(shortName);
	}

	public GraphAnnotation getAnnotations() {
		if(this.children == null) {
			return null;
		}
		for(GraphMember item : this.children) {
			if(item instanceof GraphAnnotation) {
				return (GraphAnnotation) item;
			}
		}
		return null;
	}

	public GraphAttribute with(GraphAnnotation value) {
		// Remove Old GraphAnnotation
		if(this.children != null) {
			for(int i=this.children.size();i>=0;i--) {
				if(this.children.get(i) instanceof GraphAnnotation) {
					this.children.remove(i);
				}
			}
		}
		super.with(value);
		return this;
	}

	public String getName() {
		return this.name;
	}
}
