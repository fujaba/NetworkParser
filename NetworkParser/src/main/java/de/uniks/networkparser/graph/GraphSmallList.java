package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSmallList;

public class GraphSmallList<V> extends SimpleSmallList<V>{
	private static final long serialVersionUID = 1L;
	
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
