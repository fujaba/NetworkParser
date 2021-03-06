package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class AssociationSet extends SimpleSet<Association> {
	public static final AssociationSet EMPTY_SET = new AssociationSet();

	public AssociationSet() {
		this.withType(Association.class);
	}

	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for (Association item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}

	public AssociationSet getOther() {
		AssociationSet collection = new AssociationSet();
		for (Association item : this) {
			collection.add(item.getOther());
		}
		return collection;
	}

	public ClazzSet getOtherClazz() {
		ClazzSet collection = new ClazzSet();
		for (Association item : this) {
			collection.add(item.getOtherClazz());
		}
		return collection;
	}

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

	public AssociationSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Association.PROPERTY_NAME, otherValue));
	}
	
	@Override
	public SimpleSet<Association> getNewList(boolean keyValue) {
		return new AssociationSet();
	}

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

	@Override
	public Association[] toArray() {
		return super.toArray(new Association[size()]);
	}
}
