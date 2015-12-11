package de.uniks.networkparser.graph;

public class GraphDiff extends GraphMember{
	private int count;
	private GraphMember match;
	private GraphEntity mainFile;
	
	public GraphDiff withMain(GraphEntity node) {
		this.mainFile = node;
		return this;
	}

	public GraphEntity getMainFile() {
		return mainFile;
	}

	public GraphMember getMatch() {
		return match;
	}

	public GraphDiff with(GraphMember value) {
		if (this.match != value) {
			GraphMember oldValue = this.match;
			if (oldValue != null) {
				this.match = null;
				oldValue.without(this);
			}
			this.match = value;
			if (value != null) {
				value.with(this);
			}
		}
		return this;
	}
	
	int getCount() {
		return count;
	}
	void addCounter() {
		this.count++;
	}
}
