package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;

public class NodeProxyModel extends NodeProxy{
	private Object root;
	
	public NodeProxyModel(Object root) {
		this.root = root;
		withType(NodeProxyType.IN);
	}

	@Override
	public String getKey() {
		if(space == null) {
			return null;
		}
		return this.space.getId(root);
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
