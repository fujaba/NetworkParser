package de.uniks.networkparser.ext.petaf.proxy;

import de.uniks.networkparser.ext.io.EMailMessage;
import de.uniks.networkparser.ext.io.SMTPSession;
import de.uniks.networkparser.ext.petaf.Message;
import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.NodeProxyType;
import de.uniks.networkparser.interfaces.ObjectCondition;

// This.name is receiver
public class NodeProxyEMail extends NodeProxy{
	public static final String CONNECTION="connection";
	public static final String BODY="body";
	public static final String MESSAGE="message";
	public static final String PROPERTY_URL = "url";
	public static final String PROPERTY_PORT = "port";
	public static final String PROPERTY_ACCOUNT = "account";
	public static final String PROPERTY_PASSWORD = "password";
	private SMTPSession connection = null;
	private ObjectCondition creator;
	private String password;
	
	public NodeProxyEMail() {
		this.property.addAll(PROPERTY_URL, PROPERTY_PORT, PROPERTY_ACCOUNT);
		this.propertyUpdate.addAll(PROPERTY_URL, PROPERTY_PORT);
		this.propertyInfo.addAll(PROPERTY_URL, PROPERTY_PORT, PROPERTY_ACCOUNT);
	}

	
	@Override
	public int compareTo(NodeProxy o) {
		return 0;
	}
	
	
	
	@Override
	public Object getValue(Object element, String attrName) {
		if(element instanceof NodeProxyEMail ) {
			NodeProxyEMail nodeProxy = (NodeProxyEMail) element;
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
		}
		return super.getValue(element, attrName);
	}

	@Override
	public boolean setValue(Object element, String attrName, Object value, String type) {
		if(element instanceof NodeProxyEMail) {
			NodeProxyEMail nodeProxy = (NodeProxyEMail) element;
			if (PROPERTY_URL.equals(attrName)) {
				nodeProxy.withUrl((String) value);
				return true;
			}
			if (PROPERTY_PORT.equals(attrName)) {
				nodeProxy.withPort((Integer) value);
				return true;
			}
			if (PROPERTY_ACCOUNT.equals(attrName)) {
				nodeProxy.withEMailAccount((String) value);
				return true;
			}
			if (PROPERTY_PASSWORD.equals(attrName)) {
				nodeProxy.withPassword((String) value);
				return true;
			}
		}
		return super.setValue(element, attrName, value, type);
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
			SMTPSession conn = connection;
			connection = null;
			return conn.close();
		}
		return true;
	}

	@Override
	protected boolean initProxy() {
		withType(NodeProxyType.INOUT);
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
		return new NodeProxyEMail();
	}
	
	public boolean connect() {
		if(this.connection != null) {
			return this.connection.connect(password);
		}
		return false;
	}
	
	public NodeProxyEMail withEMailAccount(String name) {
		if(this.connection == null) {
			this.connection = getNewConnection();
		}
		this.connection.setSender(name);
		return this;
	}
	
	protected SMTPSession getNewConnection() {
		if(creator != null) {
			Object item = creator.update(CONNECTION);
			if(item != null) {
				return (SMTPSession) item;
			}
		}
		return new SMTPSession();
	}
	
	protected EMailMessage getNewEMailMessage() {
		if(creator != null) {
			Object item = creator.update(MESSAGE);
			if(item != null) {
				return (EMailMessage) item;
			}
		}
		return new EMailMessage(this.name).withSubject("Message from PetaF");
	}
	
	private NodeProxyEMail withPort(Integer value) {
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


	public NodeProxyEMail withUrl(String value) {
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

	public NodeProxyEMail withPassword(String password) {
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
		EMailMessage message=getNewEMailMessage();
		
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
		boolean success = this.connection.sendMessage(message);
		if(success) {
			setSendTime(buffer.length());
		}
		return success;
	}
}
