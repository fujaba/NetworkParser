package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class GraphSetDataType extends GraphDataType{
	private GraphDataType generic;
	
	GraphSetDataType() {
		super(SimpleSet.class.getName());
		this.value.withExternal(true);
	}
	
	public static GraphSetDataType ref(GraphClazz value) {
		GraphSetDataType result = new GraphSetDataType().withGeneric(GraphDataType.ref(value));
		return result;
	}
	public static GraphSetDataType ref(String value) {
		GraphSetDataType result = new GraphSetDataType().withGeneric(GraphDataType.ref(value));
		return result;
	}
	
	public static GraphSetDataType ref(GraphDataType value) {
		GraphSetDataType result = new GraphSetDataType().withGeneric(value);
		return result;
	}


	private GraphSetDataType withGeneric(GraphDataType value) {
		this.generic = value;
		return this;
	}

	public GraphDataType getGeneric() {
		return generic;
	}
	
	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getName(shortName) + ">";
	}
}
