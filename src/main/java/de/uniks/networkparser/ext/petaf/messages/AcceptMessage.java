package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedSet;

/**
 * Sending Connection Link with all Input Proxies and Filter.
 *
 * @author Stefan Lindel
 */
public class AcceptMessage extends ReceivingTimerTask {
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "accept";
	
	/** The Constant PROPERTY_PROXIES. */
	public static final String PROPERTY_PROXIES = "proxies";
	
	/** The Constant PROPERTY_MODEL. */
	public static final String PROPERTY_MODEL = "model";
	
	/** The Constant PROPERTY_MODELID. */
	public static final String PROPERTY_MODELID = "model_id";
	
	/** The Constant PROPERTY_MODELCLASS. */
	public static final String PROPERTY_MODELCLASS = "model_class";

	private String id;

	/**
	 * Instantiates a new accept message.
	 */
	public AcceptMessage() {
		this.type = PROPERTY_TYPE;
		AcceptMessage.props.add(PROPERTY_PROXIES, PROPERTY_MODELID, PROPERTY_MODELCLASS, PROPERTY_MODEL);
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute == null || !(entity instanceof AcceptMessage)) {
			return null;
		}
		AcceptMessage message = (AcceptMessage) entity;
		Space space = message.getSpace();
		if (space != null) {
			if (PROPERTY_MODELID.equalsIgnoreCase(attribute)) {
				NodeProxyModel modelProxy = space.getModel();
				if (modelProxy != null) {
					IdMap map = space.getMap();
					return map.getId(modelProxy.getModel(), false);
				}
				return null;
			}
			if (PROPERTY_MODELCLASS.equalsIgnoreCase(attribute)) {
				NodeProxyModel modelProxy = space.getModel();
				if (modelProxy != null) {
					Object model = modelProxy.getModel();
					if (model != null) {
						return model.getClass().getName();
					}
				}
				return null;
			}
			if (PROPERTY_PROXIES.equalsIgnoreCase(attribute)) {
				SortedSet<NodeProxy> nodeProxies = space.getNodeProxies();
				SimpleList<NodeProxy> candidates = new SimpleList<NodeProxy>();
				for (NodeProxy proxy : nodeProxies) {
					if (proxy.isSendable()) {
						candidates.add(proxy);
					}
				}
				return candidates;
			}
			if (PROPERTY_MODEL.equalsIgnoreCase(attribute)) {
				NodeProxyModel modelProxy = space.getModel();
				return modelProxy.getModel();
			}
		}
		return super.getValue(entity, attribute);
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	/* Add helper Variable to creating Objects */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute == null || !(entity instanceof AcceptMessage)) {
			return false;
		}
		AcceptMessage message = (AcceptMessage) entity;
		Space space = message.getSpace();
		if (space != null) {
			if (PROPERTY_MODELID.equalsIgnoreCase(attribute)) {
				this.id = "" + value;
				return true;
			}
			if (PROPERTY_MODELCLASS.equalsIgnoreCase(attribute)) {
				IdMap map = space.getMap();
				if (map == null) {
					return false;
				}
				if (map.getObject(this.id) != null) {
					/* Object exist in Map everything is ok */
					return true;
				}
				/* Check ClassName and NodeProxyModel for Candidates */
				String className = "" + value;
				SortedSet<NodeProxy> nodeProxies = space.getNodeProxies();
				SimpleList<NodeProxyModel> candidates = new SimpleList<NodeProxyModel>();
				for (NodeProxy proxy : nodeProxies) {
					if (proxy instanceof NodeProxyModel) {
						NodeProxyModel modelProxy = (NodeProxyModel) proxy;
						if (modelProxy.getId() == null) {
							Object modell = modelProxy.getModel();
							if (modell != null && modell.getClass().getName().equals(className)) {
								candidates.add(modelProxy);
							}
						}
					}
				}
				/* So I hope only one Candidate */
				if (candidates.size() != 1) {
					return false;
				}
				NodeProxyModel modelProxy = candidates.first();
				map.put(this.id, modelProxy.getModel(), false);
				/* get model from message deactive Notification */
				return true;
			}
			if (PROPERTY_MODEL.equalsIgnoreCase(attribute)) {
				/* Active Notification Model success decoding */
				space.withInit(true);
				return true;
			}
		}
		return super.setValue(entity, attribute, value, type);
	}

	/**
	 * Run task.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean runTask() {
		if (super.runTask()) {
			return true;
		}
		if (space == null) {
			return false;
		}

		NodeProxy proxy = space.updateProxy(this);
		if (proxy == null) {
			return false;
		}
		return true;
	}

	/**
	 * Creates the.
	 *
	 * @return the accept message
	 */
	public static AcceptMessage create() {
		AcceptMessage msg = new AcceptMessage();
		msg.withSendAnyHow(true);
		return msg;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		AcceptMessage acceptMessage = new AcceptMessage();
		if (!prototyp && this.space != null) {
			acceptMessage.withSpace(this.space);
		}
		return acceptMessage;
	}

	/**
	 * Checks if is sending to peers.
	 *
	 * @return true, if is sending to peers
	 */
	@Override
	public boolean isSendingToPeers() {
		return false;
	}
}