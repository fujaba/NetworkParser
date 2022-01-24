package de.uniks.networkparser.ext.petaf.proxy;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.net.DatagramPacket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.RESTServiceTask;
import de.uniks.networkparser.ext.http.HTTPRequest;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;
import de.uniks.networkparser.ext.petaf.Server_TCP;
import de.uniks.networkparser.ext.petaf.Server_UPD;
import de.uniks.networkparser.ext.petaf.SimpleExecutor;
import de.uniks.networkparser.ext.petaf.Space;
import de.uniks.networkparser.ext.petaf.TaskExecutor;
import de.uniks.networkparser.ext.petaf.messages.ConnectMessage;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.Server;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.HTMLEntity;

/**
 * Proxy for TCP-Connection
 * 
 * @author Stefan Lindel
 */
public class NodeProxyTCP extends NodeProxy implements Condition<Socket> {
    public static final int BUFFER = 100 * 1024;
    public static final String PROPERTY_URL = "url";
    public static final String PROPERTY_PORT = "port";
    public static final String USERAGENT = "User-Agent";

    protected int port;
    protected String url;
    protected int timeOut;
    public static final String LOCALHOST = "127.0.0.1";
    protected Server server;
    protected boolean allowAnswer = false;
    private int receivePort = 9876;
    private String serverType = Server.TCP;
    private TaskExecutor executor;
    private RESTServiceTask restService;

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
        if (url == null) {
            return "server:" + port;
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
        if (element instanceof NodeProxyTCP) {
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
        if (element instanceof NodeProxyTCP) {
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

    public boolean update(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            ByteBuffer buffer = new ByteBuffer();
            byte[] messageArray = new byte[BUFFER];
            int bytesRead;
            while (-1 != (bytesRead = is.read(messageArray, 0, BUFFER))) {
                buffer.with(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
                if (bytesRead != BUFFER && allowAnswer) {
                    break;
                }
            }
            Message msg = null;
            if (this.space != null) {
                IdMap map = this.space.getMap();
                Object element = map.decode(buffer);
                this.space.updateNetwork(NodeProxy.TYPE_IN, this);
                if (element instanceof Message) {
                    msg = (Message) element;
                    NodeProxy receiver = msg.getReceiver();
                    if (element instanceof ConnectMessage) {
                        receiver.updateReceive(buffer.size(), false);
                    } else {
                        receiver.updateReceive(buffer.size(), true);
                    }
                    if (msg instanceof ReceivingTimerTask) {
                        ((ReceivingTimerTask) msg).withSpace(this.space);
                    }
                    /* Let my Know about the new Receiver */
                    this.space.with(receiver);
                }
            }
            if (msg == null) {
                msg = new Message();
            }
            msg.withMessage(buffer.flip(false));
            msg.withSession(socket);
            msg.withAddToReceived(this);
            if (this.listener != null) {
                this.listener.update(msg);
            }
            if (allowAnswer) {
                getExecutor().handleMsg(msg);
            } else {
                socket.close();
                getExecutor().handleMsg(msg);
            }
        } catch (Exception e) {
            if (space != null) {
                space.handleException(e);
            }
            return false;
        }
        return true;
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
                } else if (this.timeOut > 0) {
                    requestSocket.setSoTimeout(this.timeOut);
                }
                OutputStream os = requestSocket.getOutputStream();
                byte[] buffer;
                if (this.space != null) {
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
                if (allowAnswer) {
                    update(requestSocket);
                }
                setSendTime(buffer.length);
                requestSocket.close();
                success = true;
            }
        } catch (IOException ioException) {
            /* could not reach the proxy, mark it as offline */
            this.withOnline(false);
            success = false;
        }
        return success;
    }

    public boolean start() {
        return startProxy();
    }

    @Override
    public boolean close() {
        if (this.server != null) {
            boolean succces = this.server.close();
            this.server = null;
            return succces;
        }
        return true;
    }

    @Override
    protected boolean startProxy() {
        boolean isInput = NodeProxy.isInput(getType());
        if (url == null && getType() == null || isInput) {
            if (server != null) {
                return true;
            }
            if (Server.TCP.equals(this.serverType)) {
                /* Incoming Proxy */
                if (isInput == false) {
                    withType(NodeProxy.TYPE_IN);
                }
                server = new Server_TCP(this);
                if (url == null) {
                    try {
                        String urlValue = InetAddress.getLocalHost().getHostAddress();
                        if (LOCALHOST.equals(urlValue) == false) {
                            this.url = urlValue;
                        }
                    } catch (UnknownHostException e) {
                        space.logException(this, "startProxy", e);
                    }
                }
            } else if (Server.TIME.equals(this.serverType)) {
                // DO Nothing
            } else if (Server.REST.equals(this.serverType)) {
                Space space = getSpace();
                boolean startRest = false;
                if (space != null) {
                    startRest = true;
                    if (restService == null) {
                        restService = new RESTServiceTask().withProxy(this);
                    }
                } else if (restService != null && restService.getRoutings().size() > 0) {
                    startRest = true;
                }
                if (startRest) {
                    server = new Server_TCP(this).withHandler(restService);
                }
            } else {
                /* Server.BROADCAST */
                Server_UPD updServer = new Server_UPD(this).withStart();
                this.server = updServer;
                this.online = this.server.isRun();
            }
        } else {
            withType(NodeProxy.TYPE_OUT);
            if (url == null) {
                try {
                    url = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                }
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

    public static HTMLEntity getHTTP(HTMLEntity session, String path, Object... params) {
        if (session == null) {
            return null;
        }
        CharacterBuffer buffer = new CharacterBuffer();
        buffer.add(session.getConnectionHeader("remote"));
        if (buffer.length() < 1) {
            return null;
        }
        if (path != null) {
            if (path.startsWith("/")) {
                buffer.with(path);
            } else {
                buffer.with('/');
                buffer.with(path);
            }
        }
        if (params != null && params.length > 0) {
            buffer.add("?");
            convertParams(buffer, params);
        }
        String uri = buffer.toString();
        if (uri == null) {
            return null;
        }
        HttpURLConnection conn = getConnection(uri, HTTPRequest.HTTP_TYPE_GET);
        if (conn == null) {
            return null;
        }
        List<String> cookies = session.getConnectionHeaders("Set-Cookie");
        if (cookies != null) {
            for (int i = 0; i < cookies.size(); i++) {
                String cookie = cookies.get(i).substring(0, cookies.get(i).indexOf(';'));
                conn.setRequestProperty("Cookie", cookie);
            }
        }
        try {
            conn.connect();
            return readAnswer(conn);
        } catch (IOException e) {
        }
        return null;
    }

    public static BaseItem convertParams(BaseItem result, Object... params) {
        if (params == null || params.length < 1) {
            return result;
        }
        boolean split = false;
        if (params[0] instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) params[0];
            Set<?> keySet = map.keySet();
            for (Object key : keySet) {
                split = addToList(result, "" + key, "" + map.get(key), split);
            }
        } else if (params.length % 2 == 0) {
            for (int i = 0; i < params.length; i += 2) {
                split = addToList(result, "" + params[i], "" + params[i + 1], split);
            }
        }
        return result;
    }

    private static boolean addToList(BaseItem params, String key, String value, boolean split) {
        if (params instanceof CharacterBuffer) {
            if (split) {
                params.add('&');
            }
            params.add(key, "=", value);
        } else {
            params.add(key, value);
        }
        return true;
    }

    private static HttpURLConnection getConnection(String url, String type) {
        HttpURLConnection conn = null;
        try {
            if (url == null || url.isEmpty()) {
                return null;
            }
            if (url.startsWith("localhost")) {
                url = "http://" + url;
            }
            URL remoteURL = new URL(url);
            conn = (HttpURLConnection) remoteURL.openConnection();
            if (HTTPRequest.HTTP_TYPE_POST.equals(type)) {
                conn.setRequestMethod(HTTPRequest.HTTP_TYPE_POST);
                conn.setDoOutput(true);
            } else {
                conn.setRequestMethod(HTTPRequest.HTTP_TYPE_GET);
            }
        } catch (IOException e) {
            return null;
        }
        return conn;
    }

    public static HTMLEntity postHTTP(String url, BaseItem params) {
        HTTPRequest request = new HTTPRequest(url, HTTPRequest.HTTP_TYPE_POST).withContent(params).withContentType(HTTPRequest.HTTP_CONTENT_FORM);
        return sendHTTP(request);
    }

    public static HTMLEntity postMultiHTTP(String url, Map<String, Object> params) {
        HTTPRequest request = new HTTPRequest(url, HTTPRequest.HTTP_TYPE_POST).withContentForm("----JavaBoundary", params);
        return sendHTTP(request);
    }

    public static HTMLEntity postHTTP(HTMLEntity session, String url, String bodyType, Object... params) {
        HTTPRequest request = new HTTPRequest(url, HTTPRequest.HTTP_TYPE_POST).withContent(session);
        request.withContent(bodyType, params);
        return sendHTTP(request);
    }
    
  public static HTMLEntity postHTTP(String url, int port, String path, String bodyType, Object... params) {
	  String uri = convertPath(url, port, path);
	  if (uri == null) {
	      return null;
	  }
	  HTTPRequest request = new HTTPRequest(url, HTTPRequest.HTTP_TYPE_POST);
	  request.withContent(bodyType, params);
	  return sendHTTP(request);
  }
  
  public static HTMLEntity postHTTP(String url, Map<String, Object> params) {
	  HTTPRequest request = new HTTPRequest(url, HTTPRequest.HTTP_TYPE_POST);
	  request.withContent(HTTPRequest.HTTP_CONTENT_PLAIN, params);
	  return sendHTTP(request);
  }
  
  public static HTMLEntity postHTTP(String url, String content, String... params) {
	CharacterBuffer fullUrl = new CharacterBuffer().with(url);
	if(params != null) {
	    fullUrl.add("?");
	    convertParams(fullUrl, (Object[])params);
	}
	  HTTPRequest request = new HTTPRequest(fullUrl.toString(), HTTPRequest.HTTP_TYPE_POST);
	  request.withContent(content);
	  request.withContentType(HTTPRequest.HTTP_CONTENT_FORM);
	  return sendHTTP(request);
  }
  
  public static HTMLEntity sendHTTP(HTTPRequest element) {
        if (element == null) {
            return null;
        }
        
        CharacterBuffer multiContent = element.getMultiContent();
        CharacterBuffer content = new CharacterBuffer();
        if (multiContent != null) {
            content = multiContent;
        }
        BaseItem contentElement = element.getContentElement();
        String url = element.getUrl();
        HTMLEntity session = null;
        if(contentElement instanceof HTMLEntity) {
            session = (HTMLEntity) contentElement;
            if (url != null) {
                if (url.startsWith("/")) {
                    url = session.getConnectionHeader("remote") + url;
                } else {
                    url = session.getConnectionHeader("remote") + "/" + url;
                }
            }
            if(!element.isValidContentType()) {
                return null;
            }
        }
        if (HTTPRequest.HTTP_CONTENT_PLAIN.equalsIgnoreCase(element.getContentType())) {
        	if(element.getContent() != null) {
        		content.add(element.getContentType());
        	} else {
        		convertParams(content, element.getContentValues());
        	}
        } else if (HTTPRequest.HTTP_CONTENT_JSON.equalsIgnoreCase(element.getContentType())) {
        	if(element.getContentElement() instanceof JsonObject || element.getContentElement() instanceof JsonArray) {
        		content.add(element.getContentElement().toString());
        	}else {
        		JsonObject json = new JsonObject();
        		convertParams(json, element.getContentValues());
        		content.add(json.toString());
        	}
        }
        byte[] bytes = content.toBytes(true);
        HttpURLConnection conn = getConnection(url, element.getHttp_Type());
        if (conn == null) {
        	return null;
        }
        if(!HTTPRequest.HTTP_CONTENT_HEADER.equalsIgnoreCase(element.getContentType())) {
        	conn.setRequestProperty(HTTPRequest.HTTP_CONTENT, element.getContentType());
        }
        conn.setRequestProperty(HTTPRequest.HTTP_LENGTH, "" + bytes.length);
        conn.setRequestProperty("charset", "utf-8");
        if(session != null) {
            List<String> cookies = session.getConnectionHeaders("Set-Cookie");
            if (cookies != null) {
                for (int i = 0; i < cookies.size(); i++) {
                    String cookie = cookies.get(i).substring(0, cookies.get(i).indexOf(';'));
                    conn.setRequestProperty("Cookie", cookie);
                }
            }
        }
        if (HTTPRequest.HTTP_CONTENT_HEADER.equalsIgnoreCase(element.getContentType())) {
        	Map<String, Object> contentValues = element.getContentValues();
        	for(Iterator<Entry<String, Object>> iterator = contentValues.entrySet().iterator();iterator.hasNext();) {
        		Entry<String, Object> entry = iterator.next();
        		 conn.setRequestProperty(entry.getKey(), "" + entry.getValue());
        	}
        }
        conn.setFixedLengthStreamingMode(bytes.length);
        try {
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(bytes);
            return readAnswer(conn);
        } catch (IOException e) {
        }
        return null;
    }

    private static HTMLEntity readAnswer(HttpURLConnection conn) {
        return readAnswer(conn, null);
    }

    private static HTMLEntity readAnswer(HttpURLConnection conn, HTMLEntity root) {
        if (root == null) {
            root = new HTMLEntity();
        }
        if (conn == null) {
            return root;
        }
        try {
            root.withStatus(conn.getResponseCode(), conn.getResponseMessage());
            String uri = conn.getURL().toString();
            String path = conn.getURL().getPath();
            if (uri.length() > path.length()) {
                uri = uri.substring(0, uri.length() - path.length());
            }
            root.withConnectionHeader("remote", uri);

            root.withConnectionHeader(conn.getHeaderFields());

            InputStream is = conn.getInputStream();
            CharacterBuffer sb = new CharacterBuffer();
            byte[] messageArray = new byte[BUFFER];
            while (true) {
                int bytesRead = is.read(messageArray, 0, BUFFER);
                if (bytesRead <= 0)
                    break; /* <======= no more data */
                sb.add(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
            }
            root.with(sb);
        } catch (IOException e) {
            InputStream is = conn.getErrorStream();
            byte[] messageArray = new byte[BUFFER];
            CharacterBuffer sb = new CharacterBuffer();
            try {
                while (true) {
                    int bytesRead = is.read(messageArray, 0, BUFFER);
                    if (bytesRead <= 0)
                        break; /* <======= no more data */
                    sb.add(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
                }
                root.with(sb);
            } catch (Exception e2) {
            }
        }

        conn.disconnect();
        return root;
    }

    public static String convertPath(String url, int port, String path) {
        if (url == null) {
            return null;
        }
        CharacterBuffer buffer = new CharacterBuffer();
        if (url.toLowerCase().startsWith("http")) {
            buffer.with(url);
        } else {
            buffer.with("http://" + url);
        }
        if (buffer.indexOf(':', 6) < 1) {
            buffer.with(':');
            buffer.with(port);
        }
        if (path != null) {
            if (path.startsWith("/")) {
                buffer.with(path);
            } else {
                buffer.with('/');
                buffer.with(path);
            }
        }
        return buffer.toString();
    }

    public static HTMLEntity getHTTP(String url, int port, String path) {
        String uri = convertPath(url, port, path);
        if (uri == null) {
            return null;
        }
        return getHTTP(url);
    }

    public static HTMLEntity getHTTP(String url, HTMLEntity... root) {
        HttpURLConnection conn = getConnection(url, HTTPRequest.HTTP_TYPE_GET);
        if (conn == null) {
            return null;
        }
        HTMLEntity rootItem = null;
        if (root != null && root.length > 0) {
            rootItem = root[0];
        }
        return readAnswer(conn, rootItem);
    }

    public static HTMLEntity getSimpleHTTP(String url, String... headers) {
        HttpURLConnection conn = getConnection(url, HTTPRequest.HTTP_TYPE_GET);
        if (headers != null && headers.length % 2 == 0) {
            for (int i = 0; i < headers.length; i += 2) {
                conn.setRequestProperty(headers[i], headers[i + 1]);
            }
        }
        if (conn == null) {
            return null;
        }
        HTMLEntity rootItem = null;
        return readAnswer(conn, rootItem);
    }

    public static ByteBuffer getHTTPBinary(String url) {
        HttpURLConnection conn = getConnection(url, HTTPRequest.HTTP_TYPE_GET);
        if (conn == null) {
            return null;
        }
        ByteBuffer sb = new ByteBuffer();
        try {
            InputStream is = conn.getInputStream();
            byte[] messageArray = new byte[BUFFER];
            while (true) {
                int bytesRead = is.read(messageArray, 0, BUFFER);
                if (bytesRead <= 0)
                    break; /* <======= no more data */
                sb.addBytes(messageArray, bytesRead, false);
            }
        } catch (Exception e) {
        }
        conn.disconnect();
        return sb;
    }

    @Override
    public String toString() {
        if (this.url != null && this.port > 0) {
            return this.getClass().getSimpleName() + " " + this.url + ":" + this.port;
        }
        return super.toString();
    }

    public NodeProxyTCP withTimeOut(int value) {
        this.timeOut = value;
        return this;
    }

    public NodeProxyTCP withServerType(String type) {
        this.serverType = type;
        return this;
    }

    @Override
    public boolean isValid() {
        if (this.port > 0) {
            return true;
        }
        return false;
    }

    public DatagramPacket executeBroadCast(boolean async) {
        if (async) {
            this.server = new Server_UPD(this).withStart();
        } else {
            Server_UPD localServer = new Server_UPD(this);
            this.server = localServer;
            return localServer.runClient();
        }
        return null;
    }

    public int getBufferSize() {
        return BUFFER;
    }

    public boolean sendSearch() {
        Server_UPD server = (Server_UPD) this.server;
        DatagramPacket packet = server.runClient();
        if (packet.getLength() > 0) {
            ByteBuffer buffer = new ByteBuffer().with(packet.getData());
            this.space.setReplicationInfo(buffer);
        }
        // If(This IsOnline False) May be a valid Breadcast on this Port
        return false;
    }

    public static NodeProxyTCP search(int port) {
        return NodeProxyTCP.createServer(port);
    }

    public NodeProxyTCP withReceivePort(int port) {
        this.receivePort = port;
        return this;
    }

    public int getReceivePort() {
        return receivePort;
    }

    public NodeProxyTCP withExecutor(ExecutorService executor) {
        this.executor = new SimpleExecutor().withExecutorService(executor);
        return this;
    }

    @Override
    public TaskExecutor getExecutor() {
        if (this.executor != null) {
            return executor;
        }
        return super.getExecutor();
    }

    public boolean isRun() {
        return server.isRun();
    }

    public void stop() {
        this.server.stop();
    }

    public NodeProxyTCP withRestService(RESTServiceTask restService) {
        this.restService = restService;
        if (restService != null) {
            this.restService.withProxy(this);
            this.serverType = Server.REST;
        }
        return this;
    }
}
