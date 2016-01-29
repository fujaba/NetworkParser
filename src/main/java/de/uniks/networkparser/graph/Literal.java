package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleList;

public class Literal extends GraphMember{
	private SimpleList<Object> values;

	public Literal(String name) {
		super.with(name);
	}

	@Override
	public Literal with(String name) {
		super.with(name);
		return this;
	}
	public Literal withValue(Object... values) {
		if(values == null) {
			return this;
		}
		for(Object value : values) {
			if(value != null) {
				if(this.values == null) {
					this.values = new SimpleList<Object>();
				}
				this.values.add(value);
			}
		}
		return this;
	}

	public SimpleList<Object> getValues() {
		return values;
	}
}
