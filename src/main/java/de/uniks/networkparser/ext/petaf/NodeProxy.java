package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.list.SimpleList;

public abstract class NodeProxy extends SendableItem implements Comparable<NodeProxy>, SendableEntityCreatorNoIndex {
	public static final String TYPE_IN="IN";
	public static final String TYPE_OUT="OUT";
	public static final String TYPE_INOUT="INOUT";

	public static int BUFFER = 100 * 1024;
	public static final String PROPERTY_SEND = "sendtime";
	public static final String PROPERTY_RECEIVE = "receivetime";
	public static final String PROPERTY_HISTORY = "history";
	public static final String PROPERTY_NODES = "nodes";
	public static final String PROPERTY_FILTER = "filter";
	public static final String PROPERTY_ONLINE = "online";
	public static final String PROPERTY_VERSION = "version";
	public static final String PROPERTY_TYP = "typ";
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_ID = "id";

	protected PropertyList propertyId = PropertyList.create(PROPERTY_ID);
	protected PropertyList propertyUpdate = PropertyList.create(PROPERTY_ID, PROPERTY_HISTORY, PROPERTY_FILTER, PROPERTY_SEND);
	protected PropertyList propertyInfo = PropertyList.create(PROPERTY_ID, PROPERTY_SEND, PROPERTY_RECEIVE, PROPERTY_HISTORY, PROPERTY_FILTER, PROPERTY_VERSION);
	protected PropertyList property = PropertyList.create(PROPERTY_ID, PROPERTY_SEND, PROPERTY_RECEIVE, PROPERTY_ONLINE,
			PROPERTY_NODES, PROPERTY_HISTORY, PROPERTY_FILTER, PROPERTY_VERSION);

	protected String type;
	protected long sendtime;
	protected long receivetime;
	protected long lastSendTryTime;
	protected long receiveBytes;
	protected long sendBytes; // Full bytes
	protected int lastSendCount; // Count of success sending
	protected String version; // Runtimeversion of App
	protected boolean online; // Boolean if last send is success
	protected String history; // Hashcode of last Message
	protected ObjectCondition filter; // Filter of World
	protected long no;
	protected Space space;
	protected String name;
	protected NodeProxy nextNode;	// NextPeer for MyNodes

	public String[] getUpdateProperties() {
		return propertyUpdate.getList();
	}

	public String[] getInfoProperties() {
		return propertyInfo.getList();
	}

	@Override
	public String[] getProperties() {
		return property.getList();
	}

	public String[] getIDProperties() {
		return propertyId.getList();
	}


	public void connectToPeer() {
		sendMessage(ConnectMessage.create());
	}

	public void connectInfo() {
		sendMessage(new InfoMessage());
	}

	public boolean sendMessage(Message msg) {
		if (this.space != null) {
			return this.space.sendMessage(msg, false, this);
		}
		return this.sending(msg);
	}

	public boolean sendPing() {
		InfoMessage message=new InfoMessage();
		return sendMessage(message);
	}

	public boolean sendMessageToPeers(Message msg) {
		return this.space.sendMessageToPeers(msg, this);
	}

	public boolean isValid() {
		return true;
	}
	
	protected boolean sending(Message msg) {
		if(this.isValid() == false) {
			return true;
		}
		msg.withAddToReceived(this);
		this.lastSendTryTime = System.currentTimeMillis();
		if(this.space!= null) {
			this.space.updateNetwork(TYPE_OUT, this);
		}
		return false;
	}

	public String getType() {
		return type;
	}

	public void updateReceive(int len, boolean setOnline) {
		this.receivetime = System.currentTimeMillis();
		this.receiveBytes += len;
		if(setOnline) {
			this.withOnline(true);
		}
	}

	public NodeProxy withType(String value) {
		// if output is not configured, we don't allow OUT or INOUT as value...
		// if(value.isInput()){
		// }
		this.type = value;
		return this;
	}

	public long getNewMsgNo() {
		this.no++;
		if(no<0) {
			no =0;
		}
		return no;
	}
	
	public void setSendTime(int bytes) {
//		Long oldValue = sendtime;
		this.sendtime = System.currentTimeMillis();
//		firePropertyChange(PROPERTY_SEND, oldValue, sendtime);
		this.lastSendCount = 0;
	}

	public long getSendTime() {
		return this.sendtime;
	}

	public boolean isReconnecting(SimpleList<Integer> seconds) {
		if (isOnline() || seconds.size() < 1) {
			return false;
		}
		if (lastSendCount >= seconds.size()) {
			lastSendCount = seconds.size() - 1;
		}
		int time = seconds.get(lastSendCount);
		if (time == Space.DISABLE) {
			return false;
		}
		boolean result = (isOnline() == false) && (System.currentTimeMillis() - lastSendTryTime) > time;
		if (result) {
			this.lastSendCount++;
		}
		return result;
	}

	public void setReceiveTime() {
//		Long oldValue = receivetime;
		this.receivetime = System.currentTimeMillis();
//		firePropertyChange(PROPERTY_RECEIVE, oldValue, receivetime);
	}

	public Long getReceiveTime() {
		return receivetime;
	}

	public NodeProxy withOnline(boolean value) {
		boolean oldValue = this.online;
		this.online = value;
		firePropertyChange(PROPERTY_ONLINE, oldValue, value);
		return this;
	}

	public boolean isOnline() {
		return online;
	}

	public abstract boolean isSendable();

	public int compareTo(NodeProxy o) {
		if(getKey() == null) {
			if(o.getKey() == null) {
				return 0;
			}
			return 1;
		}
		if(o == null || o.getKey() == null) {
			if(getKey() == null) {
				return 0;
			}
			return -1;
		}
		return getKey().compareTo(o.getKey());
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NodeProxy) {
			return compareTo((NodeProxy) obj) ==0;
		}
		return super.equals(obj);
	}

	public String getHistory() {
		return history;
	}

	public NodeProxy withHistory(String value) {
		String oldValue = this.history;
		this.history = value;
		firePropertyChange(PROPERTY_HISTORY, oldValue, value);
		return this;
	}

	public NodeProxy withFilter(ObjectCondition value) {
		if (value != null && !value.equals(this.filter)) {
			this.filter = value;
		}
		return this;
	}

	public ObjectCondition getFilter() {
		return filter;
	}

	public boolean filter(Object value) {
		if(filter != null) {
			return filter.update(value);
		}
		return true;
	}

	public String getVersion() {
		return version;
	}

	public NodeProxy withVersion(String value) {
		String oldValue = this.version;
		this.version = value;
		firePropertyChange(PROPERTY_VERSION, oldValue, value);
		return this;
	}

	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if (element instanceof NodeProxy == false) {
			return false;
		}
		NodeProxy nodeProxy = (NodeProxy) element;
		if (PROPERTY_SEND.equals(attrName)) {
			long oldValue = nodeProxy.sendtime;
			nodeProxy.sendtime = Long.valueOf("" + value);
			firePropertyChange(PROPERTY_SEND, oldValue, nodeProxy.sendtime);
			return true;
		}
		if (PROPERTY_RECEIVE.equals(attrName)) {
			long oldValue = nodeProxy.receivetime;
			nodeProxy.receivetime = Long.valueOf("" + value);
			firePropertyChange(PROPERTY_RECEIVE, oldValue, nodeProxy.receivetime);
			return true;
		}
		if (PROPERTY_HISTORY.equals(attrName)) {
			nodeProxy.withHistory("" + value);
			return true;
		}
		if (PROPERTY_FILTER.equals(attrName)) {
			nodeProxy.withFilter((ObjectCondition) value);
			return true;
		}
		if (PROPERTY_ONLINE.equals(attrName)) {
			nodeProxy.withOnline(Boolean.valueOf("" + value));
			return true;
		}
		if (PROPERTY_VERSION.equals(attrName)) {
			nodeProxy.withVersion("" + value);
			return true;
		}
		if (PROPERTY_TYP.equals(attrName)) {
			nodeProxy.withType("" + value);
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(Object element, String attrName) {
		if (element instanceof NodeProxy == false) {
			return null;
		}
		NodeProxy nodeProxy = (NodeProxy) element;
		if (PROPERTY_SEND.equals(attrName)) {
			return nodeProxy.getSendTime();
		}
		if (PROPERTY_RECEIVE.equals(attrName)) {
			return nodeProxy.getReceiveTime();
		}
		if (PROPERTY_HISTORY.equals(attrName)) {
			return nodeProxy.getHistory();
		}
		if (PROPERTY_FILTER.equals(attrName)) {
			return nodeProxy.getFilter();
		}
		if (PROPERTY_ONLINE.equals(attrName)) {
			return nodeProxy.isOnline();
		}
		if (PROPERTY_VERSION.equals(attrName)) {
			return nodeProxy.getVersion();
		}
		if (PROPERTY_TYP.equals(attrName)) {
			return nodeProxy.getType();
		}
		if(PROPERTY_ID.equals(attrName)) {
			return nodeProxy.getKey();
		}
		return null;
	}

	public abstract boolean close();

	public NodeProxy initSpace(Space value) {
		if (value == this.space) {
			return this;
		}
		Space oldValue = this.space;
		if (null != this.space) {
			this.space = null;
			oldValue.removeProxy(this);
		}
		this.space = value;
		initProxy();
		if (null != value) {
			value.with(this);
		}
		firePropertyChange(PROPERTY_NODES, oldValue, value);
		return this;
	}

	public String getName() {
		return name;
	}

	public NodeProxy withName(String name) {
		this.name = name;
		return this;
	}

	public Space getSpace() {
		return space;
	}

	protected abstract boolean initProxy();

	public NodeProxy next() {
		return this.nextNode;
	}

	public NodeProxy setNextMyNode(NodeProxy nextNode) {
		this.nextNode = nextNode;
		if(nextNode == null) {
			return this;
		}
		nextNode.setNextMyNode(null);
		return nextNode;
	}

	public TaskExecutor getExecutor() {
		if(this.space != null) {
			return this.space.getExecutor();
		}
		//Fallback
		return new SimpleExecutor();
	}

	public abstract String getKey();
	
    public static boolean isInput(String value) {
        return (value != null && value.indexOf(TYPE_IN)>=0);
    }

    public static boolean isOutput(String value) {
    	return (value != null && value.indexOf(TYPE_OUT)>=0);
    }
}
