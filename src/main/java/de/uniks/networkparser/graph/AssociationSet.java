package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

/**
 * The Class AssociationSet.
 *
 * @author Stefan
 */
public class AssociationSet extends SimpleSet<Association> {
	
	/** The Constant EMPTY_SET. */
	public static final AssociationSet EMPTY_SET = new AssociationSet();

	/**
	 * Instantiates a new association set.
	 */
	public AssociationSet() {
		this.withType(Association.class);
	}

	/**
	 * Gets the clazzes.
	 *
	 * @return the clazzes
	 */
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Association item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	/**
	 * Gets the other.
	 *
	 * @return the other
	 */
	public AssociationSet getOther() {
		AssociationSet collection = new AssociationSet();
		for (Association item : this) {
			collection.add(item.getOther());
		}
		return collection;
	}

	/**
	 * Gets the other clazz.
	 *
	 * @return the other clazz
	 */
	public ClazzSet getOtherClazz() {
		ClazzSet collection = new ClazzSet();
		for (Association item : this) {
			collection.add(item.getOtherClazz());
		}
		return collection;
	}

	/**
	 * Adds the.
	 *
	 * @param newValue the new value
	 * @return true, if successful
	 */
	@Override
	public boolean add(Association newValue) {
		if(newValue==null) {
			return false;
		}
		if (newValue.getOther() != null) {
			if (indexOf(newValue.getOther()) >= 0) {
				return false;
			}
		}
		return super.add(newValue);
	}

	/**
	 * Checks for name.
	 *
	 * @param otherValue the other value
	 * @return the association set
	 */
	public AssociationSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Association.PROPERTY_NAME, otherValue));
	}
	
	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public SimpleSet<Association> getNewList(boolean keyValue) {
		return new AssociationSet();
	}

	/**
	 * Contains.
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	@Override
	public boolean contains(Object o) {
		if (super.contains(o)) {
			return true;
		}
		if (o instanceof Association) {
			return super.contains(((Association) o).getOther());
		}
		return false;
	}

	/**
	 * To array.
	 *
	 * @return the association[]
	 */
	@Override
	public Association[] toArray() {
		return super.toArray(new Association[size()]);
	}
}
