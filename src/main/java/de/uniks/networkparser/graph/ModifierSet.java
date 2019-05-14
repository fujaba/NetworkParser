package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class ModifierSet extends SimpleSet<Modifier> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ModifierSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Modifier.PROPERTY_NAME, otherValue));
	}
	
	@Override
	public SimpleSet<Modifier> getNewList(boolean keyValue) {
		return new ModifierSet();
	}
}
