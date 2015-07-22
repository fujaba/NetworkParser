package de.uniks.networkparser.graph;

public class GraphNode implements GraphMember{
	private String id;
	private int count;
	private GraphNode parentNode;
	protected GraphSimpleList<GraphMember> children=new GraphSimpleList<GraphMember>();

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

	public void addValue(String property, GraphDataType clazz, String value) {
		children.add(new GraphAttribute().withValue(value).with(property).with(clazz));
	}

	@Override
	public String toString() {
		return id;
	}

	public GraphNode with(GraphAttribute... values) {
		if (values != null) {
			for (GraphAttribute value : values) {
				this.children.add(value);
			}
		}
		return this;
	}
	public GraphNode with(GraphMethod... values) {
		if (values != null) {
			for (GraphMethod value : values) {
				this.children.add(value);
			}
		}
		return this;
	}
	public GraphNode with(GraphLiteral... values) {
		if (values != null) {
			for (GraphLiteral value : values) {
				this.children.add(value);
			}
		}
		return this;
	}
	public GraphNode with(GraphList... values) {
		if (values != null) {
			for (GraphList value : values) {
				this.children.add(value);
			}
		}
		return this;
	}

	GraphNode with(GraphMember... values) {
		if (values != null) {
			for (GraphMember value : values) {
				this.children.add(value);
			}
		}
		return this;
	}
	GraphNode without(GraphMember... values) {
		if (values != null) {
			for (GraphMember value : values) {
				this.children.remove(value);
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
	
	public GraphNode withParent(GraphNode value) {
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
	
	public GraphNode getParent(GraphNode value) {
		return parentNode;
	}

	int getCount() {
		return count;
	}
	void addCounter() {
		this.count++;
	}

	GraphSimpleList<GraphMember> getChildren() {
		return children;
	}
}
