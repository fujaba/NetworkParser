package de.uniks.networkparser.graph;


public class GraphEdgeDiff extends GraphEdge{
	private GraphEdgeDiff match;

	public GraphEdgeDiff(GraphNode node, GraphCardinality cardinality, String property) {
		super(node, cardinality, property);
	}

	public GraphEdgeDiff() {
		super();
	}

	public GraphEdgeDiff getMatch() {
		return match;
	}

	public GraphEdgeDiff withMatch(GraphEdgeDiff value) {
		if (this.match != value) {
			GraphEdgeDiff oldValue = this.match;
			if (oldValue != null) {
				this.match = null;
				oldValue.withMatch(null);
			}
			this.match = value;
			if (value != null) {
				value.withMatch(this);
			}
		}
		return this;
	}

}
