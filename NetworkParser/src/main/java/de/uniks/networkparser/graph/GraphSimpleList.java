package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleList;

public class GraphSimpleList<V> extends SimpleList<V>{
	@Override
	protected boolean checkValue(Object a, Object b) {
		if(!(a instanceof GraphMember)) {
			return a.equals(b);
		}
		String idA = ((GraphMember)a).getId();
		if(idA==null) {
			return a.equals(b);
		}
		String idB;
		if(b instanceof String) {
			idB = (String)b;
		}else {
			idB = ((GraphMember)b).getId();
		}
		return idA.equalsIgnoreCase(idB);
	}

}
