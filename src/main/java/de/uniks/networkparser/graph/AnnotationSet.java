package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class AnnotationSet.
 *
 * @author Stefan
 */
public class AnnotationSet extends SimpleSet<Annotation> {
	
	/**
	 * Gets the methods.
	 *
	 * @return the methods
	 */
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	/**
	 * Gets the clazzes.
	 *
	 * @return the clazzes
	 */
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Annotation item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the annotation set
	 */
	public AnnotationSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Annotation.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Annotation> getNewList(boolean keyValue) {
		return new AnnotationSet();
	}
}
