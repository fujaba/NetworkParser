package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;

/**
 * Local is a Proxy for testing 
 * @author Stefan
 */
public class NodeProxyLocal extends NodeProxy {
	public static final String PROPERTY_ID = "id";
	private String id;
	private NodeProxyListener listener;

	protected boolean sending(Message msg) {
		boolean result = super.sending(msg);
		if(listener != null) {
			String blob = this.space.convertMessage(msg);
			listener.send(msg, blob);
		}
		return result;
	}
	public String getID()
	{
		return id;
	}

	public NodeProxyLocal withListener(NodeProxyListener value) {
		this.listener = value;
		return this;
	}
	public NodeProxyLocal withID(String value)
	{
		String oldValue = value;
		this.id = value;
		firePropertyChange(PROPERTY_ID, oldValue, value);
		return this;
	}
	
	@Override
	public String getKey() {
		return id;
	}
	
	@Override
	public Object getValue(Object element, String attrName) {
		if(element instanceof NodeProxyLocal ) {
			NodeProxyLocal nodeProxy = (NodeProxyLocal) element;
			if (PROPERTY_ID.equals(attrName)) {
				return nodeProxy.getID();
			}
		}
		return super.getValue(element, attrName);
	}
	
	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if(element instanceof NodeProxyLocal ) {
			NodeProxyLocal nodeProxy = (NodeProxyLocal) element;
			if (PROPERTY_ID.equals(attrName)) {
				nodeProxy.withID((String) value);
				return true;
			}
		}
		return super.setValue(element, attrName, value, type);
	}
	
	@Override
	public boolean close() {
		return true;
	}

	@Override
	protected boolean initProxy() {
		this.type = NodeProxyType.OUT;
		return true;
	}

	@Override
	public boolean isSendable() {
		return false;
	}
	
	public static NodeProxyLocal create(NodeProxyListener listener) {
		NodeProxyLocal proxy = new NodeProxyLocal();
		proxy.withListener(listener);
		return proxy;
	}
	
	@Override
	public Object getSendableInstance(boolean reference) {
		return new NodeProxyLocal();
	}
}
