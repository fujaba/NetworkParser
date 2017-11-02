package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;

/**
 * Sending Connection Link with all Input Proxies and Filter 
 * @author Stefan Lindel
 */
public class AcceptMessage extends ReceivingTimerTask {
	public static final String PROPERTY_TYPE="accept";
	public static final String PROPERTY_PROXIES="proxies";
	public static final String PROPERTY_MODEL="model";
	
	public AcceptMessage() {
		AcceptMessage.props.add(PROPERTY_PROXIES, PROPERTY_MODEL);
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
				return space.getNodeProxies();
			}
			if(PROPERTY_MODEL.equalsIgnoreCase(attribute)) {
				NodeProxyModel modelProxy = space.getModel();
				return modelProxy.getModell();
			}
		}
		return super.getValue(entity, attribute);
	}
	
	@Override
	public boolean runTask() throws Exception {
		if(super.runTask() ) {
			return true;
		}
		if(space == null) {
			return false;
		}
		
		NodeProxy proxy = space.updateProxy(this);
		if (proxy == null) {
			return false;
		}
		// get list of proxies from message
		space.suspendNotification();
		
//			JsonObject data = getData();
//			JsonArray jsonArray = data.getJsonArray(AcceptTaskSend.TYP_PROXIES);
//			JsonObject proxyData;
//			JsonObject properties;
//			String myProxyName=world.getName();
//			world.clearProxies();
//
//			for (int i = 0; i < jsonArray.size(); i++) {
//				proxyData = jsonArray.getJSONObject(i);
//				properties = (JsonObject) proxyData.get(JsonTokener.PROPS);
//
//				if (properties != null) {
//					if(!properties.has(NodeProxy.PROPERTY_ONLINE)){
//						continue;
//					}
//					boolean isOnline = properties.getBoolean(NodeProxy.PROPERTY_ONLINE);
//					if (isOnline) {
//						NodeProxy newProxy = (NodeProxy) getWorld().getMap()
//								.decode(proxyData);
// 						if (world.getNodeProxies().contains(newProxy)) {
//							// remove old entry and use the new one to ensure
//							// that jsonid matches
// 							world.getNodeProxies().remove(newProxy);
//						}
// 						world.with(newProxy);
//					}else if(myProxyName.equals(properties.getString((NodeProxy.PROPERTY_NAME)))){
//						NodeProxy newProxy = (NodeProxy) getWorld().getMap()
//								.decode(proxyData);
//					}
//				}
//			}
//			ListOfTalk origTalkList = getWorld().getTalkList();
//			Set<Talk> talks = origTalkList.getTalks();
//			for (Talk t : talks)
//			{
//			   origTalkList.removeTalk(t);
//			}
//
//			JsonObject dataModel = data.getJsonObject("msg");
//			dataModel = dataModel.getJsonObject(AcceptTaskSend.TYP_MODEL);
//
//			Integer longId = this.msg.getInt(NodeProxy.PROPERTY_HISTORY);
//			ModelHistory history = getWorld().getHistory();
//
//			ModelChange change=history.createChange(longId, proxy.getName(), dataModel);
//			getWorld().getHistory().addFirstHistory(change);
//			getWorld().resetNotification();
//			
//			IdMap map = getWorld().getMap();
//			Conference conference = getWorld().getConference();
//			map.decode(dataModel, conference, null);

			
//ONLY CONFNET		getWorld().setModellInit(true);
		// Send myNodes to all Node
		return true;
	}
	
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof AcceptMessage == false ) {
			return false;
		}
		return super.setValue(entity, attribute, value, type);
	}
	
	
	public static AcceptMessage create() {
		AcceptMessage msg = new AcceptMessage();
		msg.withSendAnyHow(true);
		return msg; 
	}
	

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new AcceptMessage();
	}

	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}
}