package de.uniks.networkparser.graph;


public class GraphClazzDiff extends GraphClazz{
	private GraphClazzDiff match;

	public GraphClazzDiff getMatch() {
		return match;
	}

	public GraphClazzDiff withMatch(GraphClazzDiff value) {
		if (this.match != value) {
			GraphClazzDiff oldValue = this.match;
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
