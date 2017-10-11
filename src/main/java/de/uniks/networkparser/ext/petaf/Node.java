package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.interfaces.ObjectCondition;

public interface Node {
	public void connectToPeer();

	public boolean sendMessage(Message msg);

	public boolean sendMessageToPeers(Message msg);

	public NodeProxyType getType();

	public Node withType(NodeProxyType value);

	public void setSendTime(int bytes);

	public long getSendTime();

//	public boolean isReconnecting(SimpleList<Integer> seconds);

	public void setReceiveTime();

	public Long getReceiveTime();

	public Node withOnline(boolean value);

	public boolean isOnline();

	public String getKey();

	public boolean isSendable();

	public String getHistory();
	
	public Node withHistory(String value);

	public Node withFilter(ObjectCondition value);
	
	public ObjectCondition getFilter();

	public String getVersion();

	public Node withVersion(String value);

	public boolean close();

	public String getName();

	public Node withName(String name);

	public Space getSpace();
	
	public boolean isOwn();

	public Node withOwn(boolean isOwn);

}
