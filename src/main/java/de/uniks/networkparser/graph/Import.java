package de.uniks.networkparser.graph;

public class Import extends GraphMember {
	public static Import create(Clazz value) {
		return new Import().withChildren(value);
	}
	public static Import create(String value) {
		return new Import().withChildren(new Clazz(value));
	}
	public static Import create(Class<?> type) {
		return new Import().withChildren(new Clazz(type));
	}
	
	@Override
	public Import with(String name) {
		super.with(name);
		return this;
	}
	
	protected Import withChildren(GraphMember... values) {
		super.withChildren(values);
		return this;
	}
	public Clazz getClazz() {
		if(this.children!= null && this.children instanceof Clazz) {
			return (Clazz) this.children;
		}
		return null;
	}
}
