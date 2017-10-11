package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleList;

public class NodeProxyList implements Node {

	private SimpleList<NodeProxy> list=new SimpleList<NodeProxy>();
	
	public NodeProxyList with(NodeProxy...nodeProxies) {
		if(nodeProxies != null) {
			for(NodeProxy item : nodeProxies) {
				this.list.add(item);
			}
		}
		return this;
	}
	
	@Override
	public void connectToPeer() {
		NodeProxy first = this.list.first();
		if(first != null) {
			first.connectToPeer();
		}
	}

	@Override
	public boolean sendMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendMessageToPeers(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NodeProxyType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withType(NodeProxyType value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSendTime(int bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getSendTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setReceiveTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getReceiveTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withOnline(boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOnline() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSendable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withHistory(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withFilter(ObjectCondition value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectCondition getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withVersion(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node withName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Space getSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOwn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Node withOwn(boolean isOwn) {
		// TODO Auto-generated method stub
		return null;
	}
}
