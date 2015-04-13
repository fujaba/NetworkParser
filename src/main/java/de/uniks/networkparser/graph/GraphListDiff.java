package de.uniks.networkparser.graph;


public class GraphListDiff extends GraphList{
	private GraphListDiff match;
	private GraphNode mainFile;


	public GraphListDiff getMatch() {
		return match;
	}

	public GraphListDiff withMatch(GraphListDiff match) {
		this.match = match;
		return this;
	}
	
	@Override
	public GraphList withMain(GraphNode node) {
		this.mainFile = node;
		return this;
	}

	public GraphNode getMainFile() {
		return mainFile;
	}
}
