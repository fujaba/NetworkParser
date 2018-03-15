package de.uniks.networkparser.ext.petaf.proxy;

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

public class NodeProxyBroker extends NodeProxy {
	public static final String PROPERTY_SERVERURL = "url";
	public static final String PROPERTY_clientId = "clientId";
	private String sender;
	private String password;
	private String url;
	private String clientId;
	private boolean reconnecting = false;
	private MessageSession session;
	// Standalone Variables
	private ExecutorService executorService;
	private ReaderComm readerComm;
	private static final String CLIENTID_PREFIX = "np_broker";
	private String format = MessageSession.TYPE_AMQ;
	private int mqttVersion = MQTTMessage.MQTT_VERSION_3_1_1;
	private SimpleKeyValueList<String, String> topics = new SimpleKeyValueList<String, String>();
	private SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>> values;
	private ObjectCondition callBack;
	public static final String EVENT_CONNECT = "connected";
	public static final String EVENT_CONNECTLOST = "ConnectionLost";
	public static final String EVENT_MESSAGE = "Message";
	private static final int MIN_MSG_ID = 1;		// Lowest possible MQTT message ID to use
	private static final int MAX_MSG_ID = 65535;	// Highest possible MQTT message ID to use
	private int nextMsgId = MIN_MSG_ID - 1;			// The next available message ID to use

	public NodeProxyBroker() {
		this.property.addAll(PROPERTY_SERVERURL);
		this.propertyUpdate.addAll(PROPERTY_SERVERURL);
		this.propertyInfo.addAll(PROPERTY_SERVERURL);
	}

	public NodeProxyBroker(String serverURI) {
		this(serverURI, null);
	}

	public NodeProxyBroker(String url, String clientId) {
		this.url = url;
		if (clientId == null) {
			this.clientId = NodeProxyBroker.generateClientId();
		}
	}
	
	/**
	 * Returns a randomly generated client identifier based on the the fixed prefix and the system time.
	 * @return a generated client identifier
	 */
	public static String generateClientId() {
		return CLIENTID_PREFIX + System.nanoTime();
	}


	public boolean connect() {
		if (session == null) {
			session = new MessageSession();
		}
		if(session.isClose() == false) {
			return false;
		}
		session.withHost(url);
		boolean success = false;
		if(MessageSession.TYPE_MQTT.equals(format)) {
			success = session.connectMQTT(this, clientId, sender, password, 60, mqttVersion, true);
		} else {
			// Default MessageSession.TYPE_AMQ;
			success = session.connectAMQ(this, sender, password);
		}
		if(success  && callBack != null) {
			SimpleEvent event = new SimpleEvent(this, url, null, session).withType(NodeProxyBroker.EVENT_CONNECT);
			callBack.update(event);
		}
		return success;
	}

	public String getClientId() {
		return clientId;
	}

	public String getServerURI() {
		return url;
	}

	@Override
	public boolean isSendable() {
		return true;
	}
	

	@Override
	public boolean close() {
		return close(false);
	}

	public boolean close(boolean force) {
		// @TRACE 113=<
		if(session == null) {
			return true;
		}
		// SEND CLOSE MESSAGES
		if(MessageSession.TYPE_AMQ.equals(format)) {
			RabbitMessage msg;
			if(topics != null) {
				while(topics.size() > 0) {
					String channel = topics.removePos(topics.size() - 1);
					short no = Short.valueOf(channel);
					msg = RabbitMessage.createClose(no);
					session.sending(this, msg, false);
				}
			}
			msg = RabbitMessage.createClose((short)0);
			session.sending(this, msg, false);
		} else if(MessageSession.TYPE_MQTT.equals(format)) {
			MQTTMessage msg = MQTTMessage.create(MQTTMessage.MESSAGE_TYPE_DISCONNECT);
			session.sending(this, msg, false);
		}
		return session.close();
	}

	@Override
	protected boolean initProxy() {
		try {
			return this.connect();
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public String getKey() {
		return url;
	}

	public String getUserName() {
		return sender;
	}

	public String getPassword() {
		return password;
	}

	public boolean getReconnecting() {
		return reconnecting;
	}

	@Override
	public NodeProxyBroker getSendableInstance(boolean prototyp) {
		return new NodeProxyBroker();
	}
	
	public NodeProxyBroker withCallback(ObjectCondition condition) {
		this.callBack = condition;
		return this;
	}
	
	public boolean subscribe(String topic) {
		if(session != null) {
			if(MessageSession.TYPE_AMQ.equals(format)) {
				RabbitMessage message;
				message = RabbitMessage.createChannelOpen(this, topic);
				if(session.sending(this, message, true) == null) {
					return false;
				}
				short channel = message.getChannel();
	
				message = RabbitMessage.createQueue(channel, topic, false, false, false, null);
				if(session.sending(this, message, true) == null) {
					return false;
				}
				startConsume(topic, callBack);
				message = RabbitMessage.createConsume(channel, topic, "", false, false, false, false, null);
				session.sending(this, message, false);
				return true;
			}
			if(MessageSession.TYPE_MQTT.equals(format)) {
				MQTTMessage.createChannelOpen(topic);
				MQTTMessage register = MQTTMessage.createChannelOpen(topic);
				register.withNames(topic).withQOS(1);
				session.sending(this, register, false);

				startConsume(topic, callBack);

				return true;
			}
			// NOW ADdfknhklasfjhzgzuni gr 8bfuih89
		}
		return false;
	}
	
	private boolean startConsume(String queue, ObjectCondition condition) {

		if(this.space == null) {
			// Make a now Thread
			executorService = Executors.newScheduledThreadPool(1);
			this.readerComm = new ReaderComm();
			this.readerComm.withSession(session);
			this.readerComm.withChannel(queue);
			this.readerComm.withCondition(condition);
			this.readerComm.start(this, "Broker-Reader: "+queue);
			executorService.execute(readerComm);
			
			return true;
		}
		return false;
	}
	
	public boolean publish(String channel, String message) {
		if(MessageSession.TYPE_AMQ.equals(format)) {
			SimpleKeyValueList<String, String> topics = getTopics();
			short channelNo = Short.valueOf(topics.get(channel));
	
			RabbitMessage msg = RabbitMessage.createPublish(channelNo, "", channel, message.getBytes());
			session.sending(this, msg, false);
			
			msg = RabbitMessage.createPublishHeader(channelNo, message);
			session.sending(this, msg, false);
	
			msg = RabbitMessage.createPublishBody(channelNo, message);
			session.sending(this, msg, false);
			return true;
		} else if(MessageSession.TYPE_MQTT.equals(format)) {
			MQTTMessage msg = MQTTMessage.create(MQTTMessage.MESSAGE_TYPE_PUBLISH);
			msg.withNames(channel).createMessage(message);
			session.sending(this, msg, true);
			return true;
		}
		return false;
	}
	
	public NodeProxyBroker withFormat(String format) {
		this.format = format;
		return this;
	}
	
	public static NodeProxyBroker createMQTTBroker(String url) {
		NodeProxyBroker broker = new NodeProxyBroker(url);
		broker.withFormat(MessageSession.TYPE_MQTT);
		return broker;
	}

	public SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>> getGrammar(boolean create) {
		if(create == false) {
			return values;
		}
		values = new SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>>();
		return values;
	}

	public SimpleKeyValueList<String, String> getTopics() {
		if(topics == null) {
			topics = new SimpleKeyValueList<String, String>();
		}
		return topics;
	}

	public String getFormat() {
		return format;
	}

	/**
	 * Get the next MQTT message ID that is not already in use, and marks
	 * it as now being in use.
	 *
	 * @return the next MQTT message ID to use
	 */
	public int getNextMessageId() {
//		int startingMessageId = nextMsgId;
		// Allow two complete passes of the message ID range. This gives
		// any asynchronous releases a chance to occur
//		int loopCount = 0;
		nextMsgId++;
		if ( nextMsgId > MAX_MSG_ID ) {
			nextMsgId = MIN_MSG_ID;
		}
//		Integer id = Integer.valueOf(nextMsgId);
		return nextMsgId;
	}
}
