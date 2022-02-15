package de.uniks.networkparser.ext.petaf.proxy;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.io.MQTTMessage;
import de.uniks.networkparser.ext.io.MessageSession;
import de.uniks.networkparser.ext.io.RabbitMessage;
import de.uniks.networkparser.ext.io.ReaderComm;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * Proxy for Broker.
 *
 * @author Stefan Lindel
 */
public class NodeProxyBroker extends NodeProxy {
	
	/** The Constant PROPERTY_SERVERURL. */
	public static final String PROPERTY_SERVERURL = "url";
	
	/** The Constant PROPERTY_clientId. */
	public static final String PROPERTY_clientId = "clientId";
	private String sender;
	private String password;
	private String url;
	private String clientId;
	private boolean reconnecting = false;
	private MessageSession session;
	/* Standalone Variables */
	private ExecutorService executorService;
	private ReaderComm readerComm;
	private static final String CLIENTID_PREFIX = "np_broker";
	private String format = MessageSession.TYPE_AMQ;
	private int mqttVersion = MQTTMessage.MQTT_VERSION_3_1_1;
	private SimpleKeyValueList<String, String> topics = new SimpleKeyValueList<String, String>();
	private SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>> values;
	private ObjectCondition callBack;
	
	/** The Constant EVENT_CONNECT. */
	public static final String EVENT_CONNECT = "connected";
	
	/** The Constant EVENT_CONNECTLOST. */
	public static final String EVENT_CONNECTLOST = "ConnectionLost";
	
	/** The Constant EVENT_MESSAGE. */
	public static final String EVENT_MESSAGE = "Message";
	/** Lowest possible MQTT message ID to use */
	private static final int MIN_MSG_ID = 1; 
	 /** Highest possible MQTT message ID to use */
	private static final int MAX_MSG_ID = 65535;
	/* The next available message ID to use */
	private int nextMsgId = MIN_MSG_ID - 1; 

	/**
	 * Instantiates a new node proxy broker.
	 */
	public NodeProxyBroker() {
		this.property.addAll(PROPERTY_SERVERURL);
		this.propertyUpdate.addAll(PROPERTY_SERVERURL);
		this.propertyInfo.addAll(PROPERTY_SERVERURL);
	}

	/**
	 * Instantiates a new node proxy broker.
	 *
	 * @param serverURI the server URI
	 */
	public NodeProxyBroker(String serverURI) {
		this(serverURI, null);
	}

	/**
	 * With auth.
	 *
	 * @param sender the sender
	 * @param password the password
	 * @return the node proxy broker
	 */
	public NodeProxyBroker withAuth(String sender, String password) {
		this.sender = sender;
		this.password = password;
		return this;
	}

	/**
	 * Instantiates a new node proxy broker.
	 *
	 * @param url the url
	 * @param clientId the client id
	 */
	public NodeProxyBroker(String url, String clientId) {
		this.url = url;
		if (clientId == null) {
			this.clientId = NodeProxyBroker.generateClientId();
		}
	}

	/**
	 * Returns a randomly generated client identifier based on the the fixed prefix
	 * and the system time.
	 * 
	 * @return a generated client identifier
	 */
	public static String generateClientId() {
		return CLIENTID_PREFIX + System.nanoTime();
	}

	/**
	 * Connect.
	 *
	 * @return true, if successful
	 */
	public boolean connect() {
		if (session == null) {
			session = new MessageSession();
		}
		if (session.isClose() == false) {
			return false;
		}
		session.withHost(url);
		boolean success = false;
		if (MessageSession.TYPE_MQTT.equals(format)) {
			success = session.connectMQTT(this, clientId, sender, password, 60, mqttVersion, true);
		} else {
			/* Default MessageSession.TYPE_AMQ; */
			success = session.connectAMQ(this, sender, password);
		}
		if (success && callBack != null) {
			SimpleEvent event = new SimpleEvent(this, url, null, session).withType(NodeProxyBroker.EVENT_CONNECT);
			callBack.update(event);
		}
		return success;
	}

	/**
	 * Execute exception.
	 *
	 * @param e the e
	 */
	public void executeException(Exception e) {
		if (this.callBack != null) {
			SimpleEvent event = new SimpleEvent(this, url, null, session).withType(NodeProxyBroker.EVENT_CONNECTLOST);
			callBack.update(event);
		}
	}

	/**
	 * Gets the client id.
	 *
	 * @return the client id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Gets the server URI.
	 *
	 * @return the server URI
	 */
	public String getServerURI() {
		return url;
	}

	/**
	 * Checks if is sendable.
	 *
	 * @return true, if is sendable
	 */
	@Override
	public boolean isSendable() {
		return true;
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean close() {
		return close(false);
	}

	/**
	 * Close.
	 *
	 * @param force the force
	 * @return true, if successful
	 */
	public boolean close(boolean force) {
		/* @TRACE 113=< */
		if (session == null) {
			return true;
		}
		/* SEND CLOSE MESSAGES */
		if (MessageSession.TYPE_AMQ.equals(format)) {
			RabbitMessage msg;
			if (topics != null) {
				while (topics.size() > 0) {
					String channel = topics.removePos(topics.size() - 1);
					short no = Short.valueOf(channel);
					msg = RabbitMessage.createClose(no);
					session.sending(this, msg, false);
				}
			}
			msg = RabbitMessage.createClose((short) 0);
			session.sending(this, msg, false);
		} else if (MessageSession.TYPE_MQTT.equals(format)) {
			MQTTMessage msg = MQTTMessage.create(MQTTMessage.MESSAGE_TYPE_DISCONNECT);
			session.sending(this, msg, false);
		}
		if (executorService != null) {
			executorService.shutdownNow();
		}
		return session.close();
	}

	@Override
	protected boolean startProxy() {
		try {
			return this.connect();
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	public String getKey() {
		return url;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return sender;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the reconnecting.
	 *
	 * @return the reconnecting
	 */
	public boolean getReconnecting() {
		return reconnecting;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public NodeProxyBroker getSendableInstance(boolean prototyp) {
		return new NodeProxyBroker();
	}

	/**
	 * With callback.
	 *
	 * @param condition the condition
	 * @return the node proxy broker
	 */
	public NodeProxyBroker withCallback(ObjectCondition condition) {
		this.callBack = condition;
		return this;
	}

	/**
	 * Subscribe.
	 *
	 * @param topic the topic
	 * @param callBack the call back
	 * @return true, if successful
	 */
	public boolean subscribe(String topic, ObjectCondition callBack) {
		this.callBack = callBack;
		return subscribe(topic);
	}

	/**
	 * Consume.
	 *
	 * @param topic the topic
	 * @param condition the condition
	 * @return true, if successful
	 */
	public boolean consume(String topic, ObjectCondition condition) {
/*		SimpleKeyValueList<String, String> topics = getTopics();
		short channelNo = Short.valueOf(topics.get(topic));*/

		this.callBack = condition;
		executeConsume(topic, callBack);

/*		RabbitMessage message = RabbitMessage.createConsume(channelNo, topic, "", false, false, false, false, null);
		session.sending(this, message, true);*/
		return true;
	}

	/**
	 * Subscribe.
	 *
	 * @param topic the topic
	 * @return true, if successful
	 */
	public boolean subscribe(String topic) {
		if (session != null) {
			if (MessageSession.TYPE_AMQ.equals(format)) {
				RabbitMessage message;
				message = RabbitMessage.createChannelOpen(this, topic);
				if (session.sending(this, message, true) == null) {
					return false;
				}
				short channel = message.getChannel();

				message = RabbitMessage.createQueue(channel, topic, false, false, false, null);
				if (session.sending(this, message, true) == null) {
					return false;
				}
				executeConsume(topic, callBack);
				message = RabbitMessage.createConsume(channel, topic, "", false, true, false, false, null);
				session.sending(this, message, false);
				return true;
			}
			if (MessageSession.TYPE_MQTT.equals(format)) {
				MQTTMessage.createChannelOpen(topic);
				MQTTMessage register = MQTTMessage.createChannelOpen(topic);
				register.withNames(topic).withQOS(1);
				session.sending(this, register, false);

				executeConsume(topic, callBack);

				return true;
			}
		}
		return false;
	}

	private boolean executeConsume(String queue, ObjectCondition condition) {

		if (this.space == null && queue != null) {
			/* Make a now Thread */
			executorService = Executors.newScheduledThreadPool(1);
			this.readerComm = new ReaderComm();
			this.readerComm.withSession(session);
			this.readerComm.withChannel(queue);
			this.readerComm.withCondition(condition);
			this.readerComm.start(this, "Broker-Reader: " + queue);
			executorService.execute(readerComm);

			return true;
		}
		return false;
	}

	/**
	 * Publish.
	 *
	 * @param channel the channel
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean publish(String channel, String message) {
		if (MessageSession.TYPE_AMQ.equals(format)) {
			SimpleKeyValueList<String, String> topics = getTopics();
			short channelNo = Short.valueOf(topics.get(channel));

			RabbitMessage msg = RabbitMessage.createPublish(channelNo, "", channel, message.getBytes());
			session.sending(this, msg, false);

			msg = RabbitMessage.createPublishHeader(channelNo, message);
			session.sending(this, msg, false);

			msg = RabbitMessage.createPublishBody(channelNo, message);
			session.sending(this, msg, false);
			return true;
		} else if (MessageSession.TYPE_MQTT.equals(format)) {
			MQTTMessage msg = MQTTMessage.create(MQTTMessage.MESSAGE_TYPE_PUBLISH);
			msg.withNames(channel).createMessage(message);
			session.sending(this, msg, true);
			return true;
		}
		return false;
	}

	/**
	 * Bind exchange.
	 *
	 * @param exchange the exchange
	 * @param queue the queue
	 * @return true, if successful
	 */
	public boolean bindExchange(String exchange, String queue) {
		if (MessageSession.TYPE_AMQ.equals(format)) {
			SimpleKeyValueList<String, String> topics = getTopics();
			short channelNo;
			RabbitMessage msg;
			if (topics.get(exchange) != null) {
				channelNo = Short.valueOf(topics.get(exchange));
			} else {
				msg = RabbitMessage.createChannelOpen(this, exchange);
				session.sending(this, msg, false);
				channelNo = Short.valueOf(topics.get(exchange));
			}
			msg = RabbitMessage.createExange(channelNo, exchange, null);
			session.sending(this, msg, true);

			msg = RabbitMessage.createBind(channelNo, exchange, queue);
			session.sending(this, msg, true);
			return true;
		}
		return false;
	}

	/**
	 * With format.
	 *
	 * @param format the format
	 * @return the node proxy broker
	 */
	public NodeProxyBroker withFormat(String format) {
		this.format = format;
		return this;
	}

	/**
	 * Creates the MQTT broker.
	 *
	 * @param url the url
	 * @return the node proxy broker
	 */
	public static NodeProxyBroker createMQTTBroker(String url) {
		NodeProxyBroker broker = new NodeProxyBroker(url);
		broker.withFormat(MessageSession.TYPE_MQTT);
		return broker;
	}

	/**
	 * Gets the grammar.
	 *
	 * @param create the create
	 * @return the grammar
	 */
	public SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>> getGrammar(
			boolean create) {
		if (create == false) {
			return values;
		}
		values = new SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>>();
		return values;
	}

	/**
	 * Gets the topics.
	 *
	 * @return the topics
	 */
	public SimpleKeyValueList<String, String> getTopics() {
		if (topics == null) {
			topics = new SimpleKeyValueList<String, String>();
		}
		return topics;
	}

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Get the next MQTT message ID that is not already in use, and marks it as now
	 * being in use.
	 *
	 * @return the next MQTT message ID to use
	 */
	public int getNextMessageId() {
		/* Allow two complete passes of the message ID range. This gives any asynchronous releases a chance to occur */
		nextMsgId++;
		if (nextMsgId > MAX_MSG_ID) {
			nextMsgId = MIN_MSG_ID;
		}
		return nextMsgId;
	}
}
