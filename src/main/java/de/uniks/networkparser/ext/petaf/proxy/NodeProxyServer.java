package de.uniks.networkparser.ext.petaf.proxy;

import java.net.DatagramPacket;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;
import de.uniks.networkparser.ext.petaf.Server_UPD;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.Server;

public class NodeProxyServer extends NodeProxy {
	
	public static final String PROPERTY_PORT = "port";
	private int port = 9876;
	private int bufferSize = 1024;
	private Server server;
	private String serverType;
	
	public NodeProxyServer(NodeProxyType type) {
		super();
		this.type = type;
		this.property.addAll(PROPERTY_PORT);
		this.propertyUpdate.addAll(PROPERTY_PORT);
		this.propertyInfo.addAll(PROPERTY_PORT);
	}
	
	public NodeProxyServer withServerType(String type) {
		this.serverType = type;
		return this;
	}

	
	public DatagramPacket executeBroadCast(boolean async) {
		if(async) {
			this.server = new Server_UPD(this, true);
		} else {
			Server_UPD server = new Server_UPD(this, false);
			this.server = server;
			return server.runClient();
		}
		return null;
	}
	
	public NodeProxyServer withAnswerSize(int answerSize) {
		this.bufferSize = answerSize;
		return this;
	}
	public NodeProxyServer withPort(int value) {
		this.port = value;
		return this;
	}
	public NodeProxyServer withSpace(Space value) {
		this.space = value;
		return this;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NodeProxyServer(NodeProxyType.IN);
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
			return this.server.close();
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
			if(Server.TCP.equals(this.serverType)) {
				
			} else if(Server.TIME.equals(this.serverType)) {
		    } else if(Server.REST.equals(this.serverType)) {
		    	Space space = this.getSpace();
		    	if(space!=null) {
		    		IdMap map = space.getMap();
		    		NodeProxyModel model = space.getModel();
		    		Object root = model.getModel();
		    		this.server = new RESTServiceTask(port, map, root);
		    	}
		    } else {
		    	//} else if(Server.BROADCAST) {
		    	this.server = new Server_UPD(this, true);
		    }
		}
		return true;
	}

	public static NodeProxy createServer(int port) {
		NodeProxyServer proxy = new NodeProxyServer(NodeProxyType.IN).withPort(port);
		return proxy;
	}
}
