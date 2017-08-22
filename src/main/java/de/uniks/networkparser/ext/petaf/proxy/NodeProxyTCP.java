package de.uniks.networkparser.ext.petaf.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import de.uniks.networkparser.ext.petaf.network.Message;
import de.uniks.networkparser.ext.petaf.network.NodeProxy;
import de.uniks.networkparser.ext.petaf.network.NodeProxyTCPServer;
import de.uniks.networkparser.ext.petaf.network.NodeProxyType;

public class NodeProxyTCP extends NodeProxy {
	public static final String PROPERTY_URL = "url";
	public static final String PROPERTY_PORT = "port";
	private int port;
	private String url;
	public static final String LOCALHOST = "127.0.0.1";

	protected NodeProxyTCPServer serverSocket;

	public NodeProxyTCP() {
		this.property.addAll(PROPERTY_URL, PROPERTY_PORT);
		this.propertyUpdate.addAll(PROPERTY_URL, PROPERTY_PORT);
		this.propertyInfo.addAll(PROPERTY_URL, PROPERTY_PORT);
	}

	public String getUrl() {
		return url;
	}

	public NodeProxyTCP withUrl(String value) {
		String oldValue = value;
		this.url = value;
		firePropertyChange(PROPERTY_URL, oldValue, value);
		return this;
	}

	public NodeProxyTCP withURLPort(String url, int port) {
		withUrl(url);
		withPort(port);
		return this;
	}

	@Override
	public String getKey() {
		return url + ":" + port;
	}

	public Integer getPort() {
		return port;
	}

	public NodeProxyTCP withPort(int value) {
		int oldValue = value;
		this.port = value;
		firePropertyChange(PROPERTY_PORT, oldValue, value);
		return this;
	}

	@Override
	public Object getValue(Object element, String attrName) {
		if(element instanceof NodeProxyTCP ) {
			NodeProxyTCP nodeProxy = (NodeProxyTCP) element;
			if (PROPERTY_URL.equals(attrName)) {
				return nodeProxy.getUrl();
			}
			if (PROPERTY_PORT.equals(attrName)) {
				return nodeProxy.getPort();
			}
		}
		return super.getValue(element, attrName);
	}

	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if(element instanceof NodeProxyTCP ) {
			NodeProxyTCP nodeProxy = (NodeProxyTCP) element;
			if (PROPERTY_URL.equals(attrName)) {
				nodeProxy.withUrl((String) value);
				return true;
			}
			if (PROPERTY_PORT.equals(attrName)) {
				nodeProxy.withPort((Integer) value);
				return true;
			}
		}
		return super.setValue(element, attrName, value, type);
	}

	public void updateFromMessage() {

	}

	@Override
	protected boolean postSending(Message msg) {
		if (super.postSending(msg) || this.space == null) {
			return true;
		}
		boolean success = false;
		try {
			if (url != null && (msg.isSendAnyHow() || isOnline())) {
				InetAddress addr = InetAddress.getByName(url);
				Socket requestSocket = new Socket(addr, port);
				if (msg.getTimeOut() > Message.TIMEOUTDEFAULT) {
					requestSocket.setSoTimeout(msg.getTimeOut());
				}
				OutputStream os = requestSocket.getOutputStream();

				byte[] buffer = this.space.convertMessage(msg).getBytes();
				int start = 0;
				int size = BUFFER;
				while (true) {
					int end = start + BUFFER;
					if (end > buffer.length) {
						size = buffer.length - start;
						os.write(buffer, start, size);
						break;
					} else {
						os.write(buffer, start, size);
					}
					start = end;
				}
				os.flush();

				requestSocket.close();
				setSendTime(buffer.length);
				success = true;
			}
		} catch (IOException ioException) {
			// could not reach the proxy, mark it as offline
			this.withOnline(false);
			success = false;
		}
		return success;
	}

	@Override
	public boolean close() {
		if (this.serverSocket != null) {
			this.serverSocket.closeServer();
		}
		return true;
	}

	@Override
	protected boolean initProxy() {
		if (url == null && getType() == null || getType() == NodeProxyType.IN) {
			// Incoming Proxy
			withType(NodeProxyType.IN);
			serverSocket = new NodeProxyTCPServer(this);
			if (url == null) {
				try {
					String url = InetAddress.getLocalHost().getHostAddress();
					if (LOCALHOST.equals(url) == false) {
						this.url = url;
					}
				} catch (UnknownHostException e) {
				}
			}
		} else {
			withType(NodeProxyType.OUT);
			if (url == null) {
				try {
					url = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
				}
				// NodeProxyTCP result = createProxy(url, port);
			}
		}
		return true;
	}

	@Override
	public boolean isSendable() {
		return url != null;
	}

	public static NodeProxyTCP create(String url, int port) {
		NodeProxyTCP proxy = new NodeProxyTCP().withURLPort(url, port);
		return proxy;
	}

	public static NodeProxyTCP createServer(int port) {
		NodeProxyTCP proxy = new NodeProxyTCP().withPort(port);
		proxy.withType(NodeProxyType.INOUT);
		return proxy;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new NodeProxyTCP();
	}

}
