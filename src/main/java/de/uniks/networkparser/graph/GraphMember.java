package de.uniks.networkparser.graph;

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
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

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
	
	public int size() {
		if(this.children == null) {
			return 0;
		}
		if(this.children instanceof GraphSimpleSet) {
			return ((GraphSimpleSet)this.children).size();
		}
		return 1;
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
		if((value != null && value.equals(this.name) == false)
				|| (value==null && this.name != null)) {
			this.name = value;
			return true;
		}
		return false;
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
				((GraphMember)values[0]).setParentNode(this);
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
					value.setParentNode(this);
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
					value.setParentNode(null);
				}
			}
			return this;
		}
		GraphSimpleSet collection = (GraphSimpleSet) this.children;
		for (GraphMember value : values) {
			if(value != null) {
				collection.remove(value);
				value.setParentNode(null);
			}
		}
		return this;
	}

	GraphDiff getDiff() {
		if(this.children == null) {
			GraphDiff graphDiff = new GraphDiff();
			this.withChildren(graphDiff);
			return graphDiff;
		}
		for(GraphMember item : getChildren()) {
			if(item instanceof GraphDiff) {
				return (GraphDiff) item;
			}
		}
		GraphDiff graphDiff = new GraphDiff();
		this.withChildren(graphDiff);
		return graphDiff;
	}

	public String getName() {
		return this.name;
	}

	protected GraphMember withAnnotaion(Annotation value) {
		// Remove Old GraphAnnotation
		if(this.children != null) {
			if(this.children instanceof GraphMember) {
				if(this.children instanceof Annotation) {
					((Annotation)this.children).setParentNode(null);
					this.children = null;
				}
			}
			if(this.children instanceof GraphSimpleSet) {
				GraphSimpleSet collection = (GraphSimpleSet) this.children;
				for(int i=collection.size();i>=0;i--) {
					if(collection.get(i) instanceof Annotation) {
						GraphMember oldValue = collection.remove(i);
						if(oldValue != null) {
							oldValue.setParentNode(null);
						}
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
		if(this instanceof Modifier) {
			return (Modifier)this;
		}
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
			rootModifier.withChildren(new Modifier(item.getName()));
		}
		return this;
	}
}
