package de.uniks.networkparser.ext.petaf.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniks.networkparser.ext.io.MessageSession;
import de.uniks.networkparser.ext.io.RabbitMessage;
import de.uniks.networkparser.ext.io.ReaderComm;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;

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
			clientId = NodeProxyBroker.generateClientId();
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
		if(MessageSession.TYPE_MQTT.equals(format)) {
			return session.connectMQTT(sender, password);
		}
		// Default MessageSession.TYPE_AMQ;
		return session.connectAMQ(sender, password);
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
		RabbitMessage msg;
		while(RabbitMessage.channel > 0) {
			msg = RabbitMessage.createClose();
			session.sending(msg, false);
		}
		msg = RabbitMessage.createClose();
		session.sending(msg, false);
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
	
	public boolean createChannel(String queue, ObjectCondition condition) {
		if(session != null) {
			RabbitMessage message;
			message = RabbitMessage.createChannelOpen(null);
			if(session.sending(message, true) == null) {
				return false;
			}

			message = RabbitMessage.createQueue(queue, false, false, false, null);
			if(session.sending(message, true) == null) {
				return false;
			}

			if(this.space == null) {
				// Make a now Thread
				executorService = Executors.newScheduledThreadPool(1);
				this.readerComm = new ReaderComm();
				this.readerComm.withSession(session);
				this.readerComm.withChannel(queue);
				this.readerComm.withCondition(condition);
				this.readerComm.start("Rabbit-Reader: "+queue);
				executorService.execute(readerComm);
				
				message = RabbitMessage.createConsume(queue, "", false, false, false, false, null);
				session.sending(message, false);
				return true;
			}
		}
		return false;
	}
	
	public boolean publish(String channel, String message) {
		RabbitMessage msg = RabbitMessage.createPublish("", channel, message.getBytes());
		session.sending(msg, false);
		
		msg = RabbitMessage.createPublishHeader(message);
		session.sending(msg, false);

		msg = RabbitMessage.createPublishBody(message);
		session.sending(msg, false);
		return true;
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

}
