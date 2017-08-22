package de.uniks.networkparser.ext.petaf.network;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.ext.petaf.filter.ProxyFilter;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.ext.petaf.messages.util.ChangeMessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.ConnectMessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.MessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.PingMessageCreator;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyLocal;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

/**
 * BasicSpace for Replication
 * @author Stefan
 *
 */
public class BasicSpace extends SendableItem implements ObjectCondition {
	public static final String PROPERTY_MODELROOT="root";
//	public static final String PROPERTY_NODES="nodes";
	public static final String PROPERTY_HISTORY="history";
	public static final String PROPERTY_PROXY="proxies";
	public static final String PROPERTY_PATH="path";
	public static final String PROPERTY_NAME="name";
	
	private SortedSet<NodeProxy> proxies=new SortedSet<NodeProxy>(true);
	private ByteConverter converter;
	private ModelHistory history = null;
	private String path = "";
	private ProxyFilter filter = new ProxyFilter();
	private int peerCount=2;
	static final int DISABLE=0;
	static final int MINUTE=60;
	static final int TENMINUTE=6000;
	static final int THIRTYMINUTE=30000;
	private String name;
	/** Time for Try to Reconnect Clients every x Seconds (Default:5x1m, 5x10m, 30m). Set Value to 0 for disable	 */
	private SimpleList<Integer> tryReconnectTimeSecond=new SimpleList<Integer>()
			.with(MINUTE,MINUTE,MINUTE,MINUTE,MINUTE)
			.with(TENMINUTE,TENMINUTE,TENMINUTE,TENMINUTE,TENMINUTE)
			.with(THIRTYMINUTE, DISABLE);
	private IdMap map = null;
//MOVE TO SUBCLASS	private TaskExecutor executor=new TaskExecutor();
	private Tokener tokener;

	IdMap getInternMap() {
		if(map==null){
			map = new IdMap();
			map.with(new MessageCreator(), new ChangeMessageCreator(), new PingMessageCreator(), new NodeProxyTCP(), new NodeProxyLocal(), new ConnectMessageCreator(), new NodeProxyModel(null));
		}
		return map;
	}
	
	public BasicSpace withModelRoot(NodeProxyModel modelRoot) {
		with(modelRoot);
		return this;
	}

	public BasicSpace with(NodeProxy... values) {
		if(values == null) {
			return this;
		}
		for(NodeProxy proxy : values) {
			if (proxy != null) {
				boolean changed = this.proxies.add(proxy);

				if (changed) {
					proxy.initSpace(this);
					firePropertyChange(PROPERTY_PROXY, null, proxy);
				}
			}
		}
		return this;
	}
	
	public ModelHistory getHistory() {
		if(history==null){
			history = new ModelHistory().withSpace(this);
		}
		return history;
	}

	public BasicSpace withHistory(ModelHistory value) {
		if (value == this.history) {
			return this;
		}
		ModelHistory oldValue = this.history;
		if (null != oldValue) {
			this.history = null;
			oldValue.withSpace(null);
		}
		this.history = value;
		if (null != value) {
			value.withSpace(this);
		}
		firePropertyChange(PROPERTY_HISTORY, oldValue, value);
		return this;
	}
	
	public void close() {
		for(NodeProxy proxy : proxies) {
			if(NodeProxyType.IN.equals(proxy.getType())) {
				proxy.close();
			}
		}
//		executor.shutdown();
	}
	
	public Entity getReplicationInfo() {
		Tokener tokener = getTokener();
		Entity result = tokener.newInstance();
		result.put(PROPERTY_NAME, this.name);
		EntityList proxies = tokener.newInstanceList();
		MapEntity entity = new MapEntity(getInternMap());
		for(NodeProxy proxy : this.proxies) {
			if(NodeProxyType.isOutput(proxy.getType())) {
				proxies.add(tokener.encode(proxy, entity));
			}
		}
		result.put(PROPERTY_PROXY, proxies); 
		return result;
	}
	
	public SortedSet<NodeProxy> getNodeProxies(){
		return proxies;
	}

	public ByteConverter getConverter() {
		if(converter==null){
			converter = new ByteConverterString();
		}
		return converter;
	}


	public BasicSpace withConverter(ByteConverter converter) {
		this.converter = converter;
		return this;
	}

	
	public String convertMessage(Message msg){
		BaseItem encode = getInternMap().encode(msg, tokener);
		ByteConverter byteConverter = getConverter();
		return byteConverter.encode(encode);
	}
	
	public boolean removeProxy(NodeProxy proxy){
		return this.proxies.remove(proxy);
	}

//FIXME	@Override
//	public boolean set(String attribute, Object value) {
//		if(PROPERTY_PROXY.equalsIgnoreCase(attribute)){
//			with((NodeProxy) value);
//			return true;
//		}
//		if((PROPERTY_PROXY+JsonIdMap.REMOVE).equalsIgnoreCase(attribute)){
//			return removeProxy((NodeProxy) value);
//		}
//		return super.set(attribute, value);
//	}
//	
//	@Override
//	public Object get(String attribute) {
//		if(PROPERTY_PROXY.equalsIgnoreCase(attribute)){
//			return getNodeProxies();
//		}
//		return super.get(attribute);
//	}
	
	Filter getFilter(){
		Filter result=new Filter().withPropertyRegard(filter);
		return result;
	}

	/**
	 * Add info to the Message like known Proxies and previous Change id
	 * @param msg The Message
	 * @param myProxy The Receiver-Proxy
	 * @param sendAnyhow switch for OnlineFlag for sending
	 */
	private void addInfo(Message msg, NodeProxy myProxy, boolean sendAnyhow){
		ModelHistory history = getHistory();
		msg.withModel(getModel());
		if(sendAnyhow) {
			msg.withSendAnyHow(sendAnyhow);
		}
		String messageId = msg.getMessageId(this, myProxy);
		msg.withPrevChange(history.getPrevChangeId(messageId));
	}
	
	public BasicSpace withPath(String path) {
		this.path = path;
		return this;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public boolean sendMessage(NodeProxy proxy, Message msg) {
		 return sendMessage(proxy, msg, false);
	}
	
	public boolean sendMessage(NodeProxy proxy, Message msg, boolean sendAnyhow) {
		// find my Proxy with Key
		NodeProxy myProxy = null;
		for(NodeProxy item : this.proxies) {
			if(NodeProxyType.isOutput(item.getType()) && item.getKey() != null) {
				myProxy = item;
				break;
			}
		}
		addInfo(msg, myProxy, sendAnyhow);
		with(proxy);
		return proxy.sending(msg);
	}
	
	public BasicSpace withPeerCount(int peerCount) {
		this.peerCount = peerCount;
		return this;
	}

	public boolean sendMessageToPeers(Message msg) {
		return sendMessageToPeers(msg, null);
	}
	
	public NodeProxy getProxy(String id) {
		if(id==null) {
			return null;
		}
		for(NodeProxy proxy : this.proxies) {
			if(id.equals(proxy.getKey())) {
				return proxy;
			}
		}
		return null;
	}
	
	protected void calculateSendProxy(Message msg, NodeProxy receiver, SimpleSet<NodeProxy> sendProxies) {
		SimpleList<String> receivedString = msg.getReceived();
		SimpleList<NodeProxy> received = new SimpleList<NodeProxy>();
		for(String item : receivedString) {
			received.add(getProxy(item));
		}
		
		SimpleList<Integer> receiverProxy=new SimpleList<Integer>();
		NodeProxy proxy;
		int step;
		int number;
		IdMap map = getInternMap();
		boolean out=false;
		if(receiver != null) {
			for(int i=0;i<this.proxies.size();i++) {
				proxy = this.proxies.get(i);
				if(NodeProxyType.OUT == proxy.getType() || NodeProxyType.INOUT == proxy.getType()) {
					out = true;
				}
				if(receiver == proxy) {
					receiverProxy.add(i);
					if(proxy.isSendable()) {
						msg.addToReceived(map.encode(proxy, tokener, new NodeProxyFilter()));
					}
				} else if(!proxy.isReconnecting(this.tryReconnectTimeSecond)) {
					sendProxies.add(proxy);
				}
			}
		} else {
			for(int i=0;i<this.proxies.size();i++) {
				proxy = this.proxies.get(i);
				if(NodeProxyType.OUT == proxy.getType()) {
					out = true;
				}else if(NodeProxyType.IN == proxy.getType() || NodeProxyType.INOUT == proxy.getType()) {
					if (NodeProxyType.INOUT == proxy.getType()) {
						out = true;
					}
					receiverProxy.add(i);
					if(proxy.isSendable()) {
						msg.addToReceived(map.encode(proxy, tokener, new NodeProxyFilter()));
					}
				} else if(!proxy.isReconnecting(this.tryReconnectTimeSecond)) {
					sendProxies.add(proxy);
				}
			}
		}
		if(!out) {
			return;
		}
		if(receiverProxy.size() <1 && this.proxies.size() > 0 ) {
			receiverProxy.add(0);
		}
		for(Integer i : receiverProxy) {
			// forward
			number = i;
			step=0;
			while(step<this.peerCount) {
				number++;
				if(number>=this.proxies.size()) {
					number -= this.proxies.size();
				}
				proxy = this.proxies.get(number);
				if(NodeProxyType.OUT == proxy.getType() || NodeProxyType.INOUT == proxy.getType()) {
					// If the proxy not already received the message, we want to send it to the proxy
					if(received.indexOf(proxy)<0) {
						step++;
						if(sendProxies.add(proxy)==false) {
							step = this.peerCount;
						}
					}
				}
			}
		}
		// Add Back
	}

	/**
	 * Try to reconnect old Peers.
	 * Try to send n next Peers and send back infos to last Peer
	 * @param msg Message to Send
	 * @param receiver Reciever-Proxy
	 * @return success
	 * 
	 * @author Stefan Lindel
	 */
	public boolean sendMessageToPeers(Message msg, NodeProxy receiver) {
		boolean success=false;
		addInfo(msg, receiver, false);
		// inform the next n peers
		SimpleSet<NodeProxy> sendProxies = new SimpleSet<NodeProxy>();
		calculateSendProxy(msg, receiver, sendProxies);
		
		// add message to local history
		ModelHistory history = getHistory();

		// add MSG to History
		history.addHistory(msg);

		// send to next peers
		IdMap map = getInternMap();
		for(NodeProxy peer : sendProxies) {
			boolean done = peer.sending(msg);
			if(done) {
				msg.addToReceived(map.encode(peer, tokener, new NodeProxyFilter()));
				success = true;
			}
		}
		// At Lest send nodeproxy infos back
		if(receiver != null) {
			receiver.sending(InfoMessage.create(this));
		}
		return success;
	}

	public SimpleList<Integer> getReconnectTime() {
		return this.tryReconnectTimeSecond;
	}
	public static BasicSpace newInstance(NodeProxy... proxyListener) {
		BasicSpace space = new BasicSpace().with(proxyListener);
		return space;
	}
	
	public static BasicSpace newInstance(Object world, IdMap map, NodeProxy... proxyListener) {
		BasicSpace space = new BasicSpace();
		space.with(new NodeProxyModel(world));
		space.with(proxyListener);
		return space;
	}
	public static BasicSpace newInstance(IdMap map, NodeProxy... proxyListener) {
		BasicSpace space = new BasicSpace();
		space.with(new NodeProxyModel(null));
		space.with(proxyListener);
		return space;
	}
	
	public Object execute(Runnable task) {
		return null;
	}
	
//	public Future<?> execute(Runnable task) {
//		Future<?> result = executor.submit(task);
//		return result;
//	}
	
	private NodeProxyModel nodeProxyModel;

	/**
	 * Returns all the Proxies in the Space (The ProxyModel)
	 * Not realy nice //TODO add Owner IdMap to Message
	 * @return the IdMap
	 */
	NodeProxyModel getModel() {
		if(this.nodeProxyModel == null) {
			for(NodeProxy proxy : this.proxies) {
				if(proxy instanceof NodeProxyModel) {
					nodeProxyModel = (NodeProxyModel)proxy;
				}
			}
		}
		return this.nodeProxyModel;
	}
	
	public Tokener getTokener() {
		if(tokener == null) {
			tokener = new JsonTokener();
		}
		return tokener;
	}

	public BaseItem encode(Object entity, Filter entityFilter) {
		if(this.map != null) {
			return this.map.encode(entity, tokener, entityFilter);
		}
		return null;
	}
	
	public String getId(Object entity) {
		if(this.map != null) {
			return this.map.getId(entity, true);
		}
		return null;
	}

	public Object getObject(String key) {
		if(this.map != null) {
			return this.map.getId(key, true);
		}
		return null;
	}

	@Override
	public boolean update(Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
