/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 *    Ian Craggs - MQTT 3.1.1 support
 *    Ian Craggs - per subscription message handlers (bug 466579)
 *    Ian Craggs - ack control (bug 472172)
 *    James Sutton - Bug 459142 - WebSocket support for the Java client.
 *    James Sutton - Automatic Reconnect & Offline Buffering.
 */

package de.uniks.networkparser.ext.mqtt;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import de.uniks.networkparser.ext.mqtt.internal.ClientComms;
import de.uniks.networkparser.ext.mqtt.internal.ConnectActionListener;
import de.uniks.networkparser.ext.mqtt.internal.MqttDisconnect;
import de.uniks.networkparser.ext.mqtt.internal.MqttPublish;
import de.uniks.networkparser.ext.mqtt.internal.MqttSubscribe;
import de.uniks.networkparser.ext.mqtt.internal.MqttUnsubscribe;
import de.uniks.networkparser.ext.mqtt.internal.TCPNetworkModule;
import de.uniks.networkparser.ext.mqtt.internal.Token;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * Lightweight client for talking to an MQTT server using non-blocking methods
 * that allow an operation to run in the background.
 *
 * <p>
 * This class implements the non-blocking {@link IMqttAsyncClient} client
 * interface allowing applications to initiate MQTT actions and then carry on
 * working while the MQTT action completes on a background thread. This
 * implementation is compatible with all Java SE runtimes from 1.7 and up.
 * </p>
 * <p>
 * An application can connect to an MQTT server using:
 * </p>
 * <ul>
 * <li>A plain TCP socket
 * <li>A secure SSL/TLS socket
 * </ul>
 *
 * <p>
 * To enable messages to be delivered even across network and client restarts
 * messages need to be safely stored until the message has been delivered at the
 * requested quality of service. A pluggable persistence mechanism is provided
 * to store the messages.
 * </p>
 * <p>
 * By default {@link MqttDefaultFilePersistence} is used to store messages to a
 * file. If persistence is set to null then messages are stored in memory and
 * hence can be lost if the client, Java runtime or device shuts down.
 * </p>
 * <p>
 * If connecting with {@link MqttConnectOptions#setCleanSession(boolean)} set to
 * true it is safe to use memory persistence as all state is cleared when a
 * client disconnects. If connecting with cleanSession set to false in order to
 * provide reliable message delivery then a persistent message store such as the
 * default one should be used.
 * </p>
 * <p>
 * The message store interface is pluggable. Different stores can be used by
 * implementing the {@link MqttClientPersistence} interface and passing it to
 * the clients constructor.
 * </p>
 *
 * @see IMqttAsyncClient
 */
public class MqttClient {
	public static final String EVENT_CONNECT="connected";
	public static final String EVENT_CONNECTLOST = "ConnectionLost";
	public static final String EVENT_MESSAGE= "Message";

	private static final String CLIENT_ID_PREFIX = "paho";
	private static final long QUIESCE_TIMEOUT = 30000; // ms
	private static final char MIN_HIGH_SURROGATE = '\uD800';
	private static final char MAX_HIGH_SURROGATE = '\uDBFF';
	private String clientId;
	private String serverURI;
	protected ClientComms comms;
	private SimpleKeyValueList<String, MqttTopic> topics = new SimpleKeyValueList<String, MqttTopic>();
	private SimpleKeyValueList<String, MqttPublish> persistence;
	private boolean reconnecting = false;
	
	protected long timeToWait = -1;				// How long each method should wait for action to complete -1 Standard -2 deactive
	private ScheduledExecutorService executorService;

	/**
	 * Create an MqttAsyncClient that is used to communicate with an MQTT
	 * server.
	 * <p>
	 * The address of a server can be specified on the constructor.
	 * Alternatively a list containing one or more servers can be specified
	 * using the {@link MqttConnectOptions#setServerURIs(String[])
	 * setServerURIs} method on MqttConnectOptions.
	 *
	 * <p>
	 * The <code>serverURI</code> parameter is typically used with the the
	 * <code>clientId</code> parameter to form a key. The key is used to store
	 * and reference messages while they are being delivered. Hence the
	 * serverURI specified on the constructor must still be specified even if a
	 * list of servers is specified on an MqttConnectOptions object. The
	 * serverURI on the constructor must remain the same across restarts of the
	 * client for delivery of messages to be maintained from a given client to a
	 * given server or set of servers.
	 *
	 * <p>
	 * The address of the server to connect to is specified as a URI. Two types
	 * of connection are supported <code>tcp://</code> for a TCP connection and
	 * <code>ssl://</code> for a TCP connection secured by SSL/TLS. For example:
	 * </p>
	 * <ul>
	 * <li><code>tcp://localhost:1883</code></li>
	 * <li><code>ssl://localhost:8883</code></li>
	 * </ul>
	 * <p>
	 * If the port is not specified, it will default to 1883 for
	 * <code>tcp://</code>" URIs, and 8883 for <code>ssl://</code> URIs.
	 * </p>
	 *
	 * <p>
	 * A client identifier <code>clientId</code> must be specified and be less
	 * that 65535 characters. It must be unique across all clients connecting to
	 * the same server. The clientId is used by the server to store data related
	 * to the client, hence it is important that the clientId remain the same
	 * when connecting to a server if durable subscriptions or reliable
	 * messaging are required.
	 * <p>
	 * A convenience method is provided to generate a random client id that
	 * should satisfy this criteria - {@link #generateClientId()}. As the client
	 * identifier is used by the server to identify a client when it reconnects,
	 * the client must use the same identifier between connections if durable
	 * subscriptions or reliable delivery of messages is required.
	 * </p>
	 * <p>
	 * In Java SE, SSL can be configured in one of several ways, which the
	 * client will use in the following order:
	 * </p>
	 * <ul>
	 * <li><strong>Supplying an <code>SSLSocketFactory</code></strong> -
	 * applications can use
	 * {@link MqttConnectOptions#setSocketFactory(SocketFactory)} to supply a
	 * factory with the appropriate SSL settings.</li>
	 * <li><strong>SSL Properties</strong> - applications can supply SSL
	 * settings as a simple Java Properties using
	 * {@link MqttConnectOptions#setSSLProperties(Properties)}.</li>
	 * <li><strong>Use JVM settings</strong> - There are a number of standard
	 * Java system properties that can be used to configure key and trust
	 * stores.</li>
	 * </ul>
	 *
	 * <p>
	 * In Java ME, the platform settings are used for SSL connections.
	 * </p>
	 *
	 * <p>
	 * An instance of the default persistence mechanism
	 * {@link MqttDefaultFilePersistence} is used by the client. To specify a
	 * different persistence mechanism or to turn off persistence, use the
	 * {@link #MqttAsyncClient(String, String, MqttClientPersistence)}
	 * constructor.
	 *
	 * @param serverURI
	 *            the address of the server to connect to, specified as a URI.
	 *            Can be overridden using
	 *            {@link MqttConnectOptions#setServerURIs(String[])}
	 * @param clientId
	 *            a client identifier that is unique on the server being
	 *            connected to
	 * @throws IllegalArgumentException
	 *             if the URI does not start with "tcp://", "ssl://" or
	 *             "local://".
	 * @throws IllegalArgumentException
	 *             if the clientId is null or is greater than 65535 characters
	 *             in length
	 * @throws MqttException
	 *             if any other problem was encountered
	 */
	public MqttClient(String serverURI, String clientId) throws MqttException {
		this(serverURI, clientId, new SimpleKeyValueList<String, MqttPublish>(), null);
	}

	/**
	 * Create an MqttAsyncClient that is used to communicate with an MQTT
	 * server.
	 * <p>
	 * The address of a server can be specified on the constructor.
	 * Alternatively a list containing one or more servers can be specified
	 * using the {@link MqttConnectOptions#setServerURIs(String[])
	 * setServerURIs} method on MqttConnectOptions.
	 *
	 * <p>
	 * The <code>serverURI</code> parameter is typically used with the the
	 * <code>clientId</code> parameter to form a key. The key is used to store
	 * and reference messages while they are being delivered. Hence the
	 * serverURI specified on the constructor must still be specified even if a
	 * list of servers is specified on an MqttConnectOptions object. The
	 * serverURI on the constructor must remain the same across restarts of the
	 * client for delivery of messages to be maintained from a given client to a
	 * given server or set of servers.
	 *
	 * <p>
	 * The address of the server to connect to is specified as a URI. Two types
	 * of connection are supported <code>tcp://</code> for a TCP connection and
	 * <code>ssl://</code> for a TCP connection secured by SSL/TLS. For example:
	 * </p>
	 * <ul>
	 * <li><code>tcp://localhost:1883</code></li>
	 * <li><code>ssl://localhost:8883</code></li>
	 * </ul>
	 * <p>
	 * If the port is not specified, it will default to 1883 for
	 * <code>tcp://</code>" URIs, and 8883 for <code>ssl://</code> URIs.
	 * </p>
	 *
	 * <p>
	 * A client identifier <code>clientId</code> must be specified and be less
	 * that 65535 characters. It must be unique across all clients connecting to
	 * the same server. The clientId is used by the server to store data related
	 * to the client, hence it is important that the clientId remain the same
	 * when connecting to a server if durable subscriptions or reliable
	 * messaging are required.
	 * <p>
	 * A convenience method is provided to generate a random client id that
	 * should satisfy this criteria - {@link #generateClientId()}. As the client
	 * identifier is used by the server to identify a client when it reconnects,
	 * the client must use the same identifier between connections if durable
	 * subscriptions or reliable delivery of messages is required.
	 * </p>
	 * <p>
	 * In Java SE, SSL can be configured in one of several ways, which the
	 * client will use in the following order:
	 * </p>
	 * <ul>
	 * <li><strong>Supplying an <code>SSLSocketFactory</code></strong> -
	 * applications can use
	 * {@link MqttConnectOptions#setSocketFactory(SocketFactory)} to supply a
	 * factory with the appropriate SSL settings.</li>
	 * <li><strong>SSL Properties</strong> - applications can supply SSL
	 * settings as a simple Java Properties using
	 * {@link MqttConnectOptions#setSSLProperties(Properties)}.</li>
	 * <li><strong>Use JVM settings</strong> - There are a number of standard
	 * Java system properties that can be used to configure key and trust
	 * stores.</li>
	 * </ul>
	 *
	 * <p>
	 * In Java ME, the platform settings are used for SSL connections.
	 * </p>
	 * <p>
	 * A persistence mechanism is used to enable reliable messaging. For
	 * messages sent at qualities of service (QoS) 1 or 2 to be reliably
	 * delivered, messages must be stored (on both the client and server) until
	 * the delivery of the message is complete. If messages are not safely
	 * stored when being delivered then a failure in the client or server can
	 * result in lost messages. A pluggable persistence mechanism is supported
	 * via the {@link MqttClientPersistence} interface. An implementer of this
	 * interface that safely stores messages must be specified in order for
	 * delivery of messages to be reliable. In addition
	 * {@link MqttConnectOptions#setCleanSession(boolean)} must be set to false.
	 * In the event that only QoS 0 messages are sent or received or
	 * cleanSession is set to true then a safe store is not needed.
	 * </p>
	 * <p>
	 * An implementation of file-based persistence is provided in class
	 * {@link MqttDefaultFilePersistence} which will work in all Java SE based
	 * systems. If no persistence is needed, the persistence parameter can be
	 * explicitly set to <code>null</code>.
	 * </p>
	 *
	 * @param serverURI
	 *            the address of the server to connect to, specified as a URI.
	 *            Can be overridden using
	 *            {@link MqttConnectOptions#setServerURIs(String[])}
	 * @param clientId
	 *            a client identifier that is unique on the server being
	 *            connected to
	 * @param persistence
	 *            the persistence class to use to store in-flight message. If
	 *            null then the default persistence mechanism is used
	 * @param pingSender
	 *            Custom {@link MqttPingSender} implementation.
	 * @param executorService
	 *            used for managing threads. If null then a newFixedThreadPool
	 *            is used.
	 * @throws IllegalArgumentException
	 *             if the URI does not start with "tcp://", "ssl://" or
	 *             "local://"
	 * @throws IllegalArgumentException
	 *             if the clientId is null or is greater than 65535 characters
	 *             in length
	 * @throws MqttException
	 *             if any other problem was encountered
	 */
	public MqttClient(String serverURI, String clientId, SimpleKeyValueList<String, MqttPublish> persistence,
			ScheduledExecutorService executorService) throws MqttException {
		if (clientId == null) { // Support empty client Id, 3.1.1 standard
			throw new IllegalArgumentException("Null clientId");
		}
		// Count characters, surrogate pairs count as one character.
		int clientIdLength = 0;
		for (int i = 0; i < clientId.length() - 1; i++) {
			if (Character_isHighSurrogate(clientId.charAt(i)))
				i++;
			clientIdLength++;
		}
		if (clientIdLength > 65535) {
			throw new IllegalArgumentException("ClientId longer than 65535 characters");
		}

		MqttConnectOptions.validateURI(serverURI);

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

		this.comms = new ClientComms(this, this.persistence, this.executorService);
	}

	/**
	 * @param ch
	 *            the character to check.
	 * @return returns 'true' if the character is a high-surrogate code unit
	 */
	protected static boolean Character_isHighSurrogate(char ch) {
		return (ch >= MIN_HIGH_SURROGATE) && (ch <= MAX_HIGH_SURROGATE);
	}

	/**
	 * Factory method to create an array of network modules, one for each of the
	 * supplied URIs
	 *
	 * @param address
	 *            the URI for the server.
	 * @param options
	 *            the {@link MqttConnectOptions} for the connection.
	 * @return a network module appropriate to the specified address.
	 * @throws MqttException
	 *             if an exception occurs creating the network Modules
	 * @throws MqttSecurityException
	 *             if an issue occurs creating an SSL / TLS Socket
	 */
	protected TCPNetworkModule[] createNetworkModules(String address, MqttConnectOptions options)
			throws MqttException {
		// @TRACE 116=URI={0}

		TCPNetworkModule[] networkModules = null;
		String[] serverURIs = options.getServerURIs();
		String[] array = null;
		if (serverURIs == null) {
			array = new String[] { address };
		} else if (serverURIs.length == 0) {
			array = new String[] { address };
		} else {
			array = serverURIs;
		}

		networkModules = new TCPNetworkModule[array.length];
		for (int i = 0; i < array.length; i++) {
			networkModules[i] = createNetworkModule(array[i], options);
		}

		return networkModules;
	}

	/**
	 * Factory method to create the correct network module, based on the
	 * supplied address URI.
	 *
	 * @param address the URI for the server.
	 * @param options Connect options
	 * @return a network module appropriate to the specified address.
	 */
	private TCPNetworkModule createNetworkModule(String address, MqttConnectOptions options) throws MqttException {
		// @TRACE 115=URI={0}

		TCPNetworkModule netModule;
		SocketFactory factory = options.getSocketFactory();

		int serverURIType = MqttConnectOptions.validateURI(address);

		URI uri;
		try {
			uri = new URI(address);
			// If the returned uri contains no host and the address contains underscores,
			// then it's likely that Java did not parse the URI
			if(uri.getHost() == null && address.contains("_")){
				try {
					final Field hostField = URI.class.getDeclaredField("host");
					hostField.setAccessible(true);
					// Get everything after the scheme://
					String shortAddress = address.substring(uri.getScheme().length() + 3);
					hostField.set(uri, getHostName(shortAddress));
					
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, e.getCause());
				} 
				
			}
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Malformed URI: " + address + ", " + e.getMessage());
		}

		String host = uri.getHost();
		int port = uri.getPort(); // -1 if not defined

		switch (serverURIType) {
		case MqttConnectOptions.URI_TYPE_TCP :
			if (port == -1) {
				port = 1883;
			}
			if (factory == null) {
				factory = SocketFactory.getDefault();
			}
			else if (factory instanceof SSLSocketFactory) {
				throw MqttException.withReason(MqttException.REASON_CODE_SOCKET_FACTORY_MISMATCH);
			}
			netModule = new TCPNetworkModule(factory, host, port, clientId);
			((TCPNetworkModule)netModule).setConnectTimeout(options.getConnectionTimeout());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#connect()
	 */
	public Token connect() throws MqttException {
		return this.connect(new MqttConnectOptions());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttAsyncClient#connect(org.eclipse.paho.
	 * client.mqttv3.MqttConnectOptions)
	 */
	public Token connect(MqttConnectOptions options) throws MqttException {
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
		if (options == null) {
			options = new MqttConnectOptions();
		}
		// @TRACE 103=cleanSession={0} connectionTimeout={1} TimekeepAlive={2}
		// userName={3} password={4} will={5} userContext={6} callback={7}
		comms.setNetworkModules(createNetworkModules(serverURI, options));
//		comms.setReconnectCallback(new MqttReconnectCallback(automaticReconnect));

		// Insert our own callback to iterate through the URIs till the connect
		// succeeds
		Token userToken = new Token(getClientId());
		ConnectActionListener connectActionListener = new ConnectActionListener(this, persistence, comms, options,
				userToken, reconnecting);

		// If we are using the MqttCallbackExtended, set it on the
		connectActionListener.setMqttCallback(comms.getCallback());

		comms.setNetworkModuleIndex(0);
		connectActionListener.connect();
		
		waitForCompletion(userToken);
		return userToken;
	}
	
	protected void waitForCompletion(Token token) throws MqttException {
		if(this.timeToWait>-2) {
			token.waitForCompletion(timeToWait);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#disconnect()
	 */
	public Token disconnect() throws MqttException {
		return this.disconnect(QUIESCE_TIMEOUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#disconnect(long)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see IMqttAsyncClient#isConnected()
	 */
	public boolean isConnected() {
		return comms.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IMqttAsyncClient#getClientId()
	 */
	public String getClientId() {
		return clientId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IMqttAsyncClient#getServerURI()
	 */
	public String getServerURI() {
		return serverURI;
	}

	/**
	 * Returns the currently connected Server URI Implemented due to:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=481097
	 *
	 * Where getServerURI only returns the URI that was provided in
	 * MqttAsyncClient's constructor, getCurrentServerURI returns the URI of the
	 * Server that the client is currently connected to. This would be different
	 * in scenarios where multiple server URIs have been provided to the
	 * MqttConnectOptions.
	 *
	 * @return the currently connected server URI
	 */
	public String getCurrentServerURI() {
		return comms.getNetworkModules()[comms.getNetworkModuleIndex()].getServerURI();
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
	 * @param topic
	 *            the topic to use, for example "finance/stock/ibm".
	 * @return an MqttTopic object, which can be used to publish messages to the
	 *         topic.
	 * @throws IllegalArgumentException
	 *             if the topic contains a '+' or '#' wildcard character.
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

	/*
	 * @see IMqttClient#subscribe(String)
	 */
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#subscribe(java.lang.
	 * String, int)
	 */
	public Token subscribe(String topicFilter, int qos) throws MqttException {
		return this.subscribe(new String[] { topicFilter }, new int[] { qos }, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#subscribe(java.lang.
	 * String[], int[], java.lang.Object,
	 * org.eclipse.paho.client.mqttv3.IMqttActionListener)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttAsyncClient#unsubscribe(java.lang.
	 * String[])
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see IMqttAsyncClient#setCallback(MqttCallback)
	 */
	public void setCallback(SimpleEventCondition callback) {
		comms.setCallback(callback);
	}

	public void messageArrivedComplete(int messageId, int qos) throws MqttException {
		comms.messageArrivedComplete(messageId, qos);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttAsyncClient#publish(java.lang.String,
	 * org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	public Token publish(String topic, MqttMessage message)
			throws MqttException {
		return this.publish(topic, message, null);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttAsyncClient#publish(java.lang.String,
	 * org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	public Token publish(String topic, byte...message)
			throws MqttException {
		MqttMessage mqttMessage = new MqttMessage(message);
		return this.publish(topic, mqttMessage, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.IMqttAsyncClient#publish(java.lang.String,
	 * org.eclipse.paho.client.mqttv3.MqttMessage, java.lang.Object,
	 * org.eclipse.paho.client.mqttv3.IMqttActionListener)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#close()
	 */
	public void close() throws MqttException {
		close(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.IMqttAsyncClient#close()
	 */
	public void close(boolean force) throws MqttException {
		// @TRACE 113=<
		if(force == false && comms.isConnected()) {
			Token disconnect = this.disconnect();
			waitForCompletion(disconnect);
		}
		
		comms.close(force);
		// @TRACE 114=>
	}
}
