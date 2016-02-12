package de.uniks.networkparser.graph;

import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

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
	public static final String PROPERTY_NAME="name";
	protected String name;
	protected Object children;
	protected Object parentNode;

	Object getValue(String attribute) {
		if(PROPERTY_NAME.equals(attribute)) {
			return this.name;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected boolean check(GraphMember element, Condition<?>... filters) {
		if(filters == null) {
			return element != null;
		}
		boolean result=true;
		for(Condition<?> item : filters) {
			Condition<Object> filter = (Condition<Object>) item;
			if(filter != null && result) {
				result = filter.update(element);
			}
		}
		return result;
	}

	String getFullId() {
		return name;
	}
	// PACKAGE VISIBILITY
	GraphSimpleSet getChildren() {
		if(this.children instanceof GraphSimpleSet) {
			return (GraphSimpleSet)this.children;
		}
		GraphSimpleSet collection = new GraphSimpleSet();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof GraphMember) {
			collection.with(this.children);
		}
		return collection;
	}

	SimpleSet<GraphEntity> getNodes() {
		SimpleSet<GraphEntity> collection = new SimpleSet<GraphEntity>();
		if(this.children == null) {
			return collection;
		}
		if(this.children instanceof GraphEntity) {
			collection.add((GraphEntity)this.children);
			return collection;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet list = (GraphSimpleSet) this.children;
			for(GraphMember item : list) {
				if(item instanceof GraphEntity) {
					collection.add((GraphEntity)item);
				}
			}
		}
		return collection;
	}

	/** Set the name of Element
	 * @param name The Name of Element
	 * @return The Instance
	 */
	public GraphMember with(String name) {
		setName(name);
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
		return setParentNode(value);
	}
	
	protected boolean setParentNode(GraphMember value) {
		if (this.parentNode != value) {
			GraphMember oldValue = (GraphMember) this.parentNode;
			if (this.parentNode != null) {
				this.parentNode = null;
				oldValue.without(this);
			}
			this.parentNode = value;
			if (value != null) {
				value.withChildren(this);
			}
			return true;
		}
		return false;
	}

	protected GraphMember withChildren(GraphMember... values) {
		// Do Nothing
		if (values == null || (values.length == 1 && (this.children == values[0]))) {
			return this;
		}
		if(this.children == null) {
			if(values.length==1){
				this.children = values[0];
				((GraphMember)values[0]).setParent(this);
				return this;
			}
		}
		GraphSimpleSet list;
		if( this.children instanceof GraphSimpleSet) {
			list = (GraphSimpleSet) this.children;
		}else {
			list = new GraphSimpleSet();
			list.with((GraphMember) this.children);
			this.children = list;
		}
		for (GraphMember value : values) {
			if(value != null ) {
				if(list.add(value)) {
					value.setParent(this);
				}
			}
		}
		return this;
	}

	protected GraphMember without(GraphMember... values) {
		if (values == null || this.children == null) {
			return this;
		}
		if(this.children instanceof GraphMember) {
			for (GraphMember value : values) {
				if(this.children == value) {
					this.children = null;
					value.setParent(null);
				}
			}
			return this;
		}
		GraphSimpleSet collection = (GraphSimpleSet) this.children;
		for (GraphMember value : values) {
			if(value != null) {
				collection.remove(value);
				value.setParent(null);
			}
		}
		return this;
	}

	GraphDiff getDiff() {
		if(this.children == null) {
			return null;
		}
		for(GraphMember item : getChildren()) {
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
			if(this.children instanceof GraphMember) {
				if(this.children instanceof Annotation) {
					((Annotation)this.children).setParent(null);
					this.children = null;
				}
			}
			if(this.children instanceof GraphSimpleSet) {
				GraphSimpleSet collection = (GraphSimpleSet) this.children;
				for(int i=collection.size();i>=0;i--) {
					if(collection.get(i) instanceof Annotation) {
						GraphMember oldValue = collection.remove(i);
						oldValue.setParent(null);
					}
				}
			}
		}
		withChildren(value);
		return this;
	}

	protected Annotation getAnnotation() {
		if(this.children == null) {
			return null;
		}
		if (this.children instanceof Annotation) {
			return (Annotation)this.children;
		} else if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			for(GraphMember item : collection) {
				if(item instanceof Annotation) {
					return (Annotation) item;
				}
			}
		}
		return null;
	}

	public Modifier getModifier() {
		if(this.children == null) {
			return null;
		}
		if (this.children instanceof Modifier) {
			return (Modifier)this.children;
		} else if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet collection = (GraphSimpleSet) this.children;
			for(GraphMember item : collection) {
				if(item instanceof Modifier) {
					return (Modifier) item;
				}
			}
		}
		return null;
	}
	GraphMember withModifier(Modifier... values) {
		if(values == null) {
			return this;
		}
		Modifier rootModifier = getModifier();
		for (Modifier item : values) {
			if (item.has(Modifier.PUBLIC) || item.has(Modifier.PACKAGE) || item.has(Modifier.PROTECTED)
					|| item.has(Modifier.PRIVATE)) {
				rootModifier.with(item.getName());
				continue;
			}
			rootModifier.withChildren(item);
		}
		return this;
	}
}
