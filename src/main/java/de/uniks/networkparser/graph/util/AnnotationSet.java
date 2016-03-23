package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.interfaces.Condition;
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
	@Override
	public AnnotationSet filter(Condition<Annotation> newValue) {
		AnnotationSet collection = new AnnotationSet();
		filterItems( collection, newValue);
		return collection;
	}

	public AnnotationSet hasName(String otherValue) {
		return filter(Annotation.NAME.equals(otherValue));
	}
}
