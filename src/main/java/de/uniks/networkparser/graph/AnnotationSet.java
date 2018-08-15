package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class AnnotationSet extends SimpleSet<Annotation> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public AnnotationSet hasName(String otherValue) {
		return filter(Annotation.NAME.equals(otherValue));
	}
}
