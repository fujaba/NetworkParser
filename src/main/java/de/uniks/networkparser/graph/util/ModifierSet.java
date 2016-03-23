package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ModifierSet extends SimpleSet<Modifier> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	@Override
	public ModifierSet filter(Condition<Modifier> newValue) {
		ModifierSet collection = new ModifierSet();
		filterItems( collection, newValue);
		return collection;
	}

	public ModifierSet hasName(String otherValue) {
		return filter(Modifier.NAME.equals(otherValue));
	}
}
