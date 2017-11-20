package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonTokener;

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

	@Override
	public NodeProxy initSpace(Space space) {
		super.initSpace(space);

		// serialize model
		IdMap map = space.getMap();
		map.put("root", getModell(), true);

//		Object modell = getModell();
//		BaseItem value = this.space.encode(modell, null);
//		String data = value.toString();

		return this;
	}
}
