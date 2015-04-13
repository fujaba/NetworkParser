package de.uniks.networkparser.graph;


public class GraphPattern extends GraphNode {
	private String bounds;
	// GETTER AND SETTER
	@Override
	public GraphPattern getNewList(boolean keyValue) {
		return new GraphPattern();
	}
	
	public GraphPattern withId(String id) {
		super.withId(id);
		return this;
	}

	public String getBounds() {
		return bounds;
	}

	public GraphPattern withBounds(String bounds) {
		this.bounds = bounds;
		return this;
	}
}
