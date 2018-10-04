package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

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

	public DateTypeSet getReturnTypes() {
		DateTypeSet collection = new DateTypeSet();
		for (Method item : this) {
			collection.add(item.getReturnType());
		}
		return collection;
	}

	public MethodSet hasName(String otherValue) {
		return filter(Method.NAME.equals(otherValue));
	}
}
