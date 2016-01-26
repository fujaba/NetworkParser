package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class AttributeSet extends SimpleSet<Attribute>{
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Attribute item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}
	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for(Attribute item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}
	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for(Attribute item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}
	
	@Override
	public AttributeSet filter(Condition<Attribute> newValue) {
		AttributeSet collection = new AttributeSet();
		filterItems( collection, newValue);
		return collection;
	}
	
	public AttributeSet hasName(String otherValue) {
		return filter(Attribute.NAME.equals(otherValue));
	}
}
