package de.uniks.networkparser.ext.petaf;

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

public class Message implements SendableEntityCreator, SendableEntityCreatorNoIndex {
	public static final String PROPERTY_HISTORYID = "id";
	public static final String PROPERTY_PREVIOUSCHANGE = "prevChange";
	public static final String PROPERTY_MSG = "msg";
	public static final String PROPERTY_RECEIVED = "received";
	public static final String PROPERTY_PARENT = "parent";
	public static final String PROPERTY_TYPE = "type";
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

	public Message withSpace(Space space) {
		this.space = space;
		return this;
	}
	
	public Space getSpace() {
		return space;
	}

	public String getMessageId(Space space, NodeProxy proxy) {
		if (this.historyId == null) {
			this.historyId = SHA1.value(getBlob()).toString();
		}
		return historyId;
	}

	public Message withType(String value) {
		this.type = value;
		return this;
	}

	public String getType() {
		// Inkluisive FallBack
		if (type != null) {
			return type;
		}
		return this.getClass().getName();
	}

	public Message withHistoryId(String id) {
		this.historyId = id;
		return this;
	}

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

	public CharacterBuffer getBlob() {
		CharacterBuffer list = new CharacterBuffer();
		list.withObjects(getPrevChange(), getMessage(), getReceiver());
		return list;
	}

	public boolean handle(Space space) {
		return false;
	}

	public boolean isSendingToPeers() {
		return true;
	}

	public Message withMessage(BaseItem value) {
		this.msg = value;
		return this;
	}

	public String getPrevChange() {
		return prevChange;
	}

	public Message withPrevChange(String prevChange) {
		this.prevChange = prevChange;
		return this;
	}

	public NodeProxy getReceiver() {
		return getReceived().first();
	}

	public BaseItem getMessage() {
		return msg;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public Message withTimeOut(int timeOut) {
		this.timeOut = timeOut;
		return this;
	}

	public boolean isSendAnyHow() {
		return sendAnyHow;
	}

	public Message withSendAnyHow(boolean sendAnyHow) {
		this.sendAnyHow = sendAnyHow;
		return this;
	}

	protected IdMap getInternMap(Space space) {
		return space.getMap();
	}

	public static Message createSimpleString(String text) {
		StringEntity stringEntity = new StringEntity();
		stringEntity.add(text);
		Message message = new Message().withSendAnyHow(true).withMessage(stringEntity);
		return message;
	}

	@Override
	public String toString() {
		BaseItem message = getMessage();
		if (message != null) {
			return message.toString();
		}
		return super.toString();
	}

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

	public OutputStream getOutputStream() {
		if (this.session instanceof Socket) {
			try {
				return ((Socket) session).getOutputStream();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public Message withSession(Object session) {
		this.session = session;
		return this;
	}

	@Override
	public String[] getProperties() {
		return props.getList();
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Message();
	}

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

	public Object getSession() {
		return session;
	}
	
	public boolean sending(Space space) {
		return false;
	}
}
