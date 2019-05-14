package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class AnnotationSet extends SimpleSet<Annotation> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	public AnnotationSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Annotation.PROPERTY_NAME, otherValue));
	}
	
	@Override
	public SimpleSet<Annotation> getNewList(boolean keyValue) {
		return new AnnotationSet();
	}
}
