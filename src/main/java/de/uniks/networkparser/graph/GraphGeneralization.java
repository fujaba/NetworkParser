package de.uniks.networkparser.graph;

public class GraphGeneralization extends GraphEdge{
	public GraphGeneralization() {
		super.withTyp(GraphEdgeTypes.GENERALISATION);
	}

	public GraphGeneralization withTarget(GraphClazz tgtClass) {
		this.with(tgtClass);
		return this;
	}
	public GraphGeneralization withSource(GraphClazz srcClass) {
		GraphEdge source = new GraphGeneralization().with(srcClass);
		source.withTyp(GraphEdgeTypes.CHILD);
		this.with(source);
		return this;
	}
	
	public GraphGeneralization with(GraphClazz srcClass, GraphClazz tgtClass) {
		withSource(srcClass);
		withTarget(tgtClass);
		return this;
	}

}
