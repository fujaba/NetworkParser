package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.network.Message;
import de.uniks.networkparser.ext.petaf.network.MessageTyp;
import de.uniks.networkparser.ext.petaf.network.NodeProxy;
import de.uniks.networkparser.ext.petaf.network.NodeProxyFilter;
import de.uniks.networkparser.ext.petaf.network.BasicSpace;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.list.SortedSet;

public class InfoMessage extends Message {
	private BasicSpace space;

	InfoMessage withSpace(BasicSpace space) {
		this.space = space;
		return this;
	}
	
	public static InfoMessage create(BasicSpace space) {
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
