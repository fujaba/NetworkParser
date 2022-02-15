package de.uniks.networkparser.list;

import java.util.Collection;

/**
 * The Class ModelSet.
 *
 * @author Stefan
 * @param <V> the value type
 */
public class ModelSet<V> extends SimpleSet<V> {
	
	/** The Constant EMPTY_SET. */
	public static final ModelSet<Object> EMPTY_SET = new ModelSet<Object>().withFlag(ModelSet.READONLY);

	/**
	 * Instantiates a new model set.
	 *
	 * @param objects the objects
	 */
	/* Methods for Modelclasses */
	@SuppressWarnings("unchecked")
	public ModelSet(V... objects) {
		for (V obj : objects) {
			this.add(obj);
		}
	}

	/**
	 * Instantiates a new model set.
	 */
	public ModelSet() {
	}

	/**
	 * Instantiates a new model set.
	 *
	 * @param objects the objects
	 */
	public ModelSet(Collection<V> objects) {
		this.addAll(objects);
	}

}
