package de.uniks.networkparser.graph;

public class GraphNode extends GraphMember{
	@Override
	public GraphNode with(String name) {
		super.with(name);
		return this;
	}
	
    public GraphNode withParent(GraphMember value) {
    	super.setParent(value);
        return this;
    }

	
	@Override
	public String toString() {
		return name;
	}
}
