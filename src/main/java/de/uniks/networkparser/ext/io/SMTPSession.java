package de.uniks.networkparser.ext.io;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.ByteConverter64;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class SMTPSession {
	public final static String RESPONSE_SERVERREADY = "220";
	public final static String RESPONSE_MAILACTIONOKEY="250";
	public final static String RESPONSE_STARTMAILINPUT="354";
	public final static String RESPONSE_SMTP_AUTH_NTLM_BLOB_Response="334";
	public final static String RESPONSE_LOGIN_SUCCESS="235";
	public final static String RESPONSE_SERVICE_CLOSING_TRANSMISSION="221"; 
	public static final int SSLPORT=587;
	/** 15 sec. socket read timeout */
	public static final int SOCKET_READ_TIMEOUT = 15 * 1000;
	private static final byte[] CRLF = { (byte)'\r', (byte)'\n' };

	private String host;
	private int port;
	private String sender;
	protected Socket serverSocket;
	protected BufferedReader in;
	protected OutputStream out;
	protected SimpleList<String> supportedFeature = new SimpleList<String>();
	private boolean allowutf8;
	private CharacterBuffer lastAnswer;
	private String lastSended;

	/**
	 * Creates new SMTP session by given SMTP host and port, sender email address
	 * @param host SMTP host
	 * @param port SMTP port
	 * @param sender email address of sender
	 */
	public SMTPSession(String host, int port, String sender) {
		this.host = host;
		this.port = port;
		this.sender = sender;
	}

	/**
	 * Creates new SMTP session by given SMTP host, sender email address,
	 * Assumes SMTP port is 25 (default for SMTP service).
	 * @param host SMTP host
	 * @param sender email address of sender
	 */
	public SMTPSession(String host, String sender) {
		this(host, 25, sender);
	}
	
	/**
	 * Creates new SMTP session
	 */
	public SMTPSession() {
	}
	
	public SMTPSession connectSSL(String host, String sender, String password) {
		this.host = host;
		this.port = SSLPORT;
		this.sender = sender;
		this.connect(sender, password);
		return this;
	}

	public String getSender() {
		return sender;
	}
	
	public boolean setSender(String sender) {
		if(EntityUtil.stringEquals(this.sender, sender) == false) {
			this.sender = sender;
			return true;
		}
		return false;
	}
	
	public int getPort() {
		return port;
	}
	
	public SMTPSession withPort(int port) {
		this.port = port;
		return this;
	}
	
	public SMTPSession withHost(String url) {
		this.host = url;
		return this;
	}
	
	
	/**
	 * Closes down the connection to SMTP server (if open). Should be called if
	 * an exception is raised during the SMTP session.
	 * @return success
	 */
	public boolean close() {
		try {
			in.close();
			out.close();
			serverSocket.close();
		} catch (Exception ex) {
			// Ignore the exception. Probably the socket is not open.
			return false;
		}
		return true;
	}
	
	public boolean connect(String password) {
		return this.connect(this.sender, password);
	}
	
	/**
	 * Connects to the SMTP server and gets input and output streams (in, out).
	 * @param userName the Username
	 * @param password the password
	 * @return success
	 */
	public boolean connect(String userName, String password) {
		if(serverSocket == null) {
			if(host == null) {
				return false;
			}
			try {
				serverSocket = new Socket(host, port);
				serverSocket.setSoTimeout(SOCKET_READ_TIMEOUT);
	
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
				out = serverSocket.getOutputStream();
				
				checkServerResponse(getResponse(), RESPONSE_SERVERREADY);
				
				sendHelo();
				
				CharacterBuffer answer;
				answer = sendCommand("STARTTLS");
	
				startTLS();
				sendHelo();
				
				answer = sendCommand("AUTH LOGIN");
	
				if(checkServerResponse(answer, RESPONSE_SMTP_AUTH_NTLM_BLOB_Response) == false) {
					close();
					return false;
				}
				ByteConverter64 converter = new ByteConverter64();
				answer= sendCommand(converter.toStaticString(userName).toString());
				if(checkServerResponse(answer, RESPONSE_SMTP_AUTH_NTLM_BLOB_Response) == false) {
					close();
					return false;
				}
				// send passwd
				answer = sendCommand(converter.toStaticString(password).toString());
				if(checkServerResponse(answer, RESPONSE_LOGIN_SUCCESS) == false) {
					close();
					return false;
				}
			}catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	private boolean sendHelo() {
		String response = sendCommand("EHLO " + getLocalHost()).toString();
		supportedFeature.clear();
		String[] lines = response.split("\n");
		// Skip first line
		for(int i=1;i<lines.length;i++) {
			supportedFeature.add(lines[i]);
		}
		return checkServerResponse(response, RESPONSE_MAILACTIONOKEY);
	}

	public void startTLS() {
		SSLSocketFactory ssf = (SSLSocketFactory)SSLSocketFactory.getDefault();
		try {
			this.serverSocket = ssf.createSocket(this.serverSocket, host, port, true);
			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
			out = serverSocket.getOutputStream();
			if (this.serverSocket instanceof SSLSocket) {
				SSLSocket socket = (SSLSocket) this.serverSocket;
				String[] prots = socket.getEnabledProtocols();
				SimpleList<String> eprots = new SimpleList<String>();
				for (int i = 0; i < prots.length; i++) {
					if (prots[i] != null && !prots[i].startsWith("SSL"))
						eprots.add(prots[i]);
				}
				socket.setEnabledProtocols(eprots.toArray(new String[eprots.size()]));
				socket.startHandshake();
			}			
		} catch (Exception e) {
		}
	}

	/**
	 * Connects to the SMTP server and gets input and output streams (in, out).
	 * @return success
	 */
	protected boolean connect() {
		return this.connect(null, null);
	}

	/**
	 * Sends given command and waits for a response from server.
	 * @param commandString String for sending
	 * @return response received from the server.
	 */
	protected CharacterBuffer sendCommand(String commandString) {
		this.lastSended = commandString;
		byte[] cmd = toBytes(commandString);
		sendValues(cmd);
		CharacterBuffer response = getResponse();
		return response;
	}
	

	protected void sendValues(String commandString) {
		this.lastSended = commandString;
		byte[] cmd = toBytes(commandString);
		sendValues(cmd);
	}

	/**
	 * Sends given command and waits for a response from server.
	 * 
	 * @param cmd bytes for sending
	 */
	protected void sendValues(byte... cmd) {
		try {
			out.write(cmd);
			out.write(CRLF);
			out.flush();
		} catch (IOException e) {
		}
	}
	
	
    /**
     * Convert the String to either ASCII or UTF-8 bytes
     * depending on allowutf8.
     * @param s string to convert
     * @return convert String to Byte
     */
	private byte[] toBytes(String s) {
		if(s == null) {
			return null;
		}
		if (allowutf8) {
			return s.getBytes(StandardCharsets.UTF_8);
		}
		// don't use StandardCharsets.US_ASCII because it rejects non-ASCII
		return s.getBytes();
	}

	/**
	 * Sends given commandString to the server, gets its reply and checks if it
	 * starts with expectedResponseStart. If not, throws IOException with
	 * server's reply (which is unexpected).
	 * @param commandString the Command to send
	 * @param responseCode expected value of Response
	 * @return success
	 */
	protected boolean doCommand(String commandString, String responseCode) {
		CharacterBuffer response = sendCommand(commandString);
		return checkServerResponse(response, responseCode);
	}

	/**
	 * Checks if given server reply starts with expectedResponseStart. If not,
	 * @param response Response as String
	 * @param code check the response for response code
	 * @return success
	 */
	protected boolean checkServerResponse(CharSequence response, String code) {
		if(response == null || code == null) {
			return false;
		}
		if(response.length()<code.length()) {
			return false;
		}
		int i=0;
		while(i<code.length()) {
			if(response.charAt(i) != code.charAt(i)) {
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * Gets a response back from the server. Handles multi-line responses
	 * (according to SMTP protocol) and returns them as multi-line string. Each
	 * line of the server's reply consists of 3-digit number followed by some
	 * text. If there is a '-' immediately after the number, the SMTP response
	 * continues on the next line. Otherwise it finished at this line.
	 * @return get the current Response
	 */
	protected CharacterBuffer getResponse() {
		CharacterBuffer response = new CharacterBuffer();

		String line = null;
		do {
			try {
				line = in.readLine();
			}catch (Exception e) {
			}
			if ((line == null)) {
				// SMTP response lines should at the very least have a 3-digit
				// number
				response.with("[EOF]");
				return response;
			}
			response.with(line).with('\n');
		} while ((line.length() > 3) && (line.charAt(3) == '-'));
		this.lastAnswer = response;
		return response;
	}

	/**
	 * Get the name of the local host, for use in the EHLO and HELO commands.
	 * The property InetAddress would tell us.
	 *
	 * @return the local host name
	 */
	public String getLocalHost() {
		InetAddress localHost;
		String localHostName = null;
		// get our hostname and cache it for future use
		try {
			localHost = InetAddress.getLocalHost();
			localHostName = localHost.getCanonicalHostName();
			// if we can't get our name, use local address literal
			if (localHostName == null) {
				// XXX - not correct for IPv6
				localHostName = "[" + localHost.getHostAddress() + "]";
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// last chance, try to get our address from our socket
		if (localHostName == null || localHostName.length() <= 0) {
			if (serverSocket != null && serverSocket.isBound()) {
				localHost = serverSocket.getLocalAddress();
				localHostName = localHost.getCanonicalHostName();
				// if we can't get our name, use local address literal
				if (localHostName == null)
					// XXX - not correct for IPv6
					localHostName = "[" + localHost.getHostAddress() + "]";
			}
		}
		return localHostName;
	}
	
	public String getLocalAdress() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
		    return "@"+localHost.getHostName();
		}catch (Exception e) {
		}
	    return "mailer@localhost"; // worst-case default
	}
	
	/**
	 * Sends a message using the SMTP protocol.
	 * @param message to send
	 * @return success
	 */
	public boolean sendMessage(EMailMessage message) {
		if(connect() == false) {
			return false;
		}

		// Tell the server who this message is from
		if(doCommand(message.getHeaderFrom(this.sender), RESPONSE_MAILACTIONOKEY) == false) {
			return false;
		}

		// Now tell the server who we want to send a message to
		SimpleList<String> headerTo = message.getHeaderTo();
		int pos=0;
		for(int i=0;i<headerTo.size();i++) {
			if(doCommand(headerTo.get(i), RESPONSE_MAILACTIONOKEY) == false) {
				message.removeToAdress(pos);
			} else {
				pos++;
			}
		}

		// Okay, now send the mail message. We expect a response beginning
		// with '3' indicating that the server is ready for data.
		if(doCommand("DATA", RESPONSE_STARTMAILINPUT)  == false) {
			return false;
		}
		
		message.generateMessageId(this.getLocalAdress());
		
		// Send the message headers
		sendValues(message.getHeader(EMailMessage.PROPERTY_DATE));
		sendValues(message.getHeader(EMailMessage.PROPERTY_FROM));
		sendValues(message.getHeader(EMailMessage.PROPERTY_TO));
		sendValues(message.getHeader(EMailMessage.PROPERTY_ID));
		sendValues(message.getHeader(EMailMessage.PROPERTY_SUBJECT));
		sendValues(message.getHeader(EMailMessage.PROPERTY_MIME));
		
		SimpleList<BaseItem> messages = message.getMessages();
		boolean multiPart = message.isMultiPart();
		String splitter="--";
		if(multiPart) {
			sendValues(message.getHeader(EMailMessage.PROPERTY_CONTENTTYPE)+message.getHeader(EMailMessage.PROPERTY_BOUNDARY));
		} else {
			sendValues(message.getHeader(EMailMessage.PROPERTY_CONTENTTYPE));
			sendValues(EMailMessage.CONTENT_ENCODING);
		}
		// The CRLF separator between header and content
		sendValues(CRLF);
		for(BaseItem msg : messages) {
			CharacterBuffer buffer=new CharacterBuffer();
			if(msg != null) {
				buffer.with(msg.toString());
			}
			if(multiPart) {
				sendValues(splitter+message.generateBoundaryValue());
				sendValues(EMailMessage.PROPERTY_CONTENTTYPE+message.getContentType(msg));
				sendValues(EMailMessage.CONTENT_ENCODING);
			}
			// The CRLF separator between header and content
			sendValues(CRLF);
			
			while(buffer.isEnd() == false) {
				CharacterBuffer line=buffer.readLine();
				// If the line begins with a ".", put an extra "." in front of it.
				if (line.startsWith(".")) {
					sendValues((byte)'.');
				}
				sendValues(line.toByteArray());
			}
		}
		SimpleKeyValueList<String, Buffer> attachments = message.getAttachments();
		for(int i=0;i<attachments.size();i++) {
			String fileName = attachments.get(i);
			Buffer buffer = attachments.getValueByIndex(i);
			sendValues(splitter+message.generateBoundaryValue());
			sendValues(EMailMessage.PROPERTY_CONTENTTYPE+EMailMessage.CONTENT_TYPE_PLAIN+" name="+fileName);
			sendValues(EMailMessage.CONTENT_ENCODING);
			sendValues("Content-Disposition: attachment; filename="+fileName);
			// The CRLF separator between header and content
			sendValues(CRLF);
			while(buffer.isEnd() == false) {
				CharacterBuffer line=buffer.getString(1024);
				sendValues(line.toByteArray());
			}
		}
		if(multiPart) {
			sendValues(splitter+message.generateBoundaryValue()+splitter);
		}
		// A "." on a line by itself ends a message.
		doCommand(".", RESPONSE_MAILACTIONOKEY);

		// Message is sent. Close the connection to the server
		return doCommand("QUIT", RESPONSE_SERVICE_CLOSING_TRANSMISSION);
	}
	
	public CharacterBuffer getLastAnswer() {
		return lastAnswer;
	}
	
	public String getLastSended() {
		return lastSended;
	}

	public String getUrl() {
		return this.host;
	}
}
