package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class MethodSet extends SimpleSet<Method> {
	public MethodSet() {
		this.withType(Method.class);
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Method item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	public ParameterSet getParameters() {
		ParameterSet collection = new ParameterSet();
		for (Method item : this) {
			collection.addAll(item.getParameters());
		}
		return collection;
	}

	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for (Method item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for (Method item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	public SimpleSet<DataType> getReturnTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Method item : this) {
			collection.add(item.getReturnType());
		}
		return collection;
	}

	public MethodSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Method.PROPERTY_NAME, otherValue));
	}
	
	@Override
	public SimpleSet<Method> getNewList(boolean keyValue) {
		return new MethodSet();
	}
}
