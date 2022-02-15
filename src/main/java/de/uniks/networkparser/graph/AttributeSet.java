package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class AttributeSet.
 *
 * @author Stefan
 */
public class AttributeSet extends SimpleSet<Attribute> {
	
	/**
	 * Instantiates a new attribute set.
	 */
	public AttributeSet() {
		this.withType(Attribute.class);
	}

	/**
	 * Gets the clazzes.
	 *
	 * @return the clazzes
	 */
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Attribute item : this) {
			collection.add(item.getClazz());
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
		for (Attribute item : this) {
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
		for (Attribute item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	/**
	 * Gets the data types.
	 *
	 * @return the data types
	 */
	public SimpleSet<DataType> getDataTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Attribute item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the attribute set
	 */
	public AttributeSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Attribute.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public AttributeSet getNewList(boolean keyValue) {
		return new AttributeSet();
	}
}
