package de.uniks.networkparser.graph;

public class GraphEnumeration extends GraphAbstractClazz {
	public GraphEnumeration withId(String id) {
		super.withId(id);
		return this;
	}
	public GraphEnumeration withParent(GraphNode value) {
		super.withParent(value);
		return this;
	}
	public GraphEnumeration withExternal(boolean value) {
		super.withExternal(value);
		return this;
	}
	public GraphEnumeration withTyp(String typ, String value) {
		super.withTyp(typ, value);
		return this;
	}
}
