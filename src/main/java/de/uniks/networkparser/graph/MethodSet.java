package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class MethodSet.
 *
 * @author Stefan
 */
public class MethodSet extends SimpleSet<Method> {
	
	/**
	 * Instantiates a new method set.
	 */
	public MethodSet() {
		this.withType(Method.class);
	}

	/**
	 * Gets the clazzes.
	 *
	 * @return the clazzes
	 */
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Method item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public ParameterSet getParameters() {
		ParameterSet collection = new ParameterSet();
		for (Method item : this) {
			collection.addAll(item.getParameters());
		}
		return collection;
	}

	/**
	 * Gets the annotations.
	 *
	 * @return the annotations
	 */
	public AnnotationSet getAnnotations() {
		AnnotationSet collection = new AnnotationSet();
		for (Method item : this) {
			collection.add(item.getAnnotation());
		}
		return collection;
	}

	/**
	 * Gets the modifiers.
	 *
	 * @return the modifiers
	 */
	public ModifierSet getModifiers() {
		ModifierSet collection = new ModifierSet();
		for (Method item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	/**
	 * Gets the return types.
	 *
	 * @return the return types
	 */
	public SimpleSet<DataType> getReturnTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Method item : this) {
			collection.add(item.getReturnType());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the method set
	 */
	public MethodSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Method.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Method> getNewList(boolean keyValue) {
		return new MethodSet();
	}
}
