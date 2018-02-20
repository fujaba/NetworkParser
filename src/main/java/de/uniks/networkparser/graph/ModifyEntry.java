package de.uniks.networkparser.graph;

public class ModifyEntry extends GraphMember{
	public static final String TYPE_DELETE="delete";
	public static final String TYPE_MODIFIER="modifier";
	private String type;
	public ModifyEntry withEntry(GraphMember child) {
		super.withChildren(child);
		return this;
	}

	public ModifyEntry withModifier(String type) {
		this.type = type;
		return this;
	}

	public String getType() {
		return type;
	}

	public static ModifyEntry createDelete(GraphMember child) {
		ModifyEntry result = new ModifyEntry();
		result.withModifier(TYPE_DELETE);
		result.withEntry(child);
		return result;
	}
	public static ModifyEntry createModifier(GraphMember child) {
		ModifyEntry result = new ModifyEntry();
		result.withModifier(TYPE_MODIFIER);
		result.withEntry(child);
		return result;
	}

	public GraphMember getEntry() {
		if(children == null) {
			return null;
		}
		if(this.children instanceof GraphSimpleSet) {
			GraphSimpleSet set = (GraphSimpleSet)this.children;
			return set.first();
		}
		return (GraphMember) children;
	}
}
