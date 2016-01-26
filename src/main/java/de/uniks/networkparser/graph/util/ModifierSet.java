package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.list.SimpleSet;

public class ModifierSet extends SimpleSet<Modifier> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Modifier item : this) {
			collection.withAll(item.getParent());
		}
		return collection;
	}
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Modifier item : this) {
			collection.withAll(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Modifier item : this) {
			collection.withAll(item.getParent());
		}
		return collection;
	}
}
