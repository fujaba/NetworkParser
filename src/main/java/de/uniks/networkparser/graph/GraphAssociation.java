package de.uniks.networkparser.graph;

public class GraphAssociation extends GraphEdge{
	public GraphAssociation() {
		super.withTyp(GraphEdgeTypes.ASSOCIATION);
	}
	public GraphAssociation withTarget(GraphClazz tgtClass, String tgtRoleName, GraphCardinality tgtCard) {
		this.with(tgtClass);
		this.withInfo(tgtRoleName);
		this.with(tgtCard);
		return this;
	}
	public GraphAssociation withSource(GraphClazz srcClass, String srcRoleName, GraphCardinality srcCard) {
		GraphEdge source = new GraphAssociation().withInfo(srcRoleName).with(srcCard).with(srcClass);
		this.with(source);
		return this;
	}

}
