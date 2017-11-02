package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.PetaFilter;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SortedSet;

public class InfoMessage extends Message {
	private Space space;

	InfoMessage withSpace(Space space) {
		this.space = space;
		return this;
	}
	
	public static InfoMessage create(Space space) {
		return new InfoMessage().withSpace(space);
	}

	@Override
	public BaseItem getMessage() {
		if(msg == null && space != null) {
			SortedSet<NodeProxy> proxies = space.getNodeProxies();
			
			IdMap map = getInternMap(space);
			Tokener tokener = space.getTokener();
			EntityList list = tokener.newInstanceList();
			PetaFilter filter = new PetaFilter().withTyp(PetaFilter.INFO);
			for(NodeProxy proxy : proxies) {
				list.add(map.encode(proxy, tokener, filter));
			}
			msg = list;
		}
		return super.getMessage();
	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InfoMessage();
	}
}
