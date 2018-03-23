package de.uniks.networkparser.ext.petaf.proxy;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.net.DatagramPacket;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Server_UPD;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.interfaces.Server;

public class NodeProxyServer extends NodeProxy {
	public static final String PROPERTY_PORT = "port";
	private int port = 9876;
	private int bufferSize = 1024;
	private Server server;
	private String serverType;

	public NodeProxyServer(String type) {
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
		return new NodeProxyServer(NodeProxy.TYPE_IN);
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
		if(NodeProxy.isInput(this.type)) {
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
		NodeProxyServer proxy = new NodeProxyServer(NodeProxy.TYPE_IN).withPort(port);
		return proxy;
	}
}
