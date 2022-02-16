package de.uniks.networkparser.ext.petaf;

import java.util.Collection;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SendableItem;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.UpdateCondition;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.bytes.ByteConverter;
import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.ext.LogItem;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.petaf.filter.ProxyFilter;
import de.uniks.networkparser.ext.petaf.messages.AcceptMessage;
import de.uniks.networkparser.ext.petaf.messages.ChangeMessage;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.ext.petaf.messages.InfoMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyFileSystem;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyLocal;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyModel;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.Server;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedSet;

/**
 * BasicSpace for Replication.
 *
 * @author Stefan
 */
public class Space extends SendableItem implements ObjectCondition, SendableEntityCreator {
	private final String[] properties = new String[] { Space.PROPERTY_NAME, Space.PROPERTY_PATH, Space.PROPERTY_HISTORY,
			Space.PROPERTY_PROXY };
	
	/** The Constant PROPERTY_MODELROOT. */
	public static final String PROPERTY_MODELROOT = "root";
	
	/** The Constant PROPERTY_HISTORY. */
	public static final String PROPERTY_HISTORY = "history";
	
	/** The Constant PROPERTY_PROXY. */
	public static final String PROPERTY_PROXY = "proxies";
	
	/** The Constant INMESSAGE. */
	public static final String INMESSAGE = "";
	
	/** The Constant PROPERTY_PATH. */
	public static final String PROPERTY_PATH = "path";
	
	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";

	static final int DISABLE = 0;
	static final int MINUTE = 60;
	static final int TENMINUTE = 6000;
	static final int THIRTYMINUTE = 30000;

	private SortedSet<NodeProxy> proxies = new SortedSet<NodeProxy>(true);
	private ByteConverter converter;
	protected ModelHistory history = null;
	protected String path = "";
	private ProxyFilter filter = new ProxyFilter();
	private TaskExecutor executor;
	private NodeProxy firstPeer;
	private NodeProxy myNode;
	protected SimpleList<ObjectCondition> clients = new SimpleList<ObjectCondition>();
	private int peerCount = 2;
	protected String name;
	protected NodeBackup backupTask = new NodeBackup().withSpace(this);
	protected NetworkParserLog log = new NetworkParserLog();
	protected final ErrorHandler handler = new ErrorHandler();
	protected boolean isInit = true;
	protected final ChangeMessage changeMessageCreator = new ChangeMessage();
	protected PetaFilter messageFilter = new PetaFilter().withTyp(PetaFilter.ID);

	/**
	 * Time for Try to Reconnect Clients every x Seconds (Default:5x1m, 5x10m, 30m).
	 * Set Value to 0 for disable
	 */
	private SimpleList<Integer> tryReconnectTimeSecond = new SimpleList<Integer>()
			.with(MINUTE, MINUTE, MINUTE, MINUTE, MINUTE).with(TENMINUTE, TENMINUTE, TENMINUTE, TENMINUTE, TENMINUTE)
			.with(THIRTYMINUTE, DISABLE);
	protected IdMap map = createIdMap();
	private Tokener tokener;
	private DateTimeEntity lastTimerRun;
	private NodeProxyModel myModel;

	/**
	 * Gets the map.
	 *
	 * @return the map
	 */
	public IdMap getMap() {
		return map;
	}

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the space
	 */
	public Space withMap(IdMap map) {
		this.map = map;
		return this;
	}

	/**
	 * With model.
	 *
	 * @param map the map
	 * @param root the root
	 * @return the space
	 */
	public Space withModel(IdMap map, Object root) {
		this.withMap(map);
		this.withModelRoot(this.createModel(root));
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

	protected TaskExecutor createExecutorTimer() {
		return new SimpleExecutor().withSpace(this);
	}

	/**
	 * Gets the message filter.
	 *
	 * @return the message filter
	 */
	public PetaFilter getMessageFilter() {
		return messageFilter;
	}

	protected IdMap createIdMap() {
		IdMap map = new IdMap().with(this, new Message(), changeMessageCreator, new InfoMessage(), new NodeProxyTCP(),
				new NodeProxyLocal(), new NodeProxyFileSystem(null), new ConnectMessage().withSpace(this),
				new AcceptMessage().withSpace(this), new NodeProxyModel(null));
		map.withListener(this);

		/* Check for JavaFX-Tools */
		if (ReflectionLoader.PLATFORM != null) {
			map.withModelExecutor(new ModelExecutor());
		}

		return map;
	}

	/**
	 * With creator.
	 *
	 * @param value the value
	 * @return the space
	 */
	public Space withCreator(IdMap value) {
		this.map.with(value);
		return this;
	}
	
	/**
	 * With listener.
	 *
	 * @param listener the listener
	 * @return the space
	 */
	public Space withListener(ObjectCondition listener) {
		for(NodeProxy proxy : this.proxies) {
			if(proxy instanceof NodeProxyTCP) {
				NodeProxyTCP tcpProxy = (NodeProxyTCP) proxy;
				tcpProxy.withListener(listener);
			}
		}
		return this;
	}

	/**
	 * With creator.
	 *
	 * @param values the values
	 * @return the space
	 */
	public Space withCreator(SendableEntityCreator... values) {
		this.map.withCreator(values);
		return this;
	}

	/**
	 * With model root.
	 *
	 * @param modelRoot the model root
	 * @return the space
	 */
	public Space withModelRoot(NodeProxyModel modelRoot) {
		with(modelRoot);
		return this;
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the space
	 */
	public Space withName(String... name) {
		if(name != null && name.length>0) {
			this.name = name[0];
		}
		return this;
	}
	
	/**
	 * With name.
	 *
	 * @param name the name
	 * @param root the root
	 * @return the space
	 */
	public Space withName(String name, Object root) {
		this.withName(name);
		this.createModel(root, name + ".json");
		startModelDistribution(false);
		return this;
	}

	/**
	 * With.
	 *
	 * @param values the values
	 * @return the space
	 */
	public Space with(NodeProxy... values) {
		if (values == null) {
			return this;
		}
		for (NodeProxy proxy : values) {
			if (proxy != null && proxy.isValid()) {
				boolean changed = this.proxies.add(proxy);

				if (changed) {
					if (proxy.getKey() != null) {
						this.map.put(proxy.getKey(), proxy, false);
					}
					this.myNode = null;
					proxy.initSpace(this);
					firePropertyChange(PROPERTY_PROXY, null, proxy);
				}
			}
		}
		return this;
	}

	/**
	 * Gets the history.
	 *
	 * @return the history
	 */
	public ModelHistory getHistory() {
		if (history == null) {
			history = new ModelHistory().withSpace(this);
		}
		return history;
	}

	/**
	 * Connect to peer.
	 *
	 * @param url the url
	 * @param port the port
	 * @return the node proxy
	 */
	public NodeProxy connectToPeer(String url, int port) {
		NodeProxy proxy = getOrCreateProxy(url, port);
		this.firstPeer = proxy;
		if (proxy != null) {
			proxy.connectToPeer();
		}
		return proxy;
	}

	/**
	 * Gets the first peer.
	 *
	 * @return the first peer
	 */
	public NodeProxy getFirstPeer() {
		return firstPeer;
	}

	/**
	 * Create a Server Proxy.
	 *
	 * @param port Port number 0 is for Search free Port
	 * @return NodeProxy The created Server
	 */
	public NodeProxy startServer(int port) {
		NodeProxy newProxy = getNewProxy();
		newProxy.withType(NodeProxy.TYPE_INOUT);
		newProxy.setValue(newProxy, NodeProxyTCP.PROPERTY_PORT, port, SendableEntityCreator.NEW);
		this.with(newProxy);

		return newProxy;
	}

	/**
	 * Creates the model.
	 *
	 * @param root the root
	 * @return the node proxy model
	 */
	public NodeProxyModel createModel(Object root) {
		/* Check if NodeProxyModel exists for root */
		NodeProxyModel model = getModel();
		if (root == null) {
			return model;
		}
		if (model != null) {
			while (model.nextModel() != null) {
				if (root.equals(model.getModel())) {
					return model;
				}
			}
		}
		model = new NodeProxyModel(root);
		this.with(model);
		return model;
	}

	/**
	 * Creates the model.
	 *
	 * @param root the root
	 * @param fileName the file name
	 * @return the node proxy file system
	 */
	public NodeProxyFileSystem createModel(Object root, String fileName) {
		createModel(root);
		NodeProxyFileSystem fileSystem = null;
		if (fileName != null) {
			String filePath = null;
			if (this.path != null && this.path.length() > 0) {
				filePath = this.path + "/" + fileName;
			} else {
				filePath = fileName;
			}
			fileSystem = new NodeProxyFileSystem(filePath);
			fileSystem.withFullModell(true);
			this.isInit = false;
			this.with(fileSystem);
			fileSystem.load(root);
			this.isInit = true;

			/* Refactoring All Model */
			SimpleList<NodeProxy> candidates = new SimpleList<NodeProxy>();
			NodeProxyFileSystem[] fileSystemNodes = null;
			for (int i = 0; i < proxies.size(); i++) {
				NodeProxy proxy = proxies.get(i);
				if (proxy instanceof NodeProxyFileSystem) {
					/* Add All NodeProxyFileSystem */
					candidates.add(proxy);
				} else if (proxy instanceof NodeProxyModel) {
					NodeProxyModel modelProxy = (NodeProxyModel) proxy;
					if (modelProxy.getKey() == null) {
						/* Ups Sender not Finish */
						if (fileSystemNodes == null) {
							for (int z = i + 1; z < proxies.size(); z++) {
								NodeProxy fileSystemNode = proxies.get(z);
								if (fileSystemNode instanceof NodeProxyFileSystem) {
									candidates.add(fileSystemNode);
								}
							}
							fileSystemNodes = candidates.toArray(new NodeProxyFileSystem[candidates.size()]);
						}
						Object modell = modelProxy.getModel();
						BaseItem value = this.encode(modell, null);
						ChangeMessage msg = new ChangeMessage();
						msg.withMessage(value);
						this.sendMessage(msg, false, fileSystemNodes);
					}
				}
			}
		}
		return fileSystem;
	}

	/**
	 * Gets the or create proxy.
	 *
	 * @param url the url
	 * @param port the port
	 * @return the or create proxy
	 */
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

	/**
	 * Gets the or create node proxy.
	 *
	 * @param msg the msg
	 * @param readId the read id
	 * @return the or create node proxy
	 */
	public NodeProxy getOrCreateNodeProxy(Entity msg, boolean readId) {
		Entity props = null;
		if (msg.has(JsonTokener.PROPS)) {
			props = msg;
		}

		/* New Structure */
		NodeProxy proxy = getProxy(NodeProxy.PROPERTY_ID);
		if (proxy != null || props == null) {
			return proxy;
		}
		/* Proxy not exist must be create */
		proxy = getNewProxy();

		String[] properties = proxy.getProperties();
		for (String property : properties) {
			Object value = props.getValue(property);
			if (value != null) {
				proxy.setValue(proxy, property, value, SendableEntityCreator.NEW);
			}
		}
		this.with(proxy);
		return proxy;
	}

	/**
	 * Gets the new proxy.
	 *
	 * @return the new proxy
	 */
	public NodeProxy getNewProxy() {
		return new NodeProxyTCP();

	}

	/**
	 * With history.
	 *
	 * @param value the value
	 * @return the space
	 */
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

	/**
	 * Close.
	 */
	public void close() {
		for (NodeProxy proxy : proxies) {
			if (NodeProxy.isInput(proxy.getType())) {
				proxy.close();
			}
		}
		if (executor != null) {
			executor.shutdown();
		}
	}

	/**
	 * Gets the replication info.
	 *
	 * @return the replication info
	 */
	public Entity getReplicationInfo() {
		Tokener tokener = getTokener();
		Entity result = tokener.newInstance();
		result.put(PROPERTY_NAME, this.name);
		EntityList proxies = tokener.newInstanceList();
		MapEntity entity = new MapEntity(getMap());
		for(int i=0;i<this.proxies.size();i++) {
			NodeProxy proxy = this.proxies.get(i);
			if (NodeProxy.isOutput(proxy.getType())) {
				proxies.add(tokener.encode(proxy, entity));
			}
		}
		result.put(PROPERTY_PROXY, proxies);
		return result;
	}
	
	/**
	 * Sets the replication info.
	 *
	 * @param msg the msg
	 * @return true, if successful
	 */
	public boolean setReplicationInfo(Buffer msg) {
		Tokener tokener = getTokener();
		Entity result = tokener.newInstance();
		result.withValue(msg);
		Object value = result.getValue(PROPERTY_PROXY);
		if(value != null && value instanceof Collection<?>) {
			IdMap idMap = this.getMap();
			Collection<?> list = (Collection<?>) value;
			for(Object item : list) {
				Object proxy = idMap.decode(item);
				if(proxy != null && proxy instanceof NodeProxy) {
					this.with((NodeProxy) proxy);
				}
			}
		}
		return true;
	}

	/**
	 * Gets the node proxies.
	 *
	 * @param filters the filters
	 * @return the node proxies
	 */
	public SortedSet<NodeProxy> getNodeProxies(ObjectCondition... filters) {
		if (filters == null) {
			return proxies;
		}
		SortedSet<NodeProxy> result = new SortedSet<NodeProxy>(true);
		for (NodeProxy proxy : proxies) {
			for (ObjectCondition filter : filters) {
				if (filter != null && filter.update(proxy)) {
					result.add(proxy);
				}
			}
		}
		return proxies;
	}

	/**
	 * Gets the converter.
	 *
	 * @return the converter
	 */
	public ByteConverter getConverter() {
		if (converter == null) {
			converter = new ByteConverter();
		}
		return converter;
	}

	/**
	 * With converter.
	 *
	 * @param converter the converter
	 * @return the space
	 */
	public Space withConverter(ByteConverter converter) {
		this.converter = converter;
		return this;
	}

	/**
	 * Convert message.
	 *
	 * @param msg the msg
	 * @return the string
	 */
	public String convertMessage(Message msg) {
		BaseItem encode = getMap().encode(msg, tokener, messageFilter);
		addMessageElement(msg, encode);
		ByteConverter byteConverter = getConverter();
		return byteConverter.encode(encode);
	}

	/**
	 * Start model distribution.
	 *
	 * @param alwaysEncode the always encode
	 * @return true, if successful
	 */
	public boolean startModelDistribution(boolean alwaysEncode) {
		boolean result = true;
		for (NodeProxy proxy : this.proxies) {
			if (proxy instanceof NodeProxyModel) {
				NodeProxyModel modelProxy = (NodeProxyModel) proxy;
				Object model = modelProxy.getModel();
				if ((alwaysEncode || map.getKey(model) == null)
					&& (map.encode(model, tokener) == null)) {
						result = false;
				}
			}
		}
		return result;
	}

	protected void addMessageElement(Message msg, BaseItem encode) {

	}

	/**
	 * Removes the proxy.
	 *
	 * @param proxy the proxy
	 * @return true, if successful
	 */
	public boolean removeProxy(NodeProxy proxy) {
		boolean changed = this.proxies.remove(proxy);
		if (changed) {
			this.myNode = null;
		}
		return changed;
	}

	Filter getFilter() {
		Filter result = new Filter().withPropertyRegard(filter);
		return result;
	}

	/**
	 * Add info to the Message like known Proxies and previous Change id
	 * 
	 * @param msg        The Message
	 * @param myProxy    The Receiver-Proxy
	 * @param sendAnyhow switch for OnlineFlag for sending
	 */
	private void addInfo(Message msg, NodeProxy myProxy, boolean sendAnyhow) {
		ModelHistory history = getHistory();
		if (sendAnyhow) {
			msg.withSendAnyHow(sendAnyhow);
		}
		if (myProxy == null) {
			myProxy = getMyNode();
		}
		String messageId = msg.getMessageId(this, myProxy);
		msg.withPrevChange(history.getPrevChangeId(messageId));
		/* Add Receiver if possible */
		msg.withAddToReceived(myProxy);
		if (msg instanceof ReceivingTimerTask) {
			((ReceivingTimerTask) msg).withSpace(this);
		}
	}

	/**
	 * With path.
	 *
	 * @param path the path
	 * @return the space
	 */
	public Space withPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Send message.
	 *
	 * @param task the task
	 * @param proxy the proxy
	 * @return true, if successful
	 */
	public boolean sendMessage(SendingTimerTask task, NodeProxy... proxy) {
		if (proxy != null) {
			NodeProxy sender = null;
			if (proxy != null && proxy.length > 0) {
				sender = proxy[0];
			}
			task.withSender(sender);
			scheduleTask(task);
		}
		return true;
	}

	/**
	 * Method for Sending.
	 *
	 * @param msg     Message to Send
	 * @param proxies List of Proxies
	 * @return success sending
	 */
	public boolean sendMessage(Message msg, NodeProxy... proxies) {
		return sendMessage(msg, false, proxies);
	}

	/**
	 * Method for Sending.
	 *
	 * @param msg        Message to Send
	 * @param sendAnyhow Sending Message for every NodeProxy
	 * @param proxies    List of Proxies
	 * @return success sending
	 */
	public boolean sendMessage(Message msg, boolean sendAnyhow, NodeProxy... proxies) {
		if (msg == null) {
			return false;
		}
		if (msg.sending(this)) {
			return true;
		}
		SortedSet<NodeProxy> target;
		if ((proxies == null || proxies.length<1) && (sendAnyhow || msg.isSendAnyHow())) {
			target = this.proxies;
		} else {
			target = new SortedSet<NodeProxy>(true);
			for (NodeProxy proxy : proxies) {
				target.with(proxy);
			}
		}
		if (target == null || target.size() < 1) {
			return false;
		}
		/* find my Proxy with Key */
		NodeProxy myProxy = null;
		for (NodeProxy item : this.proxies) {
			if (NodeProxy.isOutput(item.getType()) && item.getKey() != null) {
				myProxy = item;
				break;
			}
		}
		addInfo(msg, myProxy, sendAnyhow);

		boolean sended = true;
		for (NodeProxy proxy : target) {
			if (proxy == null) {
				continue;
			}
			/* Add to ProxyList if not Exist */
			with(proxy);
			if (proxy.filter(msg)) {
				sended = sended && proxy.sending(msg);
			}
			if (sended) {
				proxy.withOnline(true);
			}
		}
		return sended;
	}

	/**
	 * With peer count.
	 *
	 * @param peerCount the peer count
	 * @return the space
	 */
	public Space withPeerCount(int peerCount) {
		this.peerCount = peerCount;
		return this;
	}

	/**
	 * Send message to peers.
	 *
	 * @param msg the msg
	 * @return true, if successful
	 */
	public boolean sendMessageToPeers(Message msg) {
		return sendMessageToPeers(msg, null);
	}

	/**
	 * Gets the proxy.
	 *
	 * @param id the id
	 * @return the proxy
	 */
	public NodeProxy getProxy(String id) {
		if (id == null) {
			return null;
		}
		for (NodeProxy proxy : this.proxies) {
			if (id.equals(proxy.getKey())) {
				return proxy;
			}
		}
		return null;
	}

	/**
	 * Adds the message.
	 *
	 * @param owner the owner
	 * @param logItem the log item
	 * @return true, if successful
	 */
	public boolean addMessage(Object owner, LogItem logItem) {
		if (this.log != null) {
			return this.log.print(owner, logItem);
		}
		return false;
	}

	/**
	 * Error.
	 *
	 * @param owner the owner
	 * @param method the method
	 * @param message the message
	 * @param params the params
	 * @return true, if successful
	 */
	public boolean error(Object owner, String method, Object message, Object... params) {
		if (this.log != null) {
			this.log.error(owner, method, message, params);
		}
		return false;
	}

	/**
	 * Debug.
	 *
	 * @param owner the owner
	 * @param method the method
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean debug(Object owner, String method, Object message) {
		if (this.log != null) {
			this.log.debug(owner, method, message);
		}
		return false;
	}

	protected void calculateSendProxy(Message msg, NodeProxy receiver, SimpleSet<NodeProxy> sendProxies) {
		SimpleSet<NodeProxy> received = msg.getReceived();

		SimpleList<Integer> receiverProxy = new SimpleList<Integer>();
		NodeProxy proxy;
		int step;
		int number;
		boolean out = false;
		NodeProxy myNode = getMyNode();
		if (receiver != null) {
			for (int i = 0; i < this.proxies.size(); i++) {
				proxy = this.proxies.get(i);
				if (NodeProxy.isOutput(proxy.getType())) {
					out = true;
				}
				if (receiver == proxy || isMyNode(proxy, myNode)) {
					receiverProxy.add(i);
					if (proxy.isSendable()) {
						msg.withAddToReceived(proxy);
					}
				} else if (!proxy.isReconnecting(this.tryReconnectTimeSecond)) {
					sendProxies.add(proxy);
				}
			}
		} else {
			for (int i = 0; i < this.proxies.size(); i++) {
				proxy = this.proxies.get(i);
				if (NodeProxy.TYPE_OUT.equals(proxy.getType())) {
					out = true;
				} else if (NodeProxy.isInput(proxy.getType())) {
					if (NodeProxy.isOutput(proxy.getType())) {
						out = true;
					}
					receiverProxy.add(i);
					if (proxy.isSendable()) {
						msg.withAddToReceived(proxy);
					}
				} else if (!proxy.isReconnecting(this.tryReconnectTimeSecond)) {
					sendProxies.add(proxy);
				}
			}
		}
		if (!out) {
			return;
		}
		if (receiverProxy.size() < 1 && this.proxies.size() > 0) {
			receiverProxy.add(0);
		}
		for (Integer i : receiverProxy) {
			/* forward */
			number = i;
			step = 0;
			while (step < this.peerCount) {
				number++;
				if (number >= this.proxies.size()) {
					number -= this.proxies.size();
					step++;
				}
				proxy = this.proxies.get(number);
				if (NodeProxy.isOutput(proxy.getType())) {
					/* If the proxy not already received the message, we want to send it to the proxy */
					if (received.indexOf(proxy) < 0 || proxy instanceof NodeProxyFileSystem) {
						step++;
						if (!sendProxies.add(proxy)) {
							/* Break while */
							step = this.peerCount;
						}
					}
				}
			}
		}
		 /* Add Back */
		receiverProxy.add(this.firstPeer);
	}

	private boolean isMyNode(NodeProxy proxy, NodeProxy myNode) {
		if (proxy == myNode) {
			return true;
		}
		while (myNode != null) {
			myNode = myNode.next();
			if (myNode == proxy) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Try to reconnect old Peers. Try to send n next Peers and send back infos to
	 * last Peer
	 *
	 * @author Stefan Lindel
	 * @param msg      Message to Send
	 * @param receiver Reciever-Proxy
	 * @return success
	 */
	public boolean sendMessageToPeers(Message msg, NodeProxy receiver) {
		boolean success = false;
		addInfo(msg, receiver, false);
		/* inform the next n peers */
		SimpleSet<NodeProxy> sendProxies = new SimpleSet<NodeProxy>();
		calculateSendProxy(msg, receiver, sendProxies);
		if (sendProxies.size() < 1) {
			return true;
		}

		/* add message to local history */
		ModelHistory history = getHistory();

		/* add MSG to History */
		history.addHistory(msg);

		/* send to next peers */
		for (NodeProxy peer : sendProxies) {
			if (peer.filter(msg)) {
				boolean done = peer.sending(msg);
				if (done) {
					msg.withAddToReceived(peer);
					success = true;
				}
			}
		}
		/* At Lest send nodeproxy infos back */
		if (receiver != null) {
			receiver.sending(InfoMessage.create(this));
		}
		return success;
	}

	/**
	 * Gets the reconnect time.
	 *
	 * @return the reconnect time
	 */
	public SimpleList<Integer> getReconnectTime() {
		return this.tryReconnectTimeSecond;
	}

	/**
	 * New instance.
	 *
	 * @param proxyListener the proxy listener
	 * @return the space
	 */
	public static Space newInstance(NodeProxy... proxyListener) {
		Space space = new Space().with(proxyListener);
		return space;
	}

	/**
	 * New instance.
	 *
	 * @param world the world
	 * @param map the map
	 * @param proxyListener the proxy listener
	 * @return the space
	 */
	public static Space newInstance(Object world, IdMap map, NodeProxy... proxyListener) {
		Space space = new Space();
		space.with(new NodeProxyModel(world));
		space.with(proxyListener);
		return space;
	}

	/**
	 * New instance.
	 *
	 * @param map the map
	 * @param proxyListener the proxy listener
	 * @return the space
	 */
	public static Space newInstance(IdMap map, NodeProxy... proxyListener) {
		Space space = new Space();
		space.with(new NodeProxyModel(null));
		space.with(proxyListener);
		return space;
	}

	/**
	 * Execute.
	 *
	 * @param task the task
	 * @return the object
	 */
	public Object execute(Runnable task) {
		if (this.executor == null) {
			this.executor = new SimpleExecutor().withSpace(this);
		}
		return this.executor.executeTask(task, 0);
	}

	/**
	 * Returns all the Proxies in the Space (The ProxyModel).
	 *
	 * @return the first NodeProxyModel
	 */
	public NodeProxyModel getModel() {
		if (this.myModel == null) {
			NodeProxyModel last = null;
			for (NodeProxy item : proxies) {
				if (item instanceof NodeProxyModel) {
					NodeProxyModel proxy = (NodeProxyModel) item;
					if (last == null) {
						this.myModel = last = proxy.setNextModel(null);
					} else {
						last = last.setNextModel(proxy);
					}
				}
			}
		}
		return this.myModel;
	}
	
	/**
	 * Returns the ModelRootof Proxies in the Space.
	 *
	 * @return the first NodeProxyModel Root
	 */
	public Object getModelRoot() {
		getModel();
		if(this.myModel != null) {
			return myModel.getModel();
		}
		return null;
	}

	/**
	 * Gets the tokener.
	 *
	 * @return the tokener
	 */
	public Tokener getTokener() {
		if (tokener == null) {
			tokener = new JsonTokener();
		}
		return tokener;
	}

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @param entityFilter the entity filter
	 * @return the base item
	 */
	public BaseItem encode(Object entity, Filter entityFilter) {
		if (this.map != null) {
			return this.map.encode(entity, tokener, entityFilter);
		}
		return null;
	}

	/**
	 * Gets the id.
	 *
	 * @param entity the entity
	 * @return the id
	 */
	public String getId(Object entity) {
		if (this.map != null) {
			return this.map.getId(entity, true);
		}
		return null;
	}

	/**
	 * Gets the key.
	 *
	 * @param entity the entity
	 * @return the key
	 */
	public String getKey(Object entity) {
		if (this.map != null) {
			return this.map.getKey(entity);
		}
		return null;
	}

	/**
	 * Gets the object.
	 *
	 * @param key the key
	 * @return the object
	 */
	public Object getObject(String key) {
		if (this.map != null) {
			return this.map.getId(key, true);
		}
		return null;
	}

	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public TaskExecutor getExecutor() {
		if (this.executor == null) {
			this.executor = createExecutorTimer();
		}
		return this.executor;
	}

	/**
	 * Update backup.
	 *
	 * @return true, if successful
	 */
	public boolean updateBackup() {
		if (this.backupTask.isEnable()) {
			return false;
		}
		this.backupTask.enable();
		;
		this.scheduleTask(this.backupTask, 10000);
		return true;
	}

	/**
	 * Schedule task.
	 *
	 * @param task the task
	 * @return the object
	 */
	public Object scheduleTask(Runnable task) {
		if (task == null) {
			return null;
		}
		return getExecutor().executeTask(task, 0);
	}

	/**
	 * Schedule task.
	 *
	 * @param task the task
	 * @param delay the delay
	 * @return the object
	 */
	public Object scheduleTask(Runnable task, int delay) {
		if (task == null) {
			return null;
		}
		return getExecutor().executeTask(task, delay);
	}

	/**
	 * Schedule task.
	 *
	 * @param task the task
	 * @param delay the delay
	 * @param interval the interval
	 * @return the object
	 */
	public Object scheduleTask(Runnable task, int delay, int interval) {
		if (task == null) {
			return null;
		}
		return getExecutor().executeTask(task, delay, interval);
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof SimpleEvent)) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		for (ObjectCondition client : clients) {
			client.update(event);
		}
		if (event.isIdEvent()) {
			return true;
		}
		if (event.getPropertyName() == null) {
			/* Only Notification from Map */
			return true;
		}

		if (event.getNewValue() instanceof Message) {
			return true;
		}
		if (event.getModelValue() instanceof NodeProxy) {
			return true;
		}

		if (event.getNewValue() instanceof NodeProxy || event.getSource() instanceof NodeProxy) {
			return true;
		}
		return updateModel(event);
	}

	/**
	 * Update model.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean updateModel(SimpleEvent event) {
		if (!this.isInit) {
			return false;
		}
		if (SendableEntityCreator.UPDATE.equals(event.getType()) || SendableEntityCreator.NEW.equals(event.getType())) {
			Object newValue = event.getNewValue();
			Object oldValue = event.getOldValue();
			/* Nachrichten senden */
			if (newValue instanceof NodeProxy) {
				return true;
			}
			if (newValue instanceof LogItem || oldValue instanceof LogItem) {
				return true;
			}
			/* Now Send Changing */
			ChangeMessage change = changeMessageCreator.getSendableInstance(false);
			change.withValue(event);
			sendMessageToPeers(change);
			return true;
		}
		return isInit;
	}

	/**
	 * With init.
	 *
	 * @param value the value
	 * @return the space
	 */
	public Space withInit(boolean value) {
		this.isInit = value;
		return this;
	}

	/**
	 * With client.
	 *
	 * @param clients the clients
	 * @return the space
	 */
	public Space withClient(ObjectCondition... clients) {
		if (clients == null) {
			return this;
		}
		for (ObjectCondition item : clients) {
			this.clients.add(item);
		}
		return this;
	}

	/**
	 * Without clients.
	 *
	 * @param clients the clients
	 * @return the space
	 */
	public Space withoutClients(ObjectCondition... clients) {
		if (clients == null) {
			return this;
		}
		for (ObjectCondition item : clients) {
			this.clients.remove(item);
		}
		return this;
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		if (this.executor != null) {
			this.executor.shutdown();
		}
		for (NodeProxy proxy : proxies) {
			proxy.close();
		}
	}

	/**
	 * Gets the my node.
	 *
	 * @return the my node
	 */
	public NodeProxy getMyNode() {
		if (this.myNode == null) {
			NodeProxy last = null;
			for (NodeProxy item : proxies) {
				if (NodeProxy.isInput(item.getType())) {
					if (last == null) {
						this.myNode = last = item;
						item.setNextMyNode(null);
					} else {
						last = last.setNextMyNode(item);
					}
				}
			}
		}
		return this.myNode;
	}

	/* Gennererll Update */
	/**
	 * update Proxy.
	 *
	 * @param message Message
	 * @return the NodeProxy Receiver
	 */
	public NodeProxy updateProxy(Message message) {
		return message.getReceiver();
	}

	 /* Gennererll Update */
	/**
 	 * Update Proxy.
 	 *
 	 * @param msg Message
 	 * @return the NodeProxy Receiver
 	 */
	public NodeProxy updateProxy(Entity msg) {
		String string = msg.getString("KEY");
		NodeProxy proxy = getProxy(string);
		if (proxy == null) {
			return null;
		}
		String[] updateProperties = proxy.getUpdateProperties();
		for (String property : updateProperties) {
			Object object = msg.getValue(property);
			if (object != null) {
				proxy.setValue(proxy, property, object, SendableEntityCreator.UPDATE);
			}
		}
		return proxy;
	}

	/**
	 * Clear proxies.
	 */
	public void clearProxies() {
		this.proxies.clear();
	}

	/**
	 * Suspend notification.
	 *
	 * @param accumulates the accumulates
	 * @return true, if successful
	 */
	public boolean suspendNotification(UpdateCondition... accumulates) {
		MapListener mapListener = getMap().getMapListener();
		return mapListener.suspendNotification(accumulates);
	}

	/**
	 * Reset notification.
	 *
	 * @return the simple list
	 */
	public SimpleList<UpdateCondition> resetNotification() {
		MapListener mapListener = getMap().getMapListener();
		return mapListener.resetNotification();
	}

	/**
	 * Gets the last proxy.
	 *
	 * @param property for example TimeValue PROPERTY_SEND PROPERTY_RECEIVE
	 *                 PROPERTY_HISTORY
	 * @return LastProxy
	 */
	public NodeProxy getLastProxy(String property) {
		NodeProxy lastItem = null;
		long max = Long.MIN_VALUE;
		for (NodeProxy item : proxies) {
			Object value = item.getValue(item, property);
			if (value instanceof Number) {
				Long no = (Long) value;
				if (no > max) {
					max = no;
					lastItem = item;
				}
			}

		}
		return lastItem;
	}

	/**
	 * Send event to clients.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean sendEventToClients(SimpleEvent event) {
		boolean result = true;
		for (ObjectCondition client : clients) {
			result = result && client.update(event);
		}
		return result;
	}

	/**
	 * With last timer run.
	 *
	 * @param value the value
	 * @return the space
	 */
	public Space withLastTimerRun(DateTimeEntity value) {
		this.lastTimerRun = value;
		return this;
	}

	/**
	 * Gets the last timer run.
	 *
	 * @return the last timer run
	 */
	public DateTimeEntity getLastTimerRun() {
		return lastTimerRun;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	/* Methods for Creator */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof Space)) {
			return null;
		}
		Space space = (Space) entity;
		if (attribute.equalsIgnoreCase(Space.PROPERTY_PROXY)) {
			return space.getNodeProxies();
		}
		if (attribute.equalsIgnoreCase(Space.PROPERTY_NAME)) {
			return space.getName();
		}
		if (attribute.equalsIgnoreCase(Space.PROPERTY_PATH)) {
			return space.getPath();
		}

		if (attribute.equalsIgnoreCase(Space.PROPERTY_HISTORY)) {
			return space.getHistory();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof Space)) {
			return false;
		}
		Space space = (Space) entity;
		if (attribute.equalsIgnoreCase(Space.PROPERTY_PROXY)) {
			space.with((NodeProxy) value);
			return true;
		}
		return false;
	}

	/**
	 * Handle exception.
	 *
	 * @param e the e
	 */
	public void handleException(Throwable e) {
		this.handler.saveException(e, false);
	}

	/**
	 * Handle msg.
	 *
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean handleMsg(Message message) {
		/* Allgemeine Verarbeiten */
		if (message instanceof ReceivingTimerTask) {
			((ReceivingTimerTask) message).withSpace(this);
			this.scheduleTask((ReceivingTimerTask) message);
			return true;
		}
		if (message.isSendingToPeers()) {
			this.sendMessageToPeers(message);
		}
		return false;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return Space.class;
	}

	/**
	 * Update network.
	 *
	 * @param type the type
	 * @param nodeProxy the node proxy
	 * @return true, if successful
	 */
	public boolean updateNetwork(String type, NodeProxy nodeProxy) {
		SimpleEvent event = new SimpleEvent(this, type, null, nodeProxy);
		return sendEventToClients(event);
	}

	/**
	 * With model executor.
	 *
	 * @param modelExecutor the model executor
	 * @return the space
	 */
	public Space withModelExecutor(ObjectCondition modelExecutor) {
		this.map.withModelExecutor(modelExecutor);
		return this;
	}

	/**
	 * Search.
	 *
	 * @param port the port
	 * @return the node proxy TCP
	 */
	public NodeProxyTCP search(int port) {
		NodeProxyTCP proxy = NodeProxyTCP.createServer(port);
		this.with(proxy);
		proxy.sendSearch();
		return proxy;
	}

	/**
	 * Creates the server.
	 *
	 * @param port the port
	 * @return the node proxy TCP
	 */
	public NodeProxyTCP createServer(int port) {
		NodeProxyTCP proxy = NodeProxyTCP.createServer(port);
		proxy.withServerType(Server.TCP);
		this.with(proxy);
		
		return proxy;
	}
	
	/**
	 * Log exception.
	 *
	 * @param owner the owner
	 * @param method the method
	 * @param exception the exception
	 * @return true, if successful
	 */
	public boolean logException(Object owner, String method, Throwable exception) {
		if(log != null) {
			return log.error(owner, method, exception);
		}
		return false;
	}
}
