package de.uniks.networkparser.graph;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class ClazzSet.
 *
 * @author Stefan
 */
public class ClazzSet extends SimpleSet<Clazz> {
	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public AttributeSet getAttributes() {
		AttributeSet collection = new AttributeSet();
		for (Clazz item : this) {
			collection.addAll(item.getAttributes());
		}
		return collection;
	}

	/**
	 * Gets the associations.
	 *
	 * @return the associations
	 */
	public AssociationSet getAssociations() {
		AssociationSet collection = new AssociationSet();
		for (Clazz item : this) {
			collection.addAll(item.getAssociations());
		}
		return collection;
	}

	/**
	 * Gets the methods.
	 *
	 * @return the methods
	 */
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Clazz item : this) {
			collection.addAll(item.getMethods());
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
		for (Clazz item : this) {
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
		for (Clazz item : this) {
			collection.add(item.getModifier());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the clazz set
	 */
	public ClazzSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Clazz.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Clazz> getNewList(boolean keyValue) {
		return new ClazzSet();
	}

	/**
	 * To string.
	 *
	 * @param splitter the splitter
	 * @return the string
	 */
	public String toString(String splitter) {
		if (size() == 0) {
			return null;
		}
		CharacterBuffer buffer = new CharacterBuffer();
		for (Clazz clazz : this) {
			if (buffer.length() > 0) {
				buffer.with(splitter);
			}
			buffer.with(clazz.getName());
		}
		return buffer.toString();
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean contains(Object o) {
		if (o instanceof String && o != null) {
			for (Clazz item : this) {
				if (o.equals(item.getId())) {
					return true;
				}
			}
		}
		return super.contains(o);
	}

	/**
	 * Gets the clazz.
	 *
	 * @param id the id
	 * @return the clazz
	 */
	public Clazz getClazz(String id) {
		if (id == null) {
			return null;
		}
		for (Clazz item : this) {
			if (id.equals(item.getName())) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * To array.
	 *
	 * @return the clazz[]
	 */
	@Override
	public Clazz[] toArray() {
		return super.toArray(new Clazz[size()]);
	}
}
