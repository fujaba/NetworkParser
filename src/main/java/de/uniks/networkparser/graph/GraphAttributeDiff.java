package de.uniks.networkparser.graph;


public class GraphAttributeDiff extends GraphAttribute{
	private GraphAttributeDiff match;

	public GraphAttributeDiff isMatch() {
		return match;
	}

	public GraphAttributeDiff withMatch(GraphAttributeDiff value) {
		if (this.match != value) {
			GraphAttributeDiff oldValue = this.match;
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
