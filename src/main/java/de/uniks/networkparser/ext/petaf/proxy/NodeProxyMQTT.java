package de.uniks.networkparser.ext.petaf.proxy;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.SocketFactory;

import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttMessage;
import de.uniks.networkparser.ext.mqtt.MqttTopic;
import de.uniks.networkparser.ext.mqtt.internal.ClientComms;
import de.uniks.networkparser.ext.mqtt.internal.ConnectActionListener;
import de.uniks.networkparser.ext.mqtt.internal.MqttDisconnect;
import de.uniks.networkparser.ext.mqtt.internal.MqttPublish;
import de.uniks.networkparser.ext.mqtt.internal.MqttSubscribe;
import de.uniks.networkparser.ext.mqtt.internal.MqttUnsubscribe;
import de.uniks.networkparser.ext.mqtt.internal.TCPNetworkModule;
import de.uniks.networkparser.ext.mqtt.internal.Token;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class NodeProxyMQTT extends NodeProxy{
	/** The default keep alive interval in seconds if one is not specified  */
	public static final int KEEP_ALIVE_INTERVAL_DEFAULT = 60;
	/**
	 * The default connection timeout in seconds if one is not specified
	 */
	public static final int CONNECTION_TIMEOUT_DEFAULT = 30;
	/** The default max inflight if one is not specified */
    public static final int MAX_INFLIGHT_DEFAULT = 10;

	/** The default MqttVersion is 3.1.1 first, dropping back to 3.1 if that fails */
	public static final int MQTT_VERSION_DEFAULT = 0;
	/**Mqtt Version 3.1 */
	public static final int MQTT_VERSION_3_1 = 3;
	/** Mqtt Version 3.1.1 */
	public static final int MQTT_VERSION_3_1_1 = 4;
	
	public static final String EVENT_CONNECT="connected";
	public static final String EVENT_CONNECTLOST = "ConnectionLost";
	public static final String EVENT_MESSAGE= "Message";
	private static final String CLIENT_ID_PREFIX = "paho";
	private static final long QUIESCE_TIMEOUT = 30000; // ms
	private static final char MIN_HIGH_SURROGATE = '\uD800';
	private static final char MAX_HIGH_SURROGATE = '\uDBFF';
	
	protected static final int URI_TYPE_TCP = 0;
	protected static final int URI_TYPE_SSL = 1;
	protected static final int URI_TYPE_LOCAL = 2;
	protected static final int URI_TYPE_WS = 3;
	protected static final int URI_TYPE_WSS = 4;
	
	public static final String PROPERTY_SERVERURL = "url";
	
	private String userName;
	private char[] password;
	private boolean cleanSession = true;
	private int keepAliveInterval = KEEP_ALIVE_INTERVAL_DEFAULT;
	private int maxInflight = MAX_INFLIGHT_DEFAULT;
	private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
	private int mqttVersion = MQTT_VERSION_DEFAULT;
	private String clientId;
	private String serverURI;
	private boolean reconnecting = false;
	
	protected long timeToWait = -1;				// How long each method should wait for action to complete -1 Standard -2 deactive
	protected ClientComms comms;

	private SimpleKeyValueList<String, MqttTopic> topics = new SimpleKeyValueList<String, MqttTopic>();
	private SimpleKeyValueList<String, MqttPublish> persistence;
	private ScheduledExecutorService executorService;
	
	public NodeProxyMQTT() {
		this.property.addAll(PROPERTY_SERVERURL);
		this.propertyUpdate.addAll(PROPERTY_SERVERURL);
		this.propertyInfo.addAll(PROPERTY_SERVERURL);
	}
	
	public NodeProxyMQTT(String serverURI) {
		this(serverURI, null, new SimpleKeyValueList<String, MqttPublish>(), null);
	}
	
	public NodeProxyMQTT(String serverURI, String clientId) {
		this(serverURI, clientId, new SimpleKeyValueList<String, MqttPublish>(), null);
	}
	
	public NodeProxyMQTT(String serverURI, String clientId, SimpleKeyValueList<String, MqttPublish> persistence,
			ScheduledExecutorService executorService) {
		if(clientId == null) {
			clientId = NodeProxyMQTT.generateClientId();
		}
		// Count characters, surrogate pairs count as one character.
		int clientIdLength = 0;
		for (int i = 0; i < clientId.length() - 1; i++) {
			char ch = clientId.charAt(i);
			if((ch >= MIN_HIGH_SURROGATE) && (ch <= MAX_HIGH_SURROGATE)) {
				i++;
			}
			clientIdLength++;
		}
		if (clientIdLength > 65535) {
			throw new IllegalArgumentException("ClientId longer than 65535 characters");
		}

		NodeProxyMQTT.validateURI(serverURI);

		this.serverURI = serverURI;
		this.clientId = clientId;

		this.persistence = persistence;
		if (this.persistence == null) {
			this.persistence = new SimpleKeyValueList<String, MqttPublish>();
		}

		this.executorService = executorService;
		if (this.executorService == null) {
			this.executorService = Executors.newScheduledThreadPool(10);
		}

		// @TRACE 101=<init> ClientID={0} ServerURI={1} PersistenceType={2}

		try {
			this.comms = new ClientComms(this, this.persistence, this.executorService);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a randomly generated client identifier based on the the fixed
	 * prefix (paho) and the system time.
	 * <p>
	 * When cleanSession is set to false, an application must ensure it uses the
	 * same client identifier when it reconnects to the server to resume state
	 * and maintain assured message delivery.
	 * </p>
	 * 
	 * @return a generated client identifier
	 * @see MqttConnectOptions#setCleanSession(boolean)
	 */
	public static String generateClientId() {
		// length of nanoTime = 15, so total length = 19 < 65535(defined in
		// spec)
		return CLIENT_ID_PREFIX + System.nanoTime();
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NodeProxyMQTT();
	}

	public Token connect() throws MqttException {
		if (comms.isConnected()) {
			throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CONNECTED);
		}
		if (comms.isConnecting()) {
			throw MqttException.withReason(MqttException.REASON_CODE_CONNECT_IN_PROGRESS);
		}
		if (comms.isDisconnecting()) {
			throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECTING);
		}
		if (comms.isClosed()) {
			throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CLOSED);
		}
		// @TRACE 103=cleanSession={0} connectionTimeout={1} TimekeepAlive={2}
		// userName={3} password={4} will={5} userContext={6} callback={7}
		comms.setNetworkModules(createNetworkModules(serverURI));
//		comms.setReconnectCallback(new MqttReconnectCallback(automaticReconnect));

		// Insert our own callback to iterate through the URIs till the connect
		// succeeds
		Token userToken = new Token(getClientId());
		ConnectActionListener connectActionListener = new ConnectActionListener(this, persistence, comms, userToken, reconnecting);

		// If we are using the MqttCallbackExtended, set it on the
		connectActionListener.setMqttCallback(comms.getCallback());

		connectActionListener.connect();
		
		waitForCompletion(userToken);
		return userToken;
	}
	
	protected void waitForCompletion(Token token) throws MqttException {
		if(this.timeToWait>-2) {
			token.waitForCompletion(timeToWait);
		}
	}
	
	/**
	 * Factory method to create an array of network modules, one for each of the
	 * supplied URIs
	 *
	 * @param address the URI for the server.
	 * @param options  the {@link MqttConnectOptions} for the connection.
	 * @return a network module appropriate to the specified address.
	 * @throws MqttException  if an exception occurs creating the network Modules
	 */
	protected TCPNetworkModule createNetworkModules(String... address)
			throws MqttException {
		// @TRACE 116=URI={0}

		if (serverURI == null) {
			if(address != null && address.length>0) {
				serverURI = address[0];
			}
		}

		// @TRACE 115=URI={0}
		TCPNetworkModule netModule;
		int serverURIType = validateURI(serverURI);

		URI uri;
		try {
			uri = new URI(serverURI);
			// If the returned uri contains no host and the address contains underscores,
			// then it's likely that Java did not parse the URI
			if(uri.getHost() == null && serverURI.contains("_")){
				try {
					final Field hostField = URI.class.getDeclaredField("host");
					hostField.setAccessible(true);
					// Get everything after the scheme://
					String shortAddress = serverURI.substring(uri.getScheme().length() + 3);
					hostField.set(uri, getHostName(shortAddress));
					
				} catch (Exception e) {
					throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, e.getCause());
				} 
				
			}
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URI: " + address + ", " + e.getMessage());
		}

		String host = uri.getHost();
		int port = uri.getPort(); // -1 if not defined
		SocketFactory factory = null;
		switch (serverURIType) {
		case URI_TYPE_TCP :
			if (port == -1) {
				port = 1883;
			}
			if (factory == null) {
				factory = SocketFactory.getDefault();
			}
			netModule = new TCPNetworkModule(factory, host, port, clientId);
			((TCPNetworkModule)netModule).setConnectTimeout(getConnectionTimeout());
			break;
		
		default:
			// This shouldn't happen, as long as validateURI() has been called.
			netModule = null;
		}
		return netModule;
	}
	
	private String getHostName(String uri) {
		int portIndex = uri.indexOf(':');
		if (portIndex == -1) {
			portIndex = uri.indexOf('/');
		}
		if (portIndex == -1) {
			portIndex = uri.length();
		}
		return uri.substring(0, portIndex);
	}

	/**
	 * Validate a URI
	 * @param srvURI The Server URI
	 * @return the URI type
	 */
	public static int validateURI(String srvURI) {
		try {
			URI vURI = new URI(srvURI);
			if ("ws".equals(vURI.getScheme())){
				return URI_TYPE_WS;
			}
			else if ("wss".equals(vURI.getScheme())) {
				return URI_TYPE_WSS;
			}

			if ((vURI.getPath() == null) || vURI.getPath().isEmpty()) {
				// No op path must be empty
			}
			else {
				throw new IllegalArgumentException(srvURI);
			} 
			if ("tcp".equals(vURI.getScheme())) {
				return URI_TYPE_TCP;
			}
			else if ("ssl".equals(vURI.getScheme())) {
				return URI_TYPE_SSL;
			}
			else if ("local".equals(vURI.getScheme())) {
				return URI_TYPE_LOCAL;
			}
			else {
				throw new IllegalArgumentException(srvURI);
			}
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(srvURI);
		}
	}
	
	private int getConnectionTimeout() {
		return connectionTimeout;
	}

	public String getClientId() {
		return clientId;
	}
	
	public String getServerURI() {
		return serverURI;
	}
	
	public boolean isConnected() {
		return comms.isConnected();
	}

	@Override
	public boolean isSendable() {
		return true;
	}
	
	public Token disconnect() throws MqttException {
		return this.disconnect(QUIESCE_TIMEOUT);
	}
	
	public Token disconnect(long quiesceTimeout) throws MqttException {
		// @TRACE 104=> quiesceTimeout={0} userContext={1} callback={2}

		Token token = new Token(getClientId());

		MqttDisconnect disconnect = new MqttDisconnect();
		try {
			comms.disconnect(disconnect, quiesceTimeout, token);
		} catch (MqttException ex) {
			// @TRACE 105=< exception
			throw ex;
		}
		// @TRACE 108=<

		waitForCompletion(token);
		return token;
	}
	
	/**
	 * Get a topic object which can be used to publish messages.
	 * <p>
	 * There are two alternative methods that should be used in preference to
	 * this one when publishing a message:
	 * </p>
	 * <ul>
	 * <li>{@link MqttClient#publish(String, MqttMessage)} to publish a
	 * message in a non-blocking manner or</li>
	 * <li>{@link MqttClient#publish(String, MqttMessage)} to publish a message
	 * in a blocking manner</li>
	 * </ul>
	 * <p>
	 * When you build an application, the design of the topic tree should take
	 * into account the following principles of topic name syntax and semantics:
	 * </p>
	 *
	 * <ul>
	 * <li>A topic must be at least one character long.</li>
	 * <li>Topic names are case sensitive. For example, <em>ACCOUNTS</em> and
	 * <em>Accounts</em> are two different topics.</li>
	 * <li>Topic names can include the space character. For example,
	 * <em>Accounts payable</em> is a valid topic.</li>
	 * <li>A leading "/" creates a distinct topic. For example,
	 * <em>/finance</em> is different from <em>finance</em>. <em>/finance</em>
	 * matches "+/+" and "/+", but not "+".</li>
	 * <li>Do not include the null character (Unicode \x0000) in any topic.</li>
	 * </ul>
	 *
	 * <p>
	 * The following principles apply to the construction and content of a topic
	 * tree:
	 * </p>
	 *
	 * <ul>
	 * <li>The length is limited to 64k but within that there are no limits to
	 * the number of levels in a topic tree.</li>
	 * <li>There can be any number of root nodes; that is, there can be any
	 * number of topic trees.</li>
	 * </ul>
	 *
	 * @param topic  the topic to use, for example "finance/stock/ibm".
	 * @return an MqttTopic object, which can be used to publish messages to the
	 *         topic.
	 */
	protected MqttTopic getTopic(String topic) {
		MqttTopic.validate(topic, false/* wildcards NOT allowed */);

		MqttTopic result = (MqttTopic) topics.get(topic);
		if (result == null) {
			result = new MqttTopic(topic, comms);
			topics.put(topic, result);
		}
		return result;
	}
	
	public void subscribe(String... topicFilter) throws MqttException {
		if(topicFilter == null) {
			return;
		}
		int[] qos=new int[topicFilter.length];
		for(int i=0;i<topicFilter.length;i++) {
			qos[i] = 1;
		}
		this.subscribe(topicFilter, qos, null);
	}

	public Token subscribe(String topicFilter, int qos) throws MqttException {
		return this.subscribe(new String[] { topicFilter }, new int[] { qos }, null);
	}

	public Token subscribe(String[] topicFilters, int[] qos, ConnectActionListener callback)
			throws MqttException {
		if (topicFilters.length != qos.length) {
			throw new IllegalArgumentException();
		}

		// Only Generate Log string if we are logging at FINE level
		Token token = new Token(getClientId());
		token.setTopics(topicFilters);

		MqttSubscribe register = new MqttSubscribe(topicFilters, qos);

		comms.sendNoWait(register, token);
		// @TRACE 109=<
		waitForCompletion(token);
		return token;
	}

	public Token unsubscribe(String... topicFilters) throws MqttException {
		// Only Generate Log string if we are logging at FINE level
		for (int i = 0; i < topicFilters.length; i++) {
			// Check if the topic filter is valid before unsubscribing
			// Although we already checked when subscribing, but invalid
			// topic filter is meanless for unsubscribing, just prohibit it
			// to reduce unnecessary control packet send to broker.
			MqttTopic.validate(topicFilters[i], true/* allow wildcards */);
		}

		Token token = new Token(getClientId());
		token.setTopics(topicFilters);

		MqttUnsubscribe unregister = new MqttUnsubscribe(topicFilters);

		comms.sendNoWait(unregister, token);
		// @TRACE 110=<
		
		waitForCompletion(token);

		return token;
	}

	public void setCallback(SimpleEventCondition callback) {
		comms.setCallback(callback);
	}
	
	public Token publish(String topic, MqttMessage message)
			throws MqttException {
		return this.publish(topic, message, null);
	}
	
	public Token publish(String topic, byte...message)
			throws MqttException {
		MqttMessage mqttMessage = new MqttMessage(message);
		return this.publish(topic, mqttMessage, null);
	}

	public Token publish(String topic, MqttMessage message,	ConnectActionListener callback) throws MqttException {
		// @TRACE 111=< topic={0} message={1}userContext={1} callback={2}

		// Checks if a topic is valid when publishing a message.
		MqttTopic.validate(topic, false/* wildcards NOT allowed */);

		Token token = new Token(getClientId());
		token.setMessage(message);
		token.setTopics(new String[] { topic });

		MqttPublish pubMsg = new MqttPublish(topic, message);
		comms.sendNoWait(pubMsg, token);

		// @TRACE 112=<

		waitForCompletion(token);
		return token;
	}
	
	/**
	 * Returns the current number of outgoing in-flight messages being sent by
	 * the client. Note that this number cannot be guaranteed to be 100%
	 * accurate as some messages may have been sent or queued in the time taken
	 * for this method to return.
	 * 
	 * @return the current number of in-flight messages.
	 */
	public int getInFlightMessageCount() {
		return this.comms.getActualInFlight();
	}
	
	@Override
	public boolean close() {
		try {
			close(false);
			return true;
		}catch (Exception e) {
		}
		return false;
	}
	
	public void close(boolean force) throws MqttException {
		// @TRACE 113=<
		if(force == false && comms.isConnected()) {
			Token disconnect = this.disconnect();
			waitForCompletion(disconnect);
		}
		
		comms.close(force);
		// @TRACE 114=>
	}
	
	@Override
	protected boolean initProxy() {
		try {
			return this.connect() != null;
		}catch (Exception e) {
		}
		return false;
	}

	@Override
	public String getKey() {
		return serverURI;
	}

	public int getMqttVersion() {
		return mqttVersion;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public int getMaxInflight() {
		return maxInflight;
	}

	public String getUserName() {
		return userName;
	}
	
	public char[] getPassword() {
		return password;
	}

	public NodeProxyMQTT withMqttVersion(int version) {
		this.mqttVersion = version;
		return this;
	}
}
