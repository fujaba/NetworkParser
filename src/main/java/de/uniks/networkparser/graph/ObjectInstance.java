package de.uniks.networkparser.graph;

public class ObjectInstance extends Clazz {

	public ObjectInstance(String name) {
		super(name);
	}

	public ObjectInstance withLink(ObjectInstance tgtInstance) {
		super.withAssoc(tgtInstance, Association.ONE);
		return this;
	}
}
