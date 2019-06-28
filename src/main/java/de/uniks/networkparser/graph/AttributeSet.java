package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class AttributeSet extends SimpleSet<Attribute> {
	public AttributeSet() {
		this.withType(Attribute.class);
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Attribute item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for (Attribute item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for (Attribute item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	public SimpleSet<DataType> getDataTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Attribute item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	public AttributeSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Attribute.PROPERTY_NAME, otherValue));
	}

	@Override
	public AttributeSet getNewList(boolean keyValue) {
		return new AttributeSet();
	}
}
