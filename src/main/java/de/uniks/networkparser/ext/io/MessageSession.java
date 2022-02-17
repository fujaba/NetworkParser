package de.uniks.networkparser.ext.io;

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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteConverter64;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroker;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLEntity;

/**
 * The Class MessageSession.
 *
 * @author Stefan
 */
public class MessageSession {
	
	/** The Constant TYPE_EMAIL. */
	public static final String TYPE_EMAIL = "EMAIL";
	
	/** The Constant TYPE_XMPP. */
	public static final String TYPE_XMPP = "XMPP";
	
	/** The Constant TYPE_FCM. */
	public static final String TYPE_FCM = "FCM";
	
	/** The Constant TYPE_PLAIN. */
	public static final String TYPE_PLAIN = "PLAIN";
	
	/** The Constant TYPE_AMQ. */
	public static final String TYPE_AMQ = "AMQ";
	
	/** The Constant TYPE_MQTT. */
	public static final String TYPE_MQTT = "MQTT";

	/** The Constant RESPONSE_SERVERREADY. */
	public static final String RESPONSE_SERVERREADY = "220";
	
	/** The Constant RESPONSE_MAILACTIONOKEY. */
	public static final String RESPONSE_MAILACTIONOKEY = "250";
	
	/** The Constant RESPONSE_STARTMAILINPUT. */
	public static final String RESPONSE_STARTMAILINPUT = "354";
	
	/** The Constant RESPONSE_SMTP_AUTH_NTLM_BLOB_Response. */
	public static final String RESPONSE_SMTP_AUTH_NTLM_BLOB_Response = "334";
	
	/** The Constant RESPONSE_LOGIN_SUCCESS. */
	public static final String RESPONSE_LOGIN_SUCCESS = "235";
	
	/** The Constant RESPONSE_SERVICE_CLOSING_TRANSMISSION. */
	public static final String RESPONSE_SERVICE_CLOSING_TRANSMISSION = "221";
	
	/** The Constant SSL_PORT. */
	public static final int SSL_PORT = 587;
	
	/** The Constant AMQP_PORT. */
	public static final int AMQP_PORT = 5672;
	
	/** The Constant MQTT_PORT. */
	public static final int MQTT_PORT = 1883;
	/** 15 sec. socket read timeout */
	public static final int SOCKET_READ_TIMEOUT = 15 * 1000;
	
	/** The Constant FEATURE_TLS. */
	public static final String FEATURE_TLS = "STARTTLS";
	
	/** The Constant BUFFER. */
	public static final int BUFFER = 1024;

	private String host;
	private int port;
	private String sender;
	protected Socket serverSocket;
	protected BufferedReader in;
	protected DataInputStream diInput;
	protected OutputStream out;
	protected SimpleList<String> supportedFeature = new SimpleList<String>();
	private BufferedBuffer lastAnswer;
	private String lastSended;
	private SocketFactory factory;
	private String type;
	private String id;
	private BufferedBuffer responseFactory = new CharacterBuffer();
	private NetworkParserLog logger;
	private boolean useTLS=true;

	/**
	 * Connect SSL.
	 *
	 * @param host the host
	 * @param sender the sender
	 * @param password the password
	 * @return the message session
	 */
	public MessageSession connectSSL(String host, String sender, String password) {
		this.host = host;
		this.port = SSL_PORT;
		this.sender = sender;
		this.connect(sender, password);
		return this;
	}

	/**
	 * Connect.
	 *
	 * @param host the host
	 * @param port the port
	 * @param sender the sender
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connect(String host, int port, String sender, String password) {
		this.host = host;
		this.port = port;
		return connect(sender, password);
	}

	private BufferedBuffer bindXMPP() {
		XMLEntity iq = XMLEntity.TAG("iq");
		iq.with("id", nextID());
		iq.with("type", "set");
		XMLEntity bind = iq.createChild("bind");
		bind.with("xmlns", "urn:ietf:params:xml:ns:xmpp-bind");
		bind.createChild("resource").withValueItem("NetworkParser");
		String command = iq.toString();
		BufferedBuffer response = sendCommand(command);

		response = sendCommand("<iq id=\"" + nextID()
				+ "\" type=\"set\"><session xmlns=\"urn:ietf:params:xml:ns:xmpp-session\"/></iq>");

		XMLEntity presence = XMLEntity.TAG("presence");
		presence.with("id", nextID());
		presence.withCloseTag();
		command = presence.toString();
		response = sendCommand(command);

		return response;
	}

	private String getLoginText(String user, String password) {
		if (user == null || password == null) {
			return "";
		}
		byte[] userBytes = user.getBytes();
		byte[] passwordBytes = password.getBytes();

		if (TYPE_AMQ.equalsIgnoreCase(type)) {
			byte[] bytes = new byte[userBytes.length + passwordBytes.length + 2];
			int i = 0;
			for (; i < userBytes.length; i++) {
				bytes[i + 1] = userBytes[i];
			}
			for (i = 0; i < passwordBytes.length; i++) {
				bytes[i + userBytes.length + 2] = passwordBytes[i];
			}
			return new String(bytes);
		}

		byte[] bytes = new byte[userBytes.length * 2 + passwordBytes.length + 2];
		int i = 0;
		for (; i < userBytes.length; i++) {
			bytes[i] = userBytes[i];
			bytes[i + userBytes.length + 1] = userBytes[i];
		}
		for (i = 0; i < passwordBytes.length; i++) {
			bytes[i + userBytes.length + userBytes.length + 2] = passwordBytes[i];
		}
		ByteConverter64 converter = new ByteConverter64();
		CharacterBuffer staticString = converter.toStaticString(bytes, true);
		return staticString.toString();
	}

	/**
	 * Gets the sender.
	 *
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender the sender
	 * @return true, if successful
	 */
	public boolean setSender(String sender) {
		if (!StringUtil.stringEquals(this.sender, sender)) {
			this.sender = sender;
			return true;
		}
		return false;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * With port.
	 *
	 * @param port the port
	 * @return the message session
	 */
	public MessageSession withPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * With host.
	 *
	 * @param url the url
	 * @return the message session
	 */
	public MessageSession withHost(String url) {
		if (url == null) {
			return this;
		}
		int pos = url.lastIndexOf(":");
		if (pos > 0) {
			try {
				this.port = Integer.parseInt(url.substring(pos + 1));
				this.host = url.substring(0, pos);
			} catch (Exception e) {
				pos = -1;
			}
		}
		if (pos < 0) {
			this.host = url;
		}
		return this;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getID() {
		return id;
	}

	/**
	 * Closes down the connection to SMTP server (if open). Should be called if an
	 * exception is raised during the SMTP session.
	 * 
	 * @return success
	 */
	public boolean close() {
		try {
			if (in != null) {
				in.close();
			}
			in = null;
			if (out != null) {
				out.close();
			}
			out = null;
			if (serverSocket != null) {
				serverSocket.close();
			}
			serverSocket = null;
		} catch (Exception ex) {
			/* Ignore the exception. Probably the socket is not open. */
			return false;
		}
		return true;
	}

	/**
	 * Checks if is close.
	 *
	 * @return true, if is close
	 */
	public boolean isClose() {
		return in == null;
	}

	private boolean initSockets(String host, int port) throws UnsupportedEncodingException, IOException {
		if (factory == null || host == null || host.isEmpty() || port < 1) {
			return false;
		}

		Socket socket;
		if (this.serverSocket != null && factory instanceof SSLSocketFactory) {
			socket = ((SSLSocketFactory) factory).createSocket(this.serverSocket, host, port, true);
		} else {
			try {
				socket = factory.createSocket(host, port);
			} catch (Exception e) {
				return false;
			}
		}
		socket.setSoTimeout(SOCKET_READ_TIMEOUT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		out = socket.getOutputStream();
		this.serverSocket = socket;
		return true;
	}

	/**
	 * Connect.
	 *
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connect(String password) {
		return this.connect(this.sender, password);
	}

	/**
	 * Connect XMPP.
	 *
	 * @param sender the sender
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connectXMPP(String sender, String password) {
		this.type = TYPE_XMPP;
		this.factory = javax.net.SocketFactory.getDefault();
		if (!isValid(sender)) {
			return false;
		}
		try {
			initSockets(host, port);

			BufferedBuffer response = sendStart();

			XMLEntity answer = new XMLEntity().withValue(response);
			this.id = answer.getString("id");
			XMLEntity features = (XMLEntity) answer.getElementBy(XMLEntity.PROPERTY_TAG, "stream:features");
			if (features == null) {
				return false;
			}
			for (int i = 0; i < features.sizeChildren(); i++) {
				XMLEntity child = (XMLEntity) features.getChild(i);
				this.supportedFeature.add(child.getTag().toUpperCase());
			}
			if (supportedFeature.contains(FEATURE_TLS)) {
				response = sendCommand("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\" />");
				/* Now create Factory */
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, new javax.net.ssl.TrustManager[] { new ServerTrustManager() },
						new java.security.SecureRandom());
				this.factory = context.getSocketFactory();

				if (!startTLS()) {
					return false;
				}
				sendStart();

				String login = getLoginText(sender, password);
				response = sendCommand("<auth mechanism=\"PLAIN\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + login + "</auth>");

				sendStart();
				bindXMPP();
			}
			return true;
		} catch (Exception e) {
		    if(logger != null) {
		        logger.error(this,  "connectXMPP", e);
		    }
		}
		return false;
	}

	/**
	 * Connect FCM.
	 *
	 * @param sender the sender
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connectFCM(String sender, String password) {
		this.type = TYPE_FCM;
		this.factory = SSLSocketFactory.getDefault();
		if (!isValid(sender)) {
			return false;
		}
		try {
			initSockets(host, port);

			if (this.serverSocket instanceof SSLSocket) {
				((SSLSocket) this.serverSocket).startHandshake();
			}
			BufferedBuffer response = sendStart();

			XMLEntity answer = new XMLEntity().withValue(response);
			this.id = answer.getString("id");

			response = getResponse();

			answer = new XMLEntity().withValue(response);
			XMLEntity features = (XMLEntity) answer.getElementBy(XMLEntity.PROPERTY_TAG, "mechanisms");
			if (features == null) {
				return false;
			}
			for (int i = 0; i < features.sizeChildren(); i++) {
				XMLEntity child = (XMLEntity) features.getChild(i);
				this.supportedFeature.add(child.getValue().toUpperCase());
			}
			String login = getLoginText(sender, password);
			sendCommand("<auth mechanism=\"PLAIN\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" + login + "</auth>");

			sendStart();

			XMLEntity iq = XMLEntity.TAG("iq");
			iq.with("type", "set");
			XMLEntity bind = iq.createChild("bind");
			bind.with("xmlns", "urn:ietf:params:xml:ns:xmpp-bind");

			sendCommand(iq.toString());
			return true;
        } catch (Exception e) {
            if (logger != null) {
                logger.error(this, "connectFCM", e);
            }
        }
		return false;
	}

	/**
	 * Connect SMTP.
	 *
	 * @param sender the sender
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connectSMTP(String sender, String password) {
		this.type = TYPE_EMAIL;
		this.factory = javax.net.SocketFactory.getDefault();
		if (!isValid(sender)) {
			return false;
		}
		try {
			if (serverSocket == null) {
				initSockets(host, port);
				checkServerResponse(getResponse(), RESPONSE_SERVERREADY);
				sendStart();

				if(useTLS) {
					sendCommand(FEATURE_TLS);
					startTLS();
					sendStart();
				}
				BufferedBuffer answer = sendCommand("AUTH LOGIN");
				if (!checkServerResponse(answer, RESPONSE_SMTP_AUTH_NTLM_BLOB_Response)) {
					close();
					return false;
				}
				ByteConverter64 converter = new ByteConverter64();
				answer = sendCommand(converter.toStaticString(sender).toString());
				if (!checkServerResponse(answer, RESPONSE_SMTP_AUTH_NTLM_BLOB_Response)) {
					close();
					return false;
				}
				/* send passwd */
				answer = sendCommand(converter.toStaticString(password).toString());
				if (!checkServerResponse(answer, RESPONSE_LOGIN_SUCCESS)) {
					close();
					return false;
				}
			}
			return true;
		} catch (Exception e) {
            if (logger != null) {
                logger.error(this, "connectSMTP", e);
            }
		}
		return false;
	}

	/**
	 * Sending.
	 *
	 * @param broker the broker
	 * @param message the message
	 * @param answer the answer
	 * @return the rabbit message
	 */
	public RabbitMessage sending(NodeProxyBroker broker, RabbitMessage message, boolean answer) {
		if (message == null) {
			return null;
		}
		message.write(this.out, logger);
		if (!answer) {
			return message;
		}
		RabbitMessage response = RabbitMessage.readFrom(diInput, logger);
		if (response != null) {
			response.analysePayLoad(broker);
		}
		return response;
	}

	/**
	 * Sending.
	 *
	 * @param broker the broker
	 * @param message the message
	 * @param answer the answer
	 * @return the MQTT message
	 */
	public MQTTMessage sending(NodeProxyBroker broker, MQTTMessage message, boolean answer) {
		if (broker == null || message == null || out == null) {
			return null;
		}
		if (message.isMessageIdRequired() && (message.getMessageId() == 0)) {
			if (message.getType() == MQTTMessage.MESSAGE_TYPE_PUBLISH && (message.getMessageQOS() != 0)) {
				message.withMessageId(broker.getNextMessageId());
			} else if (message.getType() == MQTTMessage.MESSAGE_TYPE_PUBACK
					|| message.getType() == MQTTMessage.MESSAGE_TYPE_SUBACK
					|| message.getType() == MQTTMessage.MESSAGE_TYPE_SUBSCRIBE
					|| message.getType() == MQTTMessage.MESSAGE_TYPE_UNSUBSCRIBE) {
				message.withMessageId(broker.getNextMessageId());
			}
		}

		ByteBuffer bytes = message.getHeader();
		bytes.insert(message.getPayload(), false);
		try {
			bytes.flip(false);
			out.write(bytes.array(), 0, bytes.length());
			out.flush();
			if (!answer) {
				return message;
			}
			return MQTTMessage.readFrom(diInput);
		} catch (IOException e) {
			broker.executeException(e);
		}
		return null;
	}

	/**
	 * Connect MQTT.
	 *
	 * @param broker the broker
	 * @param clientId the client id
	 * @param sender the sender
	 * @param password the password
	 * @param keepAlive the keep alive
	 * @param mqttVersion the mqtt version
	 * @param cleanSession the clean session
	 * @return true, if successful
	 */
	public boolean connectMQTT(NodeProxyBroker broker, String clientId, String sender, String password, int keepAlive,
			int mqttVersion, boolean cleanSession) {
		this.type = TYPE_MQTT;
		if (host == null || host.isEmpty()) {
			return false;
		}
		if (this.port == 0) {
			this.port = MQTT_PORT;
		}
		this.factory = javax.net.SocketFactory.getDefault();
		try {
			if (serverSocket == null) {
				initSockets(host, port);
				if (this.serverSocket == null) {
					return false;
				}

				MQTTMessage connect = MQTTMessage.create(MQTTMessage.MESSAGE_TYPE_CONNECT);
				connect.withNames(clientId, sender, password);
				connect.withKeepAliveInterval(keepAlive);
				connect.withCode(mqttVersion);
				connect.withSession(cleanSession);
				this.diInput = new DataInputStream(this.serverSocket.getInputStream());
				sending(broker, connect, true);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Connect AMQ.
	 *
	 * @param broker the broker
	 * @param sender the sender
	 * @param password the password
	 * @return true, if successful
	 */
	public boolean connectAMQ(NodeProxyBroker broker, String sender, String password) {
		this.type = TYPE_AMQ;
		if (this.port == 0) {
			this.port = AMQP_PORT;
		}
		this.factory = javax.net.SocketFactory.getDefault();
		if (sender == null && password == null) {
			sender = "guest";
			password = "guest";
		}
		if (!isValid(sender)) {
			return false;
		}
		try {
			if (serverSocket == null) {
				initSockets(host, port);
				sendStart();
				if (serverSocket == null) {
					return false;
				}

				this.diInput = new DataInputStream(this.serverSocket.getInputStream());
				RabbitMessage message = RabbitMessage.createStartOK(sender, password);
				/* START MESSAGE */
				RabbitMessage response = sending(broker, message, true);

				/* TUNE MESSAGE */
				response = RabbitMessage.readFrom(diInput, logger);
				response.analysePayLoad(broker);

				message = RabbitMessage.createTuneOK((Short) response.getData("channelMax"),
						(Integer) response.getData("frameMax"), (Short) response.getData("heartbeat"));
				sending(broker, message, false);
				message = RabbitMessage.createConnectionOpen(null);
				sending(broker, message, false);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Connects to the SMTP server and gets input and output streams (in, out).
	 * 
	 * @param sender   the Username
	 * @param password the password
	 * @return success
	 */
	public boolean connect(String sender, String password) {
		if (TYPE_FCM.equals(type)) {
			return connectFCM(sender, password);
		}
		if (TYPE_XMPP.equals(type)) {
			return connectXMPP(sender, password);
		}
		if (TYPE_AMQ.equals(type)) {
			return false;
		}
		/* DEFAULT EMAIL */
		return connectSMTP(sender, password);
	}

	private boolean isValid(String sender) {
		if (host == null || host.length() < 1 || sender == null || sender.length() < 1) {
			return false;
		}
		this.sender = sender;
		return true;
	}

	private BufferedBuffer sendStart() {
		if (TYPE_XMPP.equals(type) || TYPE_FCM.equals(type)) {
			return sendCommand("<stream:stream to=\"" + host
					+ "\" xmlns=\"jabber:client\" xmlns:stream=\"http://etherx.jabber.org/streams\" version=\"1.0\">");
		}
		if (TYPE_AMQ.equals(type)) {
			int major = 0;
			int minor = 9;
			int revision = 1;
			write("AMQP".getBytes(), 0, major, minor, revision);
			return new CharacterBuffer();
		}
		BufferedBuffer response = sendCommand("EHLO " + NodeProxyTCP.getLocalHost(serverSocket));
		supportedFeature.clear();
		String[] lines = response.toString().split("\n");
		/* Skip first line */
		for (int i = 1; i < lines.length; i++) {
			supportedFeature.add(lines[i]);
		}
		return response;
	}

	/**
	 * Start TLS.
	 *
	 * @return true, if successful
	 */
	public boolean startTLS() {
		try {
			if (this.serverSocket == null) {
				return false;
			}
			if (factory == null) {
				this.factory = SSLSocketFactory.getDefault();
			}
			initSockets(host, port);

			if (this.serverSocket instanceof SSLSocket) {
				SSLSocket socket = (SSLSocket) this.serverSocket;
				String[] prots = socket.getEnabledProtocols();
				SimpleList<String> eprots = new SimpleList<String>();
				for (int i = 0; i < prots.length; i++) {
					if (prots[i] != null && !prots[i].startsWith("SSL")
							&& !prots[i].equalsIgnoreCase("TLSv1") )
						eprots.add(prots[i]);
				}
				socket.setEnabledProtocols(eprots.toArray(new String[eprots.size()]));

				socket.startHandshake();
				return true;
			}
		} catch (Exception e) {
            if (logger != null) {
                logger.error(this, "startTLS", e);
            }
		}
		return false;
	}

	/**
	 * Sends given command and waits for a response from server.
	 * 
	 * @param commandString String for sending
	 * @return response received from the server.
	 */
	protected BufferedBuffer sendCommand(String commandString) {
		sendValues(commandString);
		return getResponse();
	}

	/**
	 * Sends given command and waits for a response from server.
	 *
	 * @param cmd bytes for sending
	 * @return success
	 */
	protected boolean sendValues(char... cmd) {
		if (cmd == null || out == null) {
			return false;
		}
		try {
		    String value = new String(cmd);
			out.write(value.getBytes());
			if (!BaseItem.CRLF.equals(value)) {
				out.write(BaseItem.CRLF.getBytes());
			}
			out.flush();
			this.lastSended = value;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sends given command and waits for a response from server.
	 *
	 * @param cmd bytes for sending
	 * @return success
	 */
	protected boolean sendValues(String cmd) {
		if (cmd == null || out == null) {
			return false;
		}
		try {
			out.write(cmd.getBytes());
			out.write(BaseItem.CRLF.getBytes());
			if(logger != null) {
				logger.debug(this, "sendValues", cmd);
			}
			out.flush();
			this.lastSended = cmd;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Write.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean write(byte... values) {
		if (out == null) {
			return false;
		}
		try {
			if (values == null) {
				return true;
			}
			out.write(values);
			out.flush();
			this.lastSended = new String(values);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Write.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean write(Object... values) {
		if (values == null || out == null) {
			return false;
		}
		try {
			CharacterBuffer sb = new CharacterBuffer();
			for (Object value : values) {
				if (value instanceof byte[]) {
					byte[] item = (byte[]) value;
					out.write(item);
					sb.with(new String(item));
				} else if (value instanceof Integer) {
					out.write((Integer) value);
					sb.withInt((Integer) value);
				}
			}
			out.flush();
			this.lastSended = sb.toString();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Sends given commandString to the server, gets its reply and checks if it
	 * starts with expectedResponseStart. If not, throws IOException with server's
	 * reply (which is unexpected).
	 * 
	 * @param commandString the Command to send
	 * @param responseCode  expected value of Response
	 * @return success
	 */
	protected boolean doCommand(String commandString, String responseCode) {
		BufferedBuffer response = sendCommand(commandString);
		return checkServerResponse(response, responseCode);
	}

	/**
	 * Checks if given server reply starts with expectedResponseStart. If not,
	 * 
	 * @param response Response as String
	 * @param code     check the response for response code
	 * @return success
	 */
	protected boolean checkServerResponse(BufferedBuffer response, String code) {
		if (response == null || code == null) {
			return false;
		}
		if (response.length() < code.length()) {
			return false;
		}
		int i = 0;
		while (i < code.length()) {
			if (response.charAt(i) != code.charAt(i)) {
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * Gets a response back from the server. Handles multi-line responses (according
	 * to SMTP protocol) and returns them as multi-line string. Each line of the
	 * server's reply consists of 3-digit number followed by some text. If there is
	 * a '-' immediately after the number, the SMTP response continues on the next
	 * line. Otherwise it finished at this line.
	 * 
	 * @return get the current Response
	 */
	protected BufferedBuffer getResponse() {
		BufferedBuffer response = this.responseFactory.getNewList(false);
		if (in == null) {
			return response;
		}
		int readed = -1;
		char[] buffer = new char[BUFFER];
		do {
			try {
				readed = in.read(buffer);
				if (readed > 0) {
					response.with(buffer, 0, readed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (readed == BUFFER);
		if (readed < 0 || response.length() < 1) {
			return response.with("[EOF]");
		}
		this.lastAnswer = response;
		return response;
	}

	/**
	 * Gets the server response.
	 *
	 * @param broker the broker
	 * @return the server response
	 */
	public Object getServerResponse(NodeProxyBroker broker) {
		if (diInput != null) {
			if (TYPE_AMQ.equals(broker.getFormat())) {
				RabbitMessage response = RabbitMessage.readFrom(diInput, logger);
				response.analysePayLoad(broker);
				return response;
			}
			if (TYPE_MQTT.equals(broker.getFormat())) {
				return MQTTMessage.readFrom(diInput);
			}
			return null;
		}
		return getResponse();

	}

	/**
	 * Gets a response back from the server. Handles multi-line responses (according
	 * to SMTP protocol) and returns them as multi-line string. Each line of the
	 * server's reply consists of 3-digit number followed by some text. If there is
	 * a '-' immediately after the number, the SMTP response continues on the next
	 * line. Otherwise it finished at this line.
	 * 
	 * @return get the current Response
	 */
	protected ByteBuffer getByteResponse() {
		ByteBuffer response = new ByteBuffer();
		if (in == null) {
			return response;
		}
		int readed = -1;
		char[] buffer = new char[BUFFER];
		do {
			try {
				readed = in.read(buffer);
				if (readed > 0) {
					response.with(buffer, 0, readed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (readed == BUFFER);
		if (readed < 0 || response.length() < 1) {
			return response.with("[EOF]");
		}
		this.lastAnswer = response;
		return response;
	}

	/**
	 * Gets the local adress.
	 *
	 * @return the local adress
	 */
	public String getLocalAdress() {
		String ipAdress = NodeProxyTCP.getIpAdress();
		if(!ipAdress.isEmpty()) {
			return "@" + ipAdress;
		}
		return "mailer@localhost"; /* worst-case default */
	}

	private static String prefix = StringUtil.randomString(5) + "-";

	/**
	 * Keeps track of the current increment, which is appended to the prefix to
	 * forum a unique ID.
	 */
	private static long messageId = 0;

	/**
	 * Returns the next unique id. Each id made up of a short alphanumeric prefix
	 * along with a unique numeric value.
	 *
	 * @return the next id.
	 */
	public static String nextID() {
		return prefix + Long.toString(messageId++);
	}

	/**
	 * Send message.
	 *
	 * @param to the to
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean sendMessage(String to, String message) {
		SocketMessage msg = new SocketMessage();
		msg.withRecipient(to);
		msg.withMessage(message);
		return sending(msg, null);
	}

	/**
	 * Sends a message using the SMTP protocol.
	 * 
	 * @param message to send
	 * @param password Password for Sending
	 * @return success
	 */
	public boolean sending(SocketMessage message, String password) {
		if (message == null) {
			return false;
		}
		if (TYPE_XMPP.equals(this.type) || TYPE_FCM.equals(this.type)) {
			XMLEntity xml = message.toXML(type);
			return sendCommand(xml.toString()) != null;
		}

		if (!connect(this.sender, password)) {
			return false;
		}

		/* Tell the server who this message is from */
		if (!doCommand(message.getHeaderFrom(this.sender), RESPONSE_MAILACTIONOKEY)) {
			return false;
		}

		/* Now tell the server who we want to send a message to */
		SimpleList<String> headerTo = message.getHeaderTo();
		int pos = 0;
		for (int i = 0; i < headerTo.size(); i++) {
			if (!doCommand(headerTo.get(i), RESPONSE_MAILACTIONOKEY)) {
				message.removeToAdress(pos);
			} else {
				pos++;
			}
		}

		/*
		 * Okay, now send the mail message. We expect a response beginning with '3'
		 * indicating that the server is ready for data.
		 */
		if (!doCommand("DATA", RESPONSE_STARTMAILINPUT)) {
			return false;
		}

		message.generateMessageId(this.getLocalAdress());

		/* Send the message headers */
		sendValues(message.getHeader(SocketMessage.PROPERTY_DATE));
		sendValues(message.getHeader(SocketMessage.PROPERTY_FROM));
		sendValues(message.getHeader(SocketMessage.PROPERTY_TO));
		sendValues(message.getHeader(SocketMessage.PROPERTY_ID));
		sendValues(message.getHeader(SocketMessage.PROPERTY_SUBJECT));
		sendValues(message.getHeader(SocketMessage.PROPERTY_MIME));

		SimpleList<BaseItem> messages = message.getMessages();
		boolean multiPart = message.isMultiPart();
		String splitter = "--";
		if (multiPart) {
			sendValues(message.getHeader(SocketMessage.PROPERTY_CONTENTTYPE)
					+ message.getHeader(SocketMessage.PROPERTY_BOUNDARY));
		} else {
			sendValues(message.getHeader(SocketMessage.PROPERTY_CONTENTTYPE));
			sendValues(SocketMessage.CONTENT_ENCODING);
		}
		/* The CRLF separator between header and content */
		sendValues(BaseItem.CRLF);
		for (BaseItem msg : messages) {
			CharacterBuffer buffer = new CharacterBuffer();
			if (msg != null) {
				buffer.with(msg.toString());
			}
			if (multiPart) {
				sendValues(splitter + message.generateBoundaryValue());
				sendValues(SocketMessage.PROPERTY_CONTENTTYPE + message.getContentType(msg));
				sendValues(SocketMessage.CONTENT_ENCODING);
			}
			/* The CRLF separator between header and content */
			sendValues(BaseItem.CRLF);

			while (!buffer.isEnd()) {
				CharacterBuffer line = buffer.readLine();
				/* If the line begins with a ".", put an extra "." in front of it. */
				if (line.startsWith(".")) {
					sendValues('.');
				}
				sendValues(line.toString());
			}
		}
		SimpleKeyValueList<String, Buffer> attachments = message.getAttachments();
		for (int i = 0; i < attachments.size(); i++) {
			String fileName = attachments.get(i);
			Buffer buffer = attachments.getValueByIndex(i);
			sendValues(splitter + message.generateBoundaryValue());
			sendValues(SocketMessage.PROPERTY_CONTENTTYPE + SocketMessage.CONTENT_TYPE_PLAIN + " name=" + fileName);
			sendValues(SocketMessage.CONTENT_ENCODING);
			sendValues("Content-Disposition: attachment; filename=" + fileName);
			/* The CRLF separator between header and content */
			sendValues(BaseItem.CRLF);
			while (!buffer.isEnd()) {
				CharacterBuffer line = buffer.getString(1024);
				sendValues(line.toString());
			}
		}
		if (multiPart) {
			sendValues(splitter + message.generateBoundaryValue() + splitter);
		}
		/* A "." on a line by itself ends a message. */
		doCommand(".", RESPONSE_MAILACTIONOKEY);

		/* Message is sent. Close the connection to the server */
		return doCommand("QUIT", RESPONSE_SERVICE_CLOSING_TRANSMISSION);
	}

	/**
	 * Gets the last answer.
	 *
	 * @return the last answer
	 */
	public BufferedBuffer getLastAnswer() {
		return lastAnswer;
	}

	/**
	 * Gets the last sended.
	 *
	 * @return the last sended
	 */
	public String getLastSended() {
		return lastSended;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return this.host;
	}

	/**
	 * With type.
	 *
	 * @param msgType the msg type
	 * @return the message session
	 */
	public MessageSession withType(String msgType) {
		this.type = msgType;
		return this;
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	public NetworkParserLog getLogger() {
		return logger;
	}

	/**
	 * With logger.
	 *
	 * @param logger the logger
	 * @return the message session
	 */
	public MessageSession withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	/**
	 * With TLS.
	 *
	 * @param value the value
	 * @return the message session
	 */
	public MessageSession withTLS(boolean value) {
		this.useTLS = value;
		return this;
	}
}
