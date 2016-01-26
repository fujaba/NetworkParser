package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class AssociationSet extends SimpleSet<Association>{
	public ClazzSet getClazzes() {
		ClazzSet collection = new ClazzSet();
		for(Association item : this) {
			collection.add(item.getClazz());
		}
		return collection;
	}
	
	public AssociationSet getOther() {
		AssociationSet collection = new AssociationSet();
		for(Association item : this) {
			collection.add(item.getOther());
		}
		return collection;
	}

	public ClazzSet getOtherClazz() {
		ClazzSet collection = new ClazzSet();
		for(Association item : this) {
			collection.add(item.getOtherClazz());
		}
		return collection;
	}
	
	@Override
	public AssociationSet filter(Condition<Association> newValue) {
		AssociationSet collection = new AssociationSet();
		filterItems( collection, newValue);
		return collection;
	}

	public AssociationSet hasName(String otherValue) {
		return filter(Association.NAME.equals(otherValue));
	}

//FIXME	getCardinality()
//	getInfo()
//	getName()
//	getType()
}
