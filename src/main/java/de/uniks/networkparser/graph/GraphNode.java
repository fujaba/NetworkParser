package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class GraphNode implements GraphMember{
	private String id;
	private int count;
	protected GraphNode parentNode;
	protected GraphSimpleSet<GraphMember> children=new GraphSimpleSet<GraphMember>();
	protected SimpleSet<GraphEdge> associations = new SimpleSet<GraphEdge>();

	// GETTER AND SETTER
	public String getId() {
		return id;
	}

	public String getTyp(String typ, boolean shortName) {
		return getId();
	}

	public GraphNode withTyp(String typ, String value) {
		withId(value);
		return this;
	}

	public GraphNode withId(String id) {
		this.id = id;
		return this;
	}

	public void addAttribute(String property, GraphDataType clazz, String value) {
		with(new GraphAttribute().withValue(value).with(property).with(clazz));
	}

	@Override
	public String toString() {
		return id;
	}

	public GraphNode with(GraphAttribute... values) {
		return withMember(values);
	}
	public GraphNode with(GraphMethod... values) {
		return withMember(values);
	}

	public GraphNode with(GraphLiteral... values) {
		return withMember(values);
	}
	public GraphNode with(GraphList... values) {
		return withMember(values);
	}
	GraphNode with(GraphMember... values) {
		return withMember(values);
	}
	
	public GraphNode with(GraphEdge... values) {
		if (values != null) {
			for (GraphEdge value : values) {
				if(this.associations.add(value)) {
					value.with(this);
				}
			}
		}
		return this;
	}

	GraphNode without(GraphMember... values) {
		if (values != null) {
			for (GraphMember value : values) {
				if(value != null) {
					this.children.remove(value);
					value.withParent(null);
				}
			}
		}
		return this;
	}

	GraphNode withMember(GraphMember... values) {
		if (values != null) {
			for (GraphMember value : values) {
				if(value != null) {
					this.children.add(value);
					value.withParent(this);
				}
			}
		}
		return this;
	}
	
	public GraphMember getByObject(String clazz, boolean fullName) {
		if(clazz == null){
			return null;
		}
		String sub = clazz;
		if(clazz.lastIndexOf(".")>=0) {
			sub = clazz.substring(clazz.lastIndexOf(".")+1);
		}
		for(GraphMember item : children) {
			if(clazz.equalsIgnoreCase(item.getId()) || sub.equalsIgnoreCase(item.getId())){
				return item;
			}
		}
		if(fullName || clazz.lastIndexOf(".") < 0) {
			return null;
		}
		sub = "."+clazz.substring(clazz.lastIndexOf(".")+1);
		for(GraphMember item : children) {
			if(item.getId().endsWith(clazz)){
				return item;
			}
		}
		return null;
	}
	
	public boolean setParent(GraphNode value) {
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
			return true;
		}
		return false;
	}
	
	public GraphNode withParent(GraphNode value) {
		setParent(value);
		return this;
	}
	
	public GraphNode getParent() {
		return parentNode;
	}

	int getCount() {
		return count;
	}
	void addCounter() {
		this.count++;
	}

	GraphSimpleSet<GraphMember> getChildren() {
		return children;
	}
}
