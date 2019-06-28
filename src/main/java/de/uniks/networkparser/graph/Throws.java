package de.uniks.networkparser.graph;

public class Throws extends GraphMember {
	public Throws(String name) {
		super.with(name);
	}

	@Override
	public Throws with(String name) {
		super.with(name);
		return this;
	}
}
