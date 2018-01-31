package de.uniks.networkparser.ext.petaf.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.Server_TCP;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.xml.HTMLEntity;

public class NodeProxyTCP extends NodeProxy {
	public static int BUFFER=100*1024;
	public static final String POST="POST";
	public static final String GET="GET";
	public static final String PUT = "PUT";
	public static final String PATCH = "PATCH";
	public static final String DELETE = "DELETE";
	
	public static final String PROPERTY_URL = "url";
	public static final String PROPERTY_PORT = "port";
	protected int port;
	protected String url;
	public static final String LOCALHOST = "127.0.0.1";
	protected Server_TCP serverSocket;
	protected boolean allowAnswer = false;

	/**
	 * Fallback Executor for Simple Using Serverclasses
	 */
	private ObjectCondition listener;

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

	public NodeProxyTCP withAllowAnswer(boolean value) {
		this.allowAnswer = value;
		return this;
	}

	public boolean isAllowAnswer() {
		return allowAnswer;
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
		if(element instanceof NodeProxyTCP) {
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

	public Message readFromInputStream(Socket socket) throws IOException {
		ByteBuffer buffer=new ByteBuffer();

		byte[] messageArray = new byte[BUFFER];
		InputStream is = socket.getInputStream();
		int bytesRead;
		while (-1 != (bytesRead = is.read(messageArray, 0, BUFFER))) {
			buffer.with(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
			if(bytesRead != BUFFER && allowAnswer) {
				break;
			}
		}

		Message msg=null;
		if(this.space != null) {
			IdMap map = this.space.getMap();
			Object element = map.decode(buffer);
			this.space.updateNetwork(NodeProxy.TYPE_IN, this);
			if(element instanceof Message) {
				msg = (Message) element;
				NodeProxy receiver = msg.getReceiver();
				if(element instanceof ConnectMessage) {
					receiver.updateReceive(buffer.size(), false);
				} else {
					receiver.updateReceive(buffer.size(), true);
				}
				
				// Let my Know about the new Receiver
				if(receiver != null) {
					this.space.with(receiver);
				}
			}
		}
		if(msg == null){
			msg=new Message();
		}
		msg.withMessage(buffer.flip(false));
		msg.withSession(socket);
		msg.withAddToReceived(this);
		if(this.listener != null) {
			this.listener.update(msg);
		}
		if(allowAnswer) {
			getExecutor().handleMsg(msg);
		}else {
			socket.close();
			getExecutor().handleMsg(msg);
		}
		return msg;
	}

	@Override
	protected boolean sending(Message msg) {
		if (super.sending(msg)) {
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
				byte[] buffer;
				if(this.space != null) {
 					buffer = this.space.convertMessage(msg).getBytes();
				} else {
					buffer = msg.toString().getBytes();
				}
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
				if(allowAnswer) {
					readFromInputStream(requestSocket);
				}
				setSendTime(buffer.length);
				requestSocket.close();
				success = true;
			}
		} catch (IOException ioException) {
			// could not reach the proxy, mark it as offline
			this.withOnline(false);
			success = false;
		}
		return success;
	}

	public boolean start() {
		return initProxy();
	}

	@Override
	public boolean close() {
		if (this.serverSocket != null) {
			this.serverSocket.close();
			this.serverSocket = null;
		}
		return true;
	}

	@Override
	protected boolean initProxy() {
		boolean isInput = NodeProxy.isInput(getType());
		if (url == null && getType() == null || isInput) {
			if(serverSocket != null) {
				return true;
			}
			// Incoming Proxy
			if(isInput == false) {
				withType(NodeProxy.TYPE_IN);
			}
			serverSocket = new Server_TCP(this);
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
			withType(NodeProxy.TYPE_OUT);
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
		NodeProxyTCP proxy = new NodeProxyTCP();
		proxy.withPort(port);
		proxy.withType(NodeProxy.TYPE_INOUT);
		return proxy;
	}

	@Override
	public NodeProxyTCP getSendableInstance(boolean reference) {
		return new NodeProxyTCP();
	}

	public NodeProxyTCP withListener(ObjectCondition condition) {
		this.listener = condition;
		this.allowAnswer = true;
		return this;
	}
	
	public static HTMLEntity postHTTP(String url, Map<String, String> params) {
		HttpURLConnection conn = getConnection(url, POST);
		if(conn == null) {
			return null;
		}
		CharacterBuffer sb=new CharacterBuffer();
		if(params != null) {
			for(Iterator<Entry<String, String>> i = params.entrySet().iterator();i.hasNext();) {
				Entry<String, String> item = i.next();
				if(sb.length() > 0 ) {
					sb.with('&');
				}
				sb.with(item.getKey(), "=", item.getValue());
			}
		}
		byte[] byteArray = sb.toByteArray();
		conn.setFixedLengthStreamingMode(byteArray.length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try {
			conn.connect();
			OutputStream os = conn.getOutputStream();
			os.write(byteArray);
			return readAnswer(conn);
		} catch (IOException e) {
		}
		
		return null;
	}
	
	public static HTMLEntity postHTTP(String url, BaseItem params) {
		HttpURLConnection conn = getConnection(url, POST);
		if(conn == null) {
			return null;
		}
		CharacterBuffer sb=new CharacterBuffer();
		if(params != null) {
			sb.with(params.toString());
		}
		byte[] byteArray = sb.toByteArray();
		conn.setFixedLengthStreamingMode(byteArray.length);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		try {
			conn.connect();
			OutputStream os = conn.getOutputStream();
			os.write(byteArray);
			return readAnswer(conn);
		} catch (IOException e) {
		}
		
		return null;
	}
	
	private static HttpURLConnection getConnection(String url, String type) {
		HttpURLConnection conn =null;
		try {
			if(url.startsWith("localhost") ) {
				url = "http://"+url;
			}
			URL remoteURL = new URL(url);
			conn = (HttpURLConnection) remoteURL.openConnection();
			if(POST.equals(type)) {
				conn.setRequestMethod(POST);
				conn.setDoOutput(true);
			} else {
				conn.setRequestMethod(GET);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	private static HTMLEntity readAnswer(HttpURLConnection conn) {
		HTMLEntity rootItem=new HTMLEntity();
		try {
			InputStream is = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			byte[] messageArray = new byte[BUFFER];
			while (true) {
				int bytesRead = is.read(messageArray, 0, BUFFER);
				if (bytesRead <= 0)
					break; // <======= no more data
				sb.append(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
			}
			rootItem.withValue(sb.toString());
		}catch (IOException e) {
		}
		conn.disconnect();
		return rootItem;
	}
	
	public static HTMLEntity getHTTP(String url) {
		HttpURLConnection conn = getConnection(url, GET);
		if(conn == null) {
			return null;
		}
		return readAnswer(conn);
	}
	
	@Override
	public String toString() {
		if(this.url != null && this.port >0) {
			return this.getClass().getSimpleName() + " "+this.url+":"+this.port;
		}
		return super.toString();
	}
	
	
	@Override
	public boolean isValid() {
		if(this.url != null && this.port >0) {
			return true;
		}
		return false;
	}
}
