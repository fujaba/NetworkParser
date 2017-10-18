package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.UpdateAccumulate;
import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.ext.LogItem;
import de.uniks.networkparser.ext.petaf.filter.ProxyFilter;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.ext.petaf.messages.util.ChangeMessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.ConnectMessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.MessageCreator;
import de.uniks.networkparser.ext.petaf.messages.util.PingMessageCreator;
import de.uniks.networkparser.ext.petaf.proxy.NodeBackup;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyLocal;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

/**
 * BasicSpace for Replication
 * @author Stefan
 *
 */
public class Space extends SendableItem implements ObjectCondition {
	public static final String PROPERTY_MODELROOT="root";
	public static final String PROPERTY_HISTORY="history";
	public static final String PROPERTY_PROXY="proxies";
	public static final String PROPERTY_PATH="path";
	public static final String PROPERTY_NAME="name";
	
	private SortedSet<NodeProxy> proxies=new SortedSet<NodeProxy>(true);
	private ByteConverter converter;
	protected ModelHistory history = null;
	private String path = "";
	private ProxyFilter filter = new ProxyFilter();
	private TaskExecutor executor;
	private NodeProxy firstPeer;
	private NodeProxy myNode;
	protected SimpleList<SimpleEventCondition> clients = new SimpleList<SimpleEventCondition>();
	private int peerCount=2;
	static final int DISABLE=0;
	static final int MINUTE=60;
	static final int TENMINUTE=6000;
	static final int THIRTYMINUTE=30000;
	private String name;
	protected NodeBackup backupTask = new NodeBackup().withSpace(this);
	protected NetworkParserLog log=new NetworkParserLog();

	/** Time for Try to Reconnect Clients every x Seconds (Default:5x1m, 5x10m, 30m). Set Value to 0 for disable	 */
	private SimpleList<Integer> tryReconnectTimeSecond=new SimpleList<Integer>()
			.with(MINUTE,MINUTE,MINUTE,MINUTE,MINUTE)
			.with(TENMINUTE,TENMINUTE,TENMINUTE,TENMINUTE,TENMINUTE)
			.with(THIRTYMINUTE, DISABLE);
	protected IdMap map = new IdMap()
			.with(	new MessageCreator(), 
					new ChangeMessageCreator(), 
					new PingMessageCreator(), 
					new NodeProxyTCP(), 
					new NodeProxyLocal(), 
					new ConnectMessageCreator(), 
					new NodeProxyModel(null));
//MOVE TO SUBCLASS	private TaskExecutor executor=new TaskExecutor();
	private Tokener tokener;
	
	public IdMap getMap() {
		return map;
	}
	
	public Space withModelRoot(NodeProxyModel modelRoot) {
		with(modelRoot);
		return this;
	}

	public Space with(NodeProxy... values) {
		if(values == null) {
			return this;
		}
		for(NodeProxy proxy : values) {
			if (proxy != null) {
				boolean changed = this.proxies.add(proxy);

				if (changed) {
					this.myNode = null;
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
	
	public NodeProxy connectToPeer(String url, int port) {
		NodeProxy proxy = getOrCreateProxy(url, port);
		this.firstPeer = proxy;
		if(proxy != null) {
			proxy.connectToPeer();
		}
		return proxy;
	}
	
	public NodeProxy getFirstPeer() {
		return firstPeer;
	}
	
	public NodeProxy getOrCreateProxy(String url, int port) {
		if (url.equals("127.0.0.1")) {
			return null;
		}
		String value = url + ":" + port;
		NodeProxy proxy = getProxy(value);

		if (proxy != null) {
			return proxy;
		}
		NodeProxy newProxy = getNewProxy();
		newProxy.setValue(newProxy, NodeProxyTCP.PROPERTY_URL, url, SendableEntityCreator.NEW);
		newProxy.setValue(newProxy, NodeProxyTCP.PROPERTY_PORT, port, SendableEntityCreator.NEW);
		this.with(newProxy);
		return newProxy;
	}
	
	public NodeProxy getNewProxy(){
		return new NodeProxyTCP();
		
	}

	public Space withHistory(ModelHistory value) {
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
		if(executor!= null) {
			executor.shutdown();
		}
	}
	
	public Entity getReplicationInfo() {
		Tokener tokener = getTokener();
		Entity result = tokener.newInstance();
		result.put(PROPERTY_NAME, this.name);
		EntityList proxies = tokener.newInstanceList();
		MapEntity entity = new MapEntity(getMap());
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


	public Space withConverter(ByteConverter converter) {
		this.converter = converter;
		return this;
	}

	
	public String convertMessage(Message msg){
		BaseItem encode = getMap().encode(msg, tokener);
		ByteConverter byteConverter = getConverter();
		return byteConverter.encode(encode);
	}
	
	public boolean removeProxy(NodeProxy proxy){
		boolean changed = this.proxies.remove(proxy);
		if(changed) {
			this.myNode = null;
		}
		return changed;
	}
	
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
//FIXME REMOVE?? SL		msg.withModel(getModel());
		if(sendAnyhow) {
			msg.withSendAnyHow(sendAnyhow);
		}
		String messageId = msg.getMessageId(this, myProxy);
		msg.withPrevChange(history.getPrevChangeId(messageId));
	}
	
	public Space withPath(String path) {
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
	
	public Space withPeerCount(int peerCount) {
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

	public void addMessage(Object owner, LogItem logItem) {
		this.log.print(owner, logItem);
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
		IdMap map = getMap();
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
		IdMap map = getMap();
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
	public static Space newInstance(NodeProxy... proxyListener) {
		Space space = new Space().with(proxyListener);
		return space;
	}
	
	public static Space newInstance(Object world, IdMap map, NodeProxy... proxyListener) {
		Space space = new Space();
		space.with(new NodeProxyModel(world));
		space.with(proxyListener);
		return space;
	}
	public static Space newInstance(IdMap map, NodeProxy... proxyListener) {
		Space space = new Space();
		space.with(new NodeProxyModel(null));
		space.with(proxyListener);
		return space;
	}
	
	public Object execute(Runnable task) {
		if(this.executor == null) {
			this.executor = new SimpleExecutor();
		}
		return this.executor.executeTask(task, 0);
	}
	
	/**
	 * Returns all the Proxies in the Space (The ProxyModel)
	 * @return the first NodeProxyModel
	 */
	protected NodeProxyModel getModel() {
		NodeProxyModel result = null;
		for(NodeProxy proxy : this.proxies) {
			if(proxy instanceof NodeProxyModel) {
				result = (NodeProxyModel)proxy;
			}
		}
		return result;
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

	public TaskExecutor getExecutor() {
		return this.executor;
	}
	
	public boolean updateBackup() {
		if(this.backupTask.isEnable()) {
			return false;
		}
		this.backupTask.enable();;
		this.scheduleTask(this.backupTask, 10000);
		return true;
	}
	
	public Object scheduleTask(Runnable task, int delay){
		if(task == null) {
			return null;
		}
		return getExecutor().executeTask(task, delay);
	}
	
	public Object scheduleTask(Runnable task, int delay, int interval){
		if(task == null) {
			return null;
		}
		return getExecutor().executeTask(task, delay, interval);
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return true;
		}
		SimpleEvent event = (SimpleEvent) value;
		for (ObjectCondition client : clients) {
			client.update(event);
		}
		return true;
	}
	
	public void dispose()
	{
		if(this.executor != null) {
			this.executor.shutdown();
		}
	}
	
	public NodeProxy getMyNode() {
		if(this.myNode == null) {
			NodeProxy last=null;
			for(NodeProxy item : proxies) {
				if(NodeProxyType.isInput(item.getType())) {
					if(last == null) {
						last = item;
						item.with(null);
					} else {
						last = item.with(last);
						last.with(null);
					}
				}
			}
		}
		return this.myNode;
	}
	
	// Gennererll Update
	public NodeProxy updateProxy(JsonObject msg) {
		String string = msg.getString("KEY");
		NodeProxy proxy = getProxy(string);
		if(proxy == null) {
			return null;
		}
		String[] updateProperties = proxy.getUpdateProperties();
		for(String property : updateProperties) {
			Object object = msg.get(property);
			if(object != null) {
				proxy.setValue(proxy, property, object, SendableEntityCreator.UPDATE);
			}
		}
		return proxy;
	}
	

	public void clearProxies() {
		this.proxies.clear();
	}
	
	public boolean suspendNotification(UpdateAccumulate... accumulates) {
		MapListener mapListener = getMap().getMapListener();
		return mapListener.suspendNotification(accumulates);
	}
	
	public SimpleList<UpdateAccumulate> resetNotification() {
		MapListener mapListener = getMap().getMapListener();
		return mapListener.resetNotification();
	}
	
	
	/**
	 * @param property for example TimeValue
	 * 			PROPERTY_SEND
	 * 			PROPERTY_RECEIVE
	 * 			PROPERTY_HISTORY
	 * @return LastProxy
	 */
	public NodeProxy getLastProxy(String property) {
		NodeProxy lastItem = null;
		long max=Long.MIN_VALUE;
		for(NodeProxy item : proxies) {
			Object value = item.getValue(item, property);
			if(value instanceof Number) {
				Long no = (Long) value;
				if(no>max) {
					max = no;
					lastItem = item;
				}
			}
			
		}
		return lastItem;
	}
}