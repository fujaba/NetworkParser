package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * Local is a Proxy for testing.
 *
 * @author Stefan
 */
public class NodeProxyLocal extends NodeProxy {
	
	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "id";
	private String id;
	private ObjectCondition listener;

	protected boolean sending(Message msg) {
		boolean result = super.sending(msg);
		if (listener != null) {
			String blob = this.space.convertMessage(msg);
			SimpleEvent evt = new SimpleEvent(msg, PROPERTY_SEND, null, blob);
			return listener.update(evt);
		}
		return result;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getID() {
		return id;
	}

	/**
	 * With listener.
	 *
	 * @param value the value
	 * @return the node proxy local
	 */
	public NodeProxyLocal withListener(ObjectCondition value) {
		this.listener = value;
		return this;
	}

	/**
	 * With ID.
	 *
	 * @param value the value
	 * @return the node proxy local
	 */
	public NodeProxyLocal withID(String value) {
		String oldValue = value;
		this.id = value;
		firePropertyChange(PROPERTY_ID, oldValue, value);
		return this;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return id;
	}

	/**
	 * Gets the value.
	 *
	 * @param element the element
	 * @param attrName the attr name
	 * @return the value
	 */
	@Override
	public Object getValue(Object element, String attrName) {
		if (element instanceof NodeProxyLocal) {
			NodeProxyLocal nodeProxy = (NodeProxyLocal) element;
			if (PROPERTY_ID.equals(attrName)) {
				return nodeProxy.getID();
			}
		}
		return super.getValue(element, attrName);
	}

	/**
	 * Sets the value.
	 *
	 * @param element the element
	 * @param attrName the attr name
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if (element instanceof NodeProxyLocal) {
			NodeProxyLocal nodeProxy = (NodeProxyLocal) element;
			if (PROPERTY_ID.equals(attrName)) {
				nodeProxy.withID((String) value);
				return true;
			}
		}
		return super.setValue(element, attrName, value, type);
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean close() {
		return true;
	}

	@Override
	protected boolean startProxy() {
		this.type = NodeProxy.TYPE_OUT;
		return true;
	}

	/**
	 * Checks if is sendable.
	 *
	 * @return true, if is sendable
	 */
	@Override
	public boolean isSendable() {
		return false;
	}

	/**
	 * Creates the.
	 *
	 * @param listener the listener
	 * @return the node proxy local
	 */
	public static NodeProxyLocal create(ObjectCondition listener) {
		NodeProxyLocal proxy = new NodeProxyLocal();
		proxy.withListener(listener);
		return proxy;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param reference the reference
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new NodeProxyLocal();
	}
}
