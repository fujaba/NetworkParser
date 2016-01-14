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

public abstract class GraphMember {
	protected String name;
	protected GraphSimpleSet<GraphMember> children;
	protected GraphMember parentNode;
	
	String getFullId() {
		return name;
	}
	// PACKAGE VISIBILITY
	GraphSimpleSet<GraphMember> getChildren() {
		if(this.children == null) {
			this.children = new GraphSimpleSet<GraphMember>();
		}
		return this.children;
	}
	
	/** Set the name of Element
	 * @param name The Name of Element
	 * @return The Instance	
	 */
	public GraphMember with(String name) {
		setName(name);
		this.name = name;
		return this;
	}

	boolean setName(String value) {
		if(value != this.name) {
			this.name = value;
			return true;
		}
		return false;
	}


	boolean setParent(GraphMember value) {
		if (this.parentNode != value) {
			GraphMember oldValue = this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.with(this);
			}
			return true;
		}
		return false;
	}
	
	protected GraphMember with(GraphMember... values) {
		if (values != null) {
			for (GraphMember value : values) {
				if(value != null) {
					getChildren().add(value);
					value.setParent(this);
				}
			}
		}
		return this;
	}
	
	protected GraphMember without(GraphMember... values) {
		if (values != null && this.children != null) {
			for (GraphMember value : values) {
				if(value != null) {
					this.children.remove(value);
					value.setParent(null);
				}
			}
		}
		return this;
	}
	
	GraphDiff getDiff() {
		if(this.children == null) {
			return null;
		}
		for(GraphMember item : this.children) {
			if(item instanceof GraphDiff) {
				return (GraphDiff) item;
			}
		}
		return null;
	}
	
	public String getName() {
		return this.name;
	}
	
	protected GraphMember withAnnotaion(Annotation value) {
		// Remove Old GraphAnnotation
		if(this.children != null) {
			for(int i=this.children.size();i>=0;i--) {
				if(this.children.get(i) instanceof Annotation) {
					this.children.remove(i);
				}
			}
		}
		with(value);
		return this;
	}
}
