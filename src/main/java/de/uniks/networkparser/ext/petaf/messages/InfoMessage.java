package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.MessageTyp;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyFilter;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonArray;
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
		if(msg == null ) {
			SortedSet<NodeProxy> proxies = space.getNodeProxies();
			IdMap map = getInternMap(space);
			JsonArray jsonArray = new JsonArray();
			for(NodeProxy proxy : proxies) {
				jsonArray.with(map.encode(proxy, space.getTokener(), new NodeProxyFilter().withTyp(MessageTyp.INFO)));
			}
			msg = jsonArray;
		}
		return super.getMessage();
	}
}
