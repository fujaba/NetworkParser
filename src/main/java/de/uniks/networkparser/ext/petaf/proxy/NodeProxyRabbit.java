package de.uniks.networkparser.ext.petaf.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.uniks.networkparser.ext.io.MessageSession;
import de.uniks.networkparser.ext.io.RabbitMessage;
import de.uniks.networkparser.ext.io.ReaderComm;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class NodeProxyRabbit extends NodeProxy {
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

	public NodeProxyRabbit() {
		this.property.addAll(PROPERTY_SERVERURL);
		this.propertyUpdate.addAll(PROPERTY_SERVERURL);
		this.propertyInfo.addAll(PROPERTY_SERVERURL);
	}

	public NodeProxyRabbit(String serverURI) {
		this(serverURI, null);
	}

	public NodeProxyRabbit(String url, String clientId) {
		this.url = url;
//		if (clientId == null) {
//			clientId = NodeProxyRabbit.generateClientId();
//		}
	}

	public boolean connect() {
		if (session == null) {
			session = new MessageSession();
		}
		if(session.isClose() == false) {
			return false;
		}
		session.withHost(url);
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
	public NodeProxyRabbit getSendableInstance(boolean prototyp) {
		return new NodeProxyRabbit();
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
				this.readerComm.withThreadName("Rabbit-Reader: "+queue);
				executorService.execute(readerComm);
			}
		}
		return false;
	}
	
	public boolean publish(String channel, String message) {
		RabbitMessage msg = RabbitMessage.createPublish("", channel, message.getBytes());
		RabbitMessage respone = session.sending(msg, true);
		return respone != null;
	}
}
