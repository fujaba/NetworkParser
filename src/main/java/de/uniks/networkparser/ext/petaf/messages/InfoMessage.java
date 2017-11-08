package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.ModelChange;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

public class InfoMessage extends ReceivingTimerTask {
	public static final String PROPERTY_PROXIES="proxies";
	public static final String PROPERTY_LASTID="history_id";
	
	public InfoMessage() {
		InfoMessage.props.add(PROPERTY_PROXIES, PROPERTY_LASTID);
	}
	
	public static InfoMessage create(Space space) {
		InfoMessage msg = new InfoMessage();
		msg.withSpace(space);
		return msg;
	}
	
	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof AcceptMessage == false ) {
			return null;
		}
		AcceptMessage message = (AcceptMessage) entity;
		Space space = message.getSpace();
		if(space != null) {
			if(PROPERTY_PROXIES.equalsIgnoreCase(attribute)) {
				SimpleList<NodeProxy> candidates = new SimpleList<NodeProxy>();
				SortedSet<NodeProxy> nodeProxies = space.getNodeProxies();
				for(NodeProxy proxy : nodeProxies) {
					if(proxy.isSendable()) {
						candidates.add(proxy);
					}
				}
				return candidates;
			}
			if(PROPERTY_LASTID.equalsIgnoreCase(attribute)) {
				ModelChange lastModelChange = space.getHistory().getLastModelChange();
				if(lastModelChange != null) {
					return lastModelChange.getKey();
				}
			}
		}
		return super.getValue(entity, attribute);
	}
	
//	@Override
//	public BaseItem getMessage() {
//		if(msg == null && space != null) {
//			SortedSet<NodeProxy> proxies = space.getNodeProxies();
//			
//			IdMap map = getInternMap(space);
//			Tokener tokener = space.getTokener();
//			EntityList list = tokener.newInstanceList();
//			PetaFilter filter = new PetaFilter().withTyp(PetaFilter.INFO);
//			for(NodeProxy proxy : proxies) {
//				list.add(map.encode(proxy, tokener, filter));
//			}
//			msg = list;
//		}
//		return super.getMessage();
//	}
	
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new InfoMessage();
	}
}
