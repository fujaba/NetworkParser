package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class ModifierSet.
 *
 * @author Stefan
 */
public class ModifierSet extends SimpleSet<Modifier> {
	
	/**
	 * Gets the methods.
	 *
	 * @return the methods
	 */
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Modifier item : this) {
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
		for (Modifier item : this) {
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
		for (Modifier item : this) {
			collection.with(item.getParent());
		}
		return collection;
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the modifier set
	 */
	public ModifierSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Modifier.PROPERTY_NAME, otherValue));
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Modifier> getNewList(boolean keyValue) {
		return new ModifierSet();
	}
}
