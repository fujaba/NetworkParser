package de.uniks.networkparser.ext.petaf.proxy;

import java.net.DatagramPacket;

import de.uniks.networkparser.ext.petaf.network.BasicSpace;
import de.uniks.networkparser.ext.petaf.network.NodeProxy;
import de.uniks.networkparser.ext.petaf.network.NodeProxyType;
import de.uniks.networkparser.ext.petaf.network.NodeProxyUDPServer;

public class NodeProxyBroadCast extends NodeProxy {
	public static final String PROPERTY_PORT = "port";
	private int port = 9876;
	private int bufferSize = 1024;
	private NodeProxyUDPServer server;
	
	public NodeProxyBroadCast(NodeProxyType type) {
		super();
		this.type = type;
		this.property.addAll(PROPERTY_PORT);
		this.propertyUpdate.addAll(PROPERTY_PORT);
		this.propertyInfo.addAll(PROPERTY_PORT);
	}

	
	public DatagramPacket executeBroadCast(boolean async) {
		if(async) {
			this.server = new NodeProxyUDPServer(this, true);
		} else {
			this.server = new NodeProxyUDPServer(this, false);
			return this.server.runClient();
		}
		return null;
	}
	
	public NodeProxyBroadCast withAnswerSize(int answerSize) {
		this.bufferSize = answerSize;
		return this;
	}
	public NodeProxyBroadCast withPort(int value) {
		this.port = value;
		return this;
	}
	public NodeProxyBroadCast withSpace(BasicSpace value) {
		this.space = value;
		return this;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NodeProxyBroadCast(NodeProxyType.IN);
	}

	@Override
	public String getKey() {
		return "udp:"+port;
	}

	@Override
	public boolean isSendable() {
		return true;
	}

	@Override
	public boolean close() {
		if(server != null) {
			return this.server.closeServer();
		}
		return true;
	}
	
	public int getPort() {
		return port;
	}
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	protected boolean initProxy() {
		// May be Server or Client
		if(NodeProxyType.isInput(this.type)) {
			this.server = new NodeProxyUDPServer(this, true);
		}
		return true;
	}

	public static NodeProxy createServer(int port) {
		NodeProxyBroadCast proxy = new NodeProxyBroadCast(NodeProxyType.IN).withPort(port);
		return proxy;
	}
}
