package de.uniks.networkparser.ext.petaf;

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
import java.io.OutputStream;
import java.net.Socket;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.StringEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.SHA1;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleSet;

/**
 * Simple Message.
 *
 * @author Stefan Lindel
 */
public class Message implements SendableEntityCreator, SendableEntityCreatorNoIndex {
	
	/** The Constant PROPERTY_HISTORYID. */
	public static final String PROPERTY_HISTORYID = "id";
	
	/** The Constant PROPERTY_PREVIOUSCHANGE. */
	public static final String PROPERTY_PREVIOUSCHANGE = "prevChange";
	
	/** The Constant PROPERTY_MSG. */
	public static final String PROPERTY_MSG = "msg";
	
	/** The Constant PROPERTY_RECEIVED. */
	public static final String PROPERTY_RECEIVED = "received";
	
	/** The Constant PROPERTY_PARENT. */
	public static final String PROPERTY_PARENT = "parent";
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";
	
	/** The Constant TIMEOUTDEFAULT. */
	public static final int TIMEOUTDEFAULT = 0;
	protected final static PropertyList props = PropertyList.create(PROPERTY_TYPE, PROPERTY_HISTORYID, PROPERTY_MSG,
			PROPERTY_RECEIVED, PROPERTY_PREVIOUSCHANGE);
	protected String historyId;
	protected Object received;
	protected String prevChange;
	protected BaseItem msg;
	protected int timeOut;
	protected boolean sendAnyHow = false;
	protected String type;
	protected Object session;
	protected Space space;

	/**
	 * With space.
	 *
	 * @param space the space
	 * @return the message
	 */
	public Message withSpace(Space space) {
		this.space = space;
		return this;
	}

	/**
	 * Gets the space.
	 *
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	/**
	 * Gets the message id.
	 *
	 * @param space the space
	 * @param proxy the proxy
	 * @return the message id
	 */
	public String getMessageId(Space space, NodeProxy proxy) {
		if (this.historyId == null) {
			this.historyId = SHA1.value(getBlob()).toString();
		}
		return historyId;
	}

	/**
	 * With type.
	 *
	 * @param value the value
	 * @return the message
	 */
	public Message withType(String value) {
		this.type = value;
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		/* Inkluisive FallBack */
		if (type != null) {
			return type;
		}
		return this.getClass().getName();
	}

	/**
	 * With history id.
	 *
	 * @param id the id
	 * @return the message
	 */
	public Message withHistoryId(String id) {
		this.historyId = id;
		return this;
	}

	/**
	 * Gets the received.
	 *
	 * @return the received
	 */
	@SuppressWarnings("unchecked")
	public SimpleSet<NodeProxy> getReceived() {
		if (received instanceof SimpleSet<?>) {
			return (SimpleSet<NodeProxy>) received;
		}
		SimpleSet<NodeProxy> result = new SimpleSet<NodeProxy>();
		if (received != null) {
			result.add(received);
		}
		return result;
	}

	/**
	 * With add to received.
	 *
	 * @param <ST> the generic type
	 * @param value the value
	 * @return the st
	 */
	@SuppressWarnings("unchecked")
	public <ST extends Message> ST withAddToReceived(NodeProxy value) {
		if (this.received == null) {
			this.received = value;
			return (ST) this;
		}
		SimpleSet<?> list;
		if (this.received instanceof NodeProxy) {
			list = new SimpleSet<NodeProxy>();
			list.with(this.received);
			this.received = list;
		} else {
			list = (SimpleSet<?>) this.received;
		}
		list.add(value);
		return (ST) this;
	}

	/**
	 * Gets the blob.
	 *
	 * @return the blob
	 */
	public CharacterBuffer getBlob() {
		CharacterBuffer list = new CharacterBuffer();
		list.withObjects(getPrevChange(), getMessage(), getReceiver());
		return list;
	}

	/**
	 * Handle.
	 *
	 * @param space the space
	 * @return true, if successful
	 */
	public boolean handle(Space space) {
		return false;
	}

	/**
	 * Checks if is sending to peers.
	 *
	 * @return true, if is sending to peers
	 */
	public boolean isSendingToPeers() {
		return true;
	}

	/**
	 * With message.
	 *
	 * @param value the value
	 * @return the message
	 */
	public Message withMessage(BaseItem value) {
		this.msg = value;
		return this;
	}

	/**
	 * Gets the prev change.
	 *
	 * @return the prev change
	 */
	public String getPrevChange() {
		return prevChange;
	}

	/**
	 * With prev change.
	 *
	 * @param prevChange the prev change
	 * @return the message
	 */
	public Message withPrevChange(String prevChange) {
		this.prevChange = prevChange;
		return this;
	}

	/**
	 * Gets the receiver.
	 *
	 * @return the receiver
	 */
	public NodeProxy getReceiver() {
		return getReceived().first();
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public BaseItem getMessage() {
		return msg;
	}

	/**
	 * Gets the time out.
	 *
	 * @return the time out
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * With time out.
	 *
	 * @param timeOut the time out
	 * @return the message
	 */
	public Message withTimeOut(int timeOut) {
		this.timeOut = timeOut;
		return this;
	}

	/**
	 * Checks if is send any how.
	 *
	 * @return true, if is send any how
	 */
	public boolean isSendAnyHow() {
		return sendAnyHow;
	}

	/**
	 * With send any how.
	 *
	 * @param sendAnyHow the send any how
	 * @return the message
	 */
	public Message withSendAnyHow(boolean sendAnyHow) {
		this.sendAnyHow = sendAnyHow;
		return this;
	}

	protected IdMap getInternMap(Space space) {
		return space.getMap();
	}

	/**
	 * Creates the simple string.
	 *
	 * @param text the text
	 * @return the message
	 */
	public static Message createSimpleString(String text) {
		StringEntity stringEntity = new StringEntity();
		stringEntity.add(text);
		Message message = new Message().withSendAnyHow(true).withMessage(stringEntity);
		return message;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		BaseItem message = getMessage();
		if (message != null) {
			return message.toString();
		}
		return super.toString();
	}

	/**
	 * Write.
	 *
	 * @param answer the answer
	 * @return true, if successful
	 */
	public boolean write(String answer) {
		OutputStream stream = getOutputStream();
		if (stream == null) {
			return false;
		}
		try {
			stream.write(answer.getBytes());
			stream.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the output stream.
	 *
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		if (this.session instanceof Socket) {
			try {
				return ((Socket) session).getOutputStream();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * With session.
	 *
	 * @param session the session
	 * @return the message
	 */
	public Message withSession(Object session) {
		this.session = session;
		return this;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return props.getList();
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Message();
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (attribute == null || entity instanceof Message == false) {
			return null;
		}
		Message msg = (Message) entity;
		if (PROPERTY_HISTORYID.equalsIgnoreCase(attribute)) {
			return msg.historyId;
		}
		if (PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)) {
			return msg.getPrevChange();
		}
		if (PROPERTY_MSG.equalsIgnoreCase(attribute)) {
			return msg.getMessage();
		}
		if (PROPERTY_RECEIVED.equalsIgnoreCase(attribute)) {
			return msg.getReceived();
		}
		if (PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			return msg.getType();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute == null || entity instanceof Message == false) {
			return false;
		}
		Message msg = (Message) entity;
		if (PROPERTY_HISTORYID.equalsIgnoreCase(attribute)) {
			msg.withHistoryId((String) value);
		}
		if (PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)) {
			msg.withPrevChange((String) value);
			return true;
		}
		if (PROPERTY_MSG.equalsIgnoreCase(attribute)) {
			if (value instanceof JsonObject) {
				msg.withMessage((JsonObject) value);
			}
			return true;
		}
		if (PROPERTY_RECEIVED.equalsIgnoreCase(attribute)) {
			if (value instanceof NodeProxy) {
				msg.withAddToReceived((NodeProxy) value);
			}
			return true;
		}
		if (PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			msg.withType((String) value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	public Object getSession() {
		return session;
	}

	/**
	 * Sending.
	 *
	 * @param space the space
	 * @return true, if successful
	 */
	public boolean sending(Space space) {
		return false;
	}
}
