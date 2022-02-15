package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.SendableItem;
/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.list.SimpleList;

/**
 * Abstrakt Class for Proxy.
 *
 * @author Stefan Lindel
 */
public abstract class NodeProxy extends SendableItem implements Comparable<NodeProxy>, SendableEntityCreatorNoIndex {
	
	/** The Constant TYPE_IN. */
	public static final String TYPE_IN = "IN";
	
	/** The Constant TYPE_OUT. */
	public static final String TYPE_OUT = "OUT";
	
	/** The Constant TYPE_INOUT. */
	public static final String TYPE_INOUT = "INOUT";

	/** The buffer. */
	public static int BUFFER = 100 * 1024;
	
	/** The Constant PROPERTY_SEND. */
	public static final String PROPERTY_SEND = "sendtime";
	
	/** The Constant PROPERTY_RECEIVE. */
	public static final String PROPERTY_RECEIVE = "receivetime";
	
	/** The Constant PROPERTY_HISTORY. */
	public static final String PROPERTY_HISTORY = "history";
	
	/** The Constant PROPERTY_NODES. */
	public static final String PROPERTY_NODES = "nodes";
	
	/** The Constant PROPERTY_FILTER. */
	public static final String PROPERTY_FILTER = "filter";
	
	/** The Constant PROPERTY_ONLINE. */
	public static final String PROPERTY_ONLINE = "online";
	
	/** The Constant PROPERTY_VERSION. */
	public static final String PROPERTY_VERSION = "version";
	
	/** The Constant PROPERTY_TYP. */
	public static final String PROPERTY_TYP = "typ";
	
	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";
	
	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "id";

	protected PropertyList propertyId = PropertyList.create(PROPERTY_ID);
	protected PropertyList propertyUpdate = PropertyList.create(PROPERTY_ID, PROPERTY_HISTORY, PROPERTY_FILTER,
			PROPERTY_SEND);
	protected PropertyList propertyInfo = PropertyList.create(PROPERTY_ID, PROPERTY_SEND, PROPERTY_RECEIVE,
			PROPERTY_HISTORY, PROPERTY_FILTER, PROPERTY_VERSION);
	protected PropertyList property = PropertyList.create(PROPERTY_ID, PROPERTY_SEND, PROPERTY_RECEIVE, PROPERTY_ONLINE,
			PROPERTY_NODES, PROPERTY_HISTORY, PROPERTY_FILTER, PROPERTY_VERSION);

	protected String type;
	protected long sendtime;
	protected long receivetime;
	protected long lastSendTryTime;
	protected long receiveBytes;
	protected long sendBytes; /* Full bytes */
	protected int lastSendCount; /* Count of success sending */
	protected String version; /* Runtimeversion of App */
	protected boolean online; /* Boolean if last send is success */
	protected String history; /* Hashcode of last Message */
	protected ObjectCondition filter; /* Filter of World */
	protected long no;
	protected Space space;
	protected String name;
	protected NodeProxy nextNode; /* NextPeer for MyNodes */

	/**
	 * Gets the update properties.
	 *
	 * @return the update properties
	 */
	public String[] getUpdateProperties() {
		return propertyUpdate.getList();
	}

	/**
	 * Gets the info properties.
	 *
	 * @return the info properties
	 */
	public String[] getInfoProperties() {
		return propertyInfo.getList();
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return property.getList();
	}

	/**
	 * Gets the ID properties.
	 *
	 * @return the ID properties
	 */
	public String[] getIDProperties() {
		return propertyId.getList();
	}

	/**
	 * Connect to peer.
	 */
	public void connectToPeer() {
		sendMessage(ConnectMessage.create());
	}

	/**
	 * Connect info.
	 */
	public void connectInfo() {
		sendMessage(new InfoMessage());
	}

	/**
	 * Send message.
	 *
	 * @param msg the msg
	 * @return true, if successful
	 */
	public boolean sendMessage(Message msg) {
		if (this.space != null) {
			return this.space.sendMessage(msg, false, this);
		}
		return this.sending(msg);
	}

	/**
	 * Send ping.
	 *
	 * @return true, if successful
	 */
	public boolean sendPing() {
		InfoMessage message = new InfoMessage();
		return sendMessage(message);
	}

	/**
	 * Send message to peers.
	 *
	 * @param msg the msg
	 * @return true, if successful
	 */
	public boolean sendMessageToPeers(Message msg) {
		if(this.space != null) {
			return this.space.sendMessageToPeers(msg, this);
		}
		return false;
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return true;
	}

	protected boolean sending(Message msg) {
		if (!this.isValid()) {
			return true;
		}
		msg.withAddToReceived(this);
		this.lastSendTryTime = System.currentTimeMillis();
		if (this.space != null) {
			this.space.updateNetwork(TYPE_OUT, this);
		}
		return false;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Update receive.
	 *
	 * @param len the len
	 * @param setOnline the set online
	 */
	public void updateReceive(int len, boolean setOnline) {
		this.receivetime = System.currentTimeMillis();
		this.receiveBytes += len;
		if (setOnline) {
			this.withOnline(true);
		}
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
	public NodeProxy withType(String value) {
		/* if output is not configured, we don't allow OUT or INOUT as value... */
		this.type = value;
		return this;
	}

	/**
	 * Gets the new msg no.
	 *
	 * @return the new msg no
	 */
	public long getNewMsgNo() {
		this.no++;
		if (no < 0) {
			no = 0;
		}
		return no;
	}

	/**
	 * Sets the send time.
	 *
	 * @param bytes the new send time
	 */
	public void setSendTime(int bytes) {
		Long oldValue = sendtime;
		this.sendtime = System.currentTimeMillis();
		firePropertyChange(PROPERTY_SEND, oldValue, sendtime);
		this.lastSendCount = 0;
	}

	/**
	 * Gets the send time.
	 *
	 * @return the send time
	 */
	public long getSendTime() {
		return this.sendtime;
	}

	/**
	 * Checks if is reconnecting.
	 *
	 * @param seconds the seconds
	 * @return true, if is reconnecting
	 */
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
		boolean result = !isOnline() && (System.currentTimeMillis() - lastSendTryTime) > time;
		if (result) {
			this.lastSendCount++;
		}
		return result;
	}

	/**
	 * Sets the receive time.
	 */
	public void setReceiveTime() {
		Long oldValue = receivetime;
		this.receivetime = System.currentTimeMillis();
		firePropertyChange(PROPERTY_RECEIVE, oldValue, receivetime);
	}

	/**
	 * Gets the receive time.
	 *
	 * @return the receive time
	 */
	public Long getReceiveTime() {
		return receivetime;
	}

	/**
	 * With online.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
	public NodeProxy withOnline(boolean value) {
		boolean oldValue = this.online;
		this.online = value;
		firePropertyChange(PROPERTY_ONLINE, oldValue, value);
		return this;
	}

	/**
	 * Checks if is online.
	 *
	 * @return true, if is online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Checks if is sendable.
	 *
	 * @return true, if is sendable
	 */
	public abstract boolean isSendable();

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	public int compareTo(NodeProxy o) {
		if (getKey() == null) {
			if (o.getKey() == null) {
				return 0;
			}
			return 1;
		}
		if (o == null || o.getKey() == null) {
			if (getKey() == null) {
				return 0;
			}
			return -1;
		}
		return getKey().compareTo(o.getKey());
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NodeProxy) {
			return compareTo((NodeProxy) obj) == 0;
		}
		return super.equals(obj);
	}

	/**
	 * Gets the history.
	 *
	 * @return the history
	 */
	public String getHistory() {
		return history;
	}

	/**
	 * Gets the history no.
	 *
	 * @return the history no
	 */
	public Integer getHistoryNo() {
		try {
			return Integer.valueOf(history);
		} catch (Exception e) {
			// DO Nothing
		}
		return null;
	}

	/**
	 * With history.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
	public NodeProxy withHistory(String value) {
		String oldValue = this.history;
		this.history = value;
		firePropertyChange(PROPERTY_HISTORY, oldValue, value);
		return this;
	}

	/**
	 * With filter.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
	public NodeProxy withFilter(ObjectCondition value) {
		if (value != null && !value.equals(this.filter)) {
			this.filter = value;
		}
		return this;
	}

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public ObjectCondition getFilter() {
		return filter;
	}

	/**
	 * Filter.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean filter(Object value) {
		if (filter != null) {
			return filter.update(value);
		}
		return true;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * With version.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
	public NodeProxy withVersion(String value) {
		String oldValue = this.version;
		this.version = value;
		firePropertyChange(PROPERTY_VERSION, oldValue, value);
		return this;
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
		if (!(element instanceof NodeProxy)) {
			return false;
		}
		NodeProxy nodeProxy = (NodeProxy) element;
		if (SendableEntityCreator.REMOVE_YOU.equalsIgnoreCase(type)) {
			return nodeProxy.close();
		}
		if (PROPERTY_SEND.equals(attrName)) {
			long oldValue = nodeProxy.sendtime;
			nodeProxy.sendtime = Long.parseLong("" + value);
			firePropertyChange(PROPERTY_SEND, oldValue, nodeProxy.sendtime);
			return true;
		}
		if (PROPERTY_RECEIVE.equals(attrName)) {
			long oldValue = nodeProxy.receivetime;
			nodeProxy.receivetime = Long.parseLong("" + value);
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

	/**
	 * Gets the value.
	 *
	 * @param element the element
	 * @param attrName the attr name
	 * @return the value
	 */
	@Override
	public Object getValue(Object element, String attrName) {
		if (!(element instanceof NodeProxy)) {
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
		if (PROPERTY_ID.equals(attrName)) {
			return nodeProxy.getKey();
		}
		return null;
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	public abstract boolean close();

	/**
	 * Inits the space.
	 *
	 * @param value the value
	 * @return the node proxy
	 */
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
		startProxy();
		if (null != value) {
			value.with(this);
		}
		firePropertyChange(PROPERTY_NODES, oldValue, value);
		return this;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the node proxy
	 */
	public NodeProxy withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets the space.
	 *
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	protected abstract boolean startProxy();

	/**
	 * Next.
	 *
	 * @return the node proxy
	 */
	public NodeProxy next() {
		return this.nextNode;
	}

	/**
	 * Sets the next my node.
	 *
	 * @param nextNode the next node
	 * @return the node proxy
	 */
	public NodeProxy setNextMyNode(NodeProxy nextNode) {
		this.nextNode = nextNode;
		if (nextNode == null) {
			return this;
		}
		nextNode.setNextMyNode(null);
		return nextNode;
	}

	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public TaskExecutor getExecutor() {
		if (this.space != null) {
			return this.space.getExecutor();
		}
		/* Fallback */
		return new SimpleExecutor();
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public abstract String getKey();

	/**
	 * Checks if is input.
	 *
	 * @param value the value
	 * @return true, if is input
	 */
	public static boolean isInput(String value) {
		return (value != null && value.indexOf(TYPE_IN) >= 0);
	}

	/**
	 * Checks if is output.
	 *
	 * @param value the value
	 * @return true, if is output
	 */
	public static boolean isOutput(String value) {
		return (value != null && value.indexOf(TYPE_OUT) >= 0);
	}
}
