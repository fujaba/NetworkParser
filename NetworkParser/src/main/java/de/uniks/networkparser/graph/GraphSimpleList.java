package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleList;

public class GraphSimpleList<V> extends SimpleList<V>{
	@Override
	protected boolean checkValue(Object a, Object b) {
		String idA = ((GraphMember)a).getId();
		String idB;
		if(b instanceof String) {
			idB = (String)b;
		}else {
			idB = ((GraphMember)b).getId();
		}
		return idA.equalsIgnoreCase(idB);
	}

}
