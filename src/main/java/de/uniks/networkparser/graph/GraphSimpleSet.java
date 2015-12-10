package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class GraphSimpleSet<V> extends SimpleSet<V>{
	@Override
	protected boolean checkValue(Object a, Object b) {
		if(!(a instanceof GraphMember)) {
			return a.equals(b);
		}
		String idA = ((GraphMember)a).getName();
		if(idA==null) {
			return a.equals(b);
		}
		String idB;
		if(b instanceof String) {
			idB = (String)b;
		}else {
			idB = ((GraphMember)b).getName();
		}
		return idA.equalsIgnoreCase(idB);
	}
}
