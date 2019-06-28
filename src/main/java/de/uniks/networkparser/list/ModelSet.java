package de.uniks.networkparser.list;

import java.util.Collection;

public class ModelSet<V> extends SimpleSet<V> {
	public static final ModelSet<Object> EMPTY_SET = new ModelSet<Object>().withFlag(ModelSet.READONLY);

	/* Methods for Modelclasses */
	@SuppressWarnings("unchecked")
	public ModelSet(V... objects) {
		for (V obj : objects) {
			this.add(obj);
		}
	}

	public ModelSet() {
	}

	public ModelSet(Collection<V> objects) {
		this.addAll(objects);
	}

}
