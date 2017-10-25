package de.uniks.networkparser.ext.petaf;

import java.io.IOException;
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
import de.uniks.networkparser.list.SimpleList;

public class Message implements SendableEntityCreator, SendableEntityCreatorNoIndex{
	public static final String PROPERTY_HISTORYID="id";
	public static final String PROPERTY_PREVIOUSCHANGE="prevChange";
	public static final String PROPERTY_MSG="msg";
	public static final String PROPERTY_RECEIVER="receiver";
	public static final String PROPERTY_RECEIVED="received";
	public static final String PROPERTY_PARENT="parent";
	public static final String PROPERTY_TYPE="type";
	public static final int TIMEOUTDEFAULT=0;
	private final static String[] props=new String[]{
			PROPERTY_TYPE,
			PROPERTY_HISTORYID,
			PROPERTY_MSG,
			PROPERTY_PREVIOUSCHANGE,
			PROPERTY_RECEIVER,
			PROPERTY_RECEIVED
	};
	protected String historyId;
	protected Object received;
	protected String prevChange;
	protected BaseItem msg;
	protected NodeProxy receiver;
	private int timeOut;
	private Socket session;
	private boolean sendAnyHow=false;
	private String type;

	public String getMessageId(Space space, NodeProxy proxy){
		if(this.historyId == null ){
			this.historyId = SHA1.value(getBlob()).toString();
		}
		return historyId;
	}

	public Message withType(String value) {
		this.type = value;
		return this;
	}

	public String getType() {
		return type;
	}

	public Message withHistoryId(String id){
		this.historyId = id;
		return this;
	}

	@SuppressWarnings("unchecked")
	public SimpleList<NodeProxy> getReceived() {
		if(received instanceof SimpleList<?>) {
			return (SimpleList<NodeProxy>) received;
		}
		SimpleList<NodeProxy> result=new SimpleList<NodeProxy>();
		if(received != null) {
			result.add(received);
		}
		return result;
	}

	public Message withAddToReceived(NodeProxy value) {
		if(this.received == null) {
			this.received = value;
		}
		SimpleList<?> list;
		if(this.received instanceof NodeProxy) {
			list = new SimpleList<NodeProxy>();
			list.with(this.received);
			this.received = list;
		} else {
			list = (SimpleList<?>) this.received;
		}
		list.add(value);
		return this;
	}

	public CharacterBuffer getBlob() {
		CharacterBuffer list=new CharacterBuffer();
		list.withObjects(getPrevChange(), getMessage(), getReceiver());
		return list;
	}

	public boolean handle(Space space) {
		return false;
	}

	public Message withReceiver(NodeProxy value) {
		this.receiver = value;
		return this;
	}

	public Message withMessage(BaseItem value){
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
		return receiver;
	}
	public BaseItem getMessage(){
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
		if(message != null) {
			return message.toString();
		}
		return super.toString();
	}

	public Socket getSession() {
		return session;
	}

	public boolean write(String answer) {
		try {
			OutputStream outputStream = session.getOutputStream();
			outputStream.write(answer.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public Message withSession(Socket session) {
		this.session = session;
		return this;
	}

	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Message();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof Message == false) {
			return null;
		}
		Message msg = (Message) entity;
		if(PROPERTY_HISTORYID.equalsIgnoreCase(attribute)){
			return msg.historyId;
		}
		if(PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)){
			return msg.getPrevChange();
		}
		if(PROPERTY_MSG.equalsIgnoreCase(attribute)){
			return msg.getMessage();
		}
		if(PROPERTY_RECEIVER.equalsIgnoreCase(attribute)){
			return msg.getReceiver();
		}
		if(PROPERTY_RECEIVED.equalsIgnoreCase(attribute)){
			return msg.getReceived();
		}
		if(PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			return msg.getType();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(attribute == null || entity instanceof Message == false) {
			return false;
		}
		Message msg = (Message) entity;
		if(PROPERTY_HISTORYID.equalsIgnoreCase(attribute)){
			msg.withHistoryId((String)value);
		}
		if(PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)){
			msg.withPrevChange((String) value);
			return true;
		}
		if(PROPERTY_MSG.equalsIgnoreCase(attribute)){
			msg.withMessage((JsonObject) value);
			return true;
		}
		if(PROPERTY_RECEIVER.equalsIgnoreCase(attribute)){
			msg.withReceiver((NodeProxy) value);
			return true;
		}
		if(PROPERTY_RECEIVED.equalsIgnoreCase(attribute)){
			msg.withAddToReceived((NodeProxy) value);
			return true;
		}
		if(PROPERTY_TYPE.equalsIgnoreCase(attribute)) {
			msg.withType((String)value);
			return true;
		}
		return false;
	}
}
