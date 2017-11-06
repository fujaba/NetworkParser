package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;

public class NodeProxyModel extends NodeProxy {
	private Object root;
	private String id;
	
	public NodeProxyModel(Object root) {
		this.root = root;
		withType(NodeProxyType.IN);
	}

	@Override
	public String getKey() {
		if(space == null) {
			return null;
		}
		return getId();
	}
	
	public String getId() {
		if(this.id != null) {
			return this.id;
		}
		this.id = this.space.getKey(root);
		return id;
	}

	@Override
	public boolean close() {
		return false;
	}

	public Object getModell() {
		return root;
	}

	@Override
	protected boolean initProxy() {
		return true;
	}

	@Override
	public boolean isSendable() {
		return false;
	}
	@Override
	public Object getSendableInstance(boolean reference) {
		return new NodeProxyModel(null);
	}
}
