package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.io.SocketMessage;
import de.uniks.networkparser.ext.io.MessageSession;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.interfaces.ObjectCondition;

// This.name is receiver
// https://console.firebase.google.com/project/<Project>/settings/cloudmessaging/
public class NodeProxyMessages extends NodeProxy{
	public static final String EVENT_CONNECTION="connection";
	public static final String BODY="body";
	public static final String MESSAGE="message";
	public static final String PROPERTY_URL = "url";
	public static final String PROPERTY_PORT = "port";
	public static final String PROPERTY_ACCOUNT = "account";
	public static final String PROPERTY_PASSWORD = "password";
	public static final String PROPERTY_MESSAGETYPE = "msgtype";
	
	private MessageSession connection = null;
	private ObjectCondition creator;
	private String password;
	private String msgType=MessageSession.TYPE_EMAIL;
	
	public NodeProxyMessages() {
		this.property.addAll(PROPERTY_URL, PROPERTY_PORT, PROPERTY_ACCOUNT, PROPERTY_MESSAGETYPE);
		this.propertyUpdate.addAll(PROPERTY_URL, PROPERTY_PORT, PROPERTY_MESSAGETYPE);
		this.propertyInfo.addAll(PROPERTY_URL, PROPERTY_PORT, PROPERTY_ACCOUNT,PROPERTY_MESSAGETYPE);
	}
	
	@Override
	public int compareTo(NodeProxy o) {
		return 0;
	}

	@Override
	public Object getValue(Object element, String attrName) {
		if(element instanceof NodeProxyMessages ) {
			NodeProxyMessages nodeProxy = (NodeProxyMessages) element;
			if (PROPERTY_URL.equals(attrName)) {
				return nodeProxy.getUrl();
			}
			if (PROPERTY_PORT.equals(attrName)) {
				return nodeProxy.getPort();
			}
			if (PROPERTY_ACCOUNT.equals(attrName)) {
				return getKey();
			}
			if (PROPERTY_PASSWORD.equals(attrName)) {
				return nodeProxy.getPort();
			}
			if (PROPERTY_MESSAGETYPE.equals(attrName)) {
				return nodeProxy.getMessageType();
			}
		}
		return super.getValue(element, attrName);
	}

	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if(element instanceof NodeProxyMessages) {
			NodeProxyMessages nodeProxy = (NodeProxyMessages) element;
			if (PROPERTY_URL.equals(attrName)) {
				nodeProxy.withUrl((String) value);
				return true;
			}
			if (PROPERTY_PORT.equals(attrName)) {
				nodeProxy.withPort((Integer) value);
				return true;
			}
			if (PROPERTY_ACCOUNT.equals(attrName)) {
				nodeProxy.withSender((String) value);
				return true;
			}
			if (PROPERTY_PASSWORD.equals(attrName)) {
				nodeProxy.withPassword((String) value);
				return true;
			}
			if (PROPERTY_MESSAGETYPE.equals(attrName)) {
				nodeProxy.withMessageType((String) value);
				return true;
			}
		}
		return super.setValue(element, attrName, value, type);
	}

	private NodeProxyMessages withMessageType(String value) {
		this.msgType = value;
		return this;
	}

	private String getMessageType() {
		return msgType;
	}

	@Override
	public String getKey() {
		if(this.connection != null) {
			return this.connection.getSender();
		}
		return null;
	}

	@Override
	public boolean close() {
		if(connection != null) {
			MessageSession conn = connection;
			connection = null;
			return conn.close();
		}
		return true;
	}

	@Override
	protected boolean initProxy() {
		withType(NodeProxy.TYPE_INOUT);
		return true;
	}

	@Override
	public boolean isSendable() {
		if(this.connection != null) {
			return this.connection.getSender() != null;
		}
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NodeProxyMessages();
	}
	
	public boolean connect() {
		if(this.connection != null) {
			this.connection.withType(this.msgType);
			return this.connection.connect(password);
			// TYPE
		}
		return false;
	}
	
	public NodeProxyMessages withSender(String name) {
		if(this.connection == null) {
			this.connection = getNewConnection();
		}
		this.connection.setSender(name);
		return this;
	}
	
	protected MessageSession getNewConnection() {
		if(creator != null) {
			Object item = creator.update(EVENT_CONNECTION);
			if(item instanceof MessageSession) {
				return (MessageSession) item;
			}
		}
		return new MessageSession();
	}
	
	protected SocketMessage getNewEMailMessage() {
		if(creator != null) {
			Object item = creator.update(MESSAGE);
			if(item != null) {
				return (SocketMessage) item;
			}
		}
		return new SocketMessage(this.name).withSubject("Message from PetaF");
	}
	
	private NodeProxyMessages withPort(Integer value) {
		if(this.connection == null) {
			this.connection = getNewConnection();
		}
		this.connection.withPort(value);
		return this;
	}
	
	public int getPort() {
		if(this.connection != null) {
			return this.connection.getPort();
		}
		return -1;
	}


	public NodeProxyMessages withUrl(String value) {
		if(this.connection == null) {
			this.connection = getNewConnection();
		}
		this.connection.withHost(value);
		return this;
	}
	
	
	public String getUrl() {
		if(this.connection != null) {
			return this.connection.getUrl();
		}
		return null;
		
	}

	public NodeProxyMessages withPassword(String password) {
		this.password = password;
		return this;
	}
	
	@Override
	protected boolean sending(Message msg) {
		if (super.sending(msg)) {
			return true;
		}
		if(this.connection == null) {
			return false;
		}
		SocketMessage message=getNewEMailMessage();
		
		String buffer;
		if(this.space != null) {
			buffer = this.space.convertMessage(msg);
		} else {
			buffer = msg.toString();
		}
		if(this.creator != null) {
			Object item = creator.update(buffer);
			if(item instanceof String) {
				buffer = (String) item;
			}
		}
		
		message.withMessage(buffer);
		boolean success = this.connection.sending(message);
		if(success) {
			setSendTime(buffer.length());
		}
		return success;
	}
}
