package de.uniks.networkparser.graph;

public class GraphModel extends GraphNode {

	public GraphClazz createClazz(String string) {
		GraphClazz result = new GraphClazz();
		this.add(result);
		return result;
	}
}
