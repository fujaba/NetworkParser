package de.uniks.networkparser.ext.mqtt.internal;

public class Connection implements Runnable {
	// Kick off the connect processing in the background so that it does not block. For instance
	// the socket could take time to create.
	private ClientComms clientComms = null;
	private Token token;
	private MqttWireMessage message;
	private String threadName;
	private long quiesceTimeout;

	public Connection(ClientComms cc, Token cToken, MqttWireMessage cPacket) {
		clientComms = cc;
		token 	= cToken;
		message 	= cPacket;
		threadName = "MQTT Con: "+clientComms.getClient().getClientId();
	}
	
	public Connection(ClientComms cc, Token cToken, MqttWireMessage cPacket, long quiesceTimeout) {
		clientComms = cc;
		token 	= cToken;
		message 	= cPacket;
		threadName = "MQTT Disc: "+clientComms.getClient().getClientId();
	}

	public void start() {
		clientComms.getExecutorService().execute(this);
	}

	public void run() {
		Thread.currentThread().setName(threadName);
		if(message.getType() == MqttWireMessage.MESSAGE_TYPE_CONNECT) {
			this.clientComms.startConnection(token, message);
		} else {
			this.clientComms.startDisconnect(token, message, quiesceTimeout);
		}
	}
}
