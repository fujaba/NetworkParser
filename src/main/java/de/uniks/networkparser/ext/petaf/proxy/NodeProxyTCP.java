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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Server_TCP;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
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
	
	public static final String BODY_PLAIN = "plain";
	public static final String HEADER_PLAIN = "plainHeader";
	public static final String BODY_JSON = "json";
	protected int port;
	protected String url;
	protected int timeOut;
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
		if(url ==null) {
			return "server:"+port;
		}
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
				if(msg instanceof ReceivingTimerTask) {
					((ReceivingTimerTask)msg).withSpace(this.space);
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
				}else if(this.timeOut >0) {
					requestSocket.setSoTimeout(this.timeOut);
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
	public static HTMLEntity postHTTP(String url, int port, String path, String bodyType, Object...params) {
		String uri = convertPath(url, port, path);
		if(uri == null) {
			return null;
		}
		if(BODY_JSON.equalsIgnoreCase(bodyType) == false && BODY_PLAIN.equalsIgnoreCase(bodyType) == false && HEADER_PLAIN.equalsIgnoreCase(bodyType) == false) {
			return null;
		}
		HttpURLConnection conn = getConnection(uri, POST);
		
		byte[] byteArray = null;
		if(HEADER_PLAIN.equalsIgnoreCase(bodyType)) {
			for(int i=0;i<params.length;i+=2) {
				conn.setRequestProperty(""+params[i], ""+params[i + 1]);
			}
		} else if(BODY_PLAIN.equalsIgnoreCase(bodyType)) {
			CharacterBuffer sb =new CharacterBuffer();
			convertParams(sb, params);
			byteArray = sb.toBytes();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		} else if(BODY_JSON.equalsIgnoreCase(bodyType)) {
			JsonObject json =new JsonObject();
			convertParams(json, params);
			byteArray = json.toString().getBytes();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		}
		conn.setFixedLengthStreamingMode(byteArray.length);
		try {
			conn.connect();
			OutputStream os = conn.getOutputStream();
			os.write(byteArray);
			return readAnswer(conn);
		} catch (IOException e) {
		}
		return null;
	}

	public static HTMLEntity postHTTP(HTMLEntity session, String path, String bodyType, Object...params) {
		CharacterBuffer buffer = new CharacterBuffer();
//		String uri = convertPath(url, port, path);
		buffer.add(session.getConnectionHeader("remote"));
		
		if(path != null) {
			if(path.startsWith("/")) {
				buffer.with(path);
			}else {
				buffer.with('/');
				buffer.with(path);
			}
		}
		String uri = buffer.toString();
		if(uri == null) {
			return null;
		}
		if(BODY_JSON.equalsIgnoreCase(bodyType) == false && BODY_PLAIN.equalsIgnoreCase(bodyType) == false && HEADER_PLAIN.equalsIgnoreCase(bodyType) == false) {
			return null;
		}
		HttpURLConnection conn = getConnection(uri, POST);
		List<String> cookies = session.getConnectionHeaders("Set-Cookie");
		if(cookies != null) {
			for(int i=0;i<cookies.size();i++) {
				String cookie = cookies.get(i).substring(0, cookies.get(i).indexOf(';'));
				conn.setRequestProperty("Cookie", cookie);
			}
		}
		byte[] byteArray = null;
		if(HEADER_PLAIN.equalsIgnoreCase(bodyType)) {
			for(int i=0;i<params.length;i+=2) {
				conn.setRequestProperty(""+params[i], ""+params[i + 1]);
			}
		} else if(BODY_PLAIN.equalsIgnoreCase(bodyType)) {
			CharacterBuffer sb =new CharacterBuffer();
			convertParams(sb, params);
			byteArray = sb.toBytes();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		} else if(BODY_JSON.equalsIgnoreCase(bodyType)) {
			JsonObject json =new JsonObject();
			convertParams(json, params);
			byteArray = json.toString().getBytes();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		}
		if(byteArray != null) {
			conn.setFixedLengthStreamingMode(byteArray.length);
		}
		try {
			conn.connect();
			if(byteArray != null) {
				OutputStream os = conn.getOutputStream();
				os.write(byteArray);
			}
			return readAnswer(conn);
		} catch (IOException e) {
		}
		return null;
	}

	public static BaseItem convertParams(BaseItem result, Object... params) {
		if(params == null || params.length<1) {
			return result;
		}
		if(params[0] instanceof Map<?,?>) {
			Map<?,?> map = (Map<?, ?>) params[0];
			Set<?> keySet = (Set<?>) map.keySet();
			for(Object key : keySet) {
				addToList(result, ""+key, ""+map.get(key));
			}
		} else if(params.length % 2 == 0) {
			for(int i=0;i<params.length;i+=2) {
				addToList(result, ""+params[i], ""+params[i + 1]);
			}
		}
		return result;
	}
	
	private static void addToList(BaseItem params, String key, String value) {
		if(params instanceof CharacterBuffer) {
			if(params.size() > 0 ) {
				params.add('&');
			}
			params.add(key, "=", value);
		} else {
			params.add(key, value);
		}
	}

	public static HTMLEntity postHTTP(String url, Map<String, String> params) {
		HttpURLConnection conn = getConnection(url, POST);
		if(conn == null) {
			return null;
		}
		CharacterBuffer sb =new CharacterBuffer();
		convertParams(sb, params);
		byte[] byteArray = sb.toBytes();
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
		byte[] byteArray = sb.toBytes();
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
		if(conn == null) {
			return rootItem;
		}
		try {
			rootItem.withStatus(conn.getResponseCode(), conn.getResponseMessage());
			String uri = conn.getURL().toString();
			String path = conn.getURL().getPath();
			if(uri.length()> path.length()) {
				uri = uri.substring(0, uri.length() - path.length());
			}
			rootItem.withConnectionHeader("remote", uri);
			
			rootItem.withConnectionHeader(conn.getHeaderFields());

			InputStream is = conn.getInputStream();
			CharacterBuffer sb = new CharacterBuffer();
			byte[] messageArray = new byte[BUFFER];
			while (true) {
				int bytesRead = is.read(messageArray, 0, BUFFER);
				if (bytesRead <= 0)
					break; // <======= no more data
				sb.add(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
			}
			rootItem.with(sb);
		}catch (IOException e) {
			InputStream is = conn.getErrorStream();
			byte[] messageArray = new byte[BUFFER];
			CharacterBuffer sb = new CharacterBuffer();
			try {
				while (true) {
					int bytesRead = is.read(messageArray, 0, BUFFER);
					if (bytesRead <= 0)
						break; // <======= no more data
					sb.add(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
				}
				rootItem.with(sb);
			}catch (Exception e2) {
			}
//			e.printStackTrace();
		}
		

		conn.disconnect();
		return rootItem;
	}
	
	public static String convertPath(String url, int port, String path) {
		if(url == null) {
			return null;
		}
		CharacterBuffer buffer=new CharacterBuffer();
		if(url.toLowerCase().startsWith("http")) {
			buffer.with(url);
		} else {
			buffer.with("http://"+url);
		}
		if(buffer.indexOf(':', 6)<1) {
			buffer.with(':');
			buffer.with(port);
		}
		if(path != null) {
			if(path.startsWith("/")) {
				buffer.with(path);
			}else {
				buffer.with('/');
				buffer.with(path);
			}
		}
		return buffer.toString();
	}

	public static HTMLEntity getHTTP(String url, int port, String path) {
		String uri = convertPath(url, port, path);
		if(uri == null) {
			return null;
		}
		return getHTTP(url);
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

	public NodeProxyTCP withTimeOut(int value) {
		this.timeOut = value;
		return this;
	}

	@Override
	public boolean isValid() {
		if(this.port >0) {
			return true;
		}
		return false;
	}

}
