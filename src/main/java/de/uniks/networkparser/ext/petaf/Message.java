package de.uniks.networkparser.ext.petaf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.StringEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.SHA1;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;

public class Message {
	public static final String PROPERTY_HISTORYID="id";
	public static final String PROPERTY_PREVIOUSCHANGE="prevChange";
	public static final String PROPERTY_MSG="msg";
	public static final String PROPERTY_RECEIVER="receiver";
	public static final String PROPERTY_RECEIVED="received";
	public static final String PROPERTY_PARENT="parent";
	public static final int TIMEOUTDEFAULT=0;
	
	protected String historyId;
	protected SimpleList<String> received=new SimpleList<String>();
	protected String prevChange;
	protected BaseItem msg;
	protected NodeProxy receiver;
	private int timeOut;
	private Socket session;
	private boolean sendAnyHow=false;
	
	public String getMessageId(Space space, NodeProxy proxy){
		if(this.historyId == null ){
			this.historyId = SHA1.value(getBlob()).toString();
		}
		return historyId;
	}
	
	public Message withHistoryId(String id){
		this.historyId = id;
		return this;
	}
	
	public SimpleList<String> getReceived() {
		return received;
	}

	public Message withAddToReceived(String value) {
		this.received.add(value);
		return this;
	}
	public void addToReceived(BaseItem value) {
		this.received.add(value.toString());
	}
	
	public CharacterBuffer getBlob() {
		CharacterBuffer list=new CharacterBuffer();
		list.withObjects(getPrevChange(), getMessage(), getReceiver());
		return list;
	}

	public boolean set(String attribute, Object value) {
		if(PROPERTY_HISTORYID.equalsIgnoreCase(attribute)){
			withHistoryId((String)value);
		}
		if(PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)){
			withPrevChange((String) value);
			return true;
		}
		if(PROPERTY_MSG.equalsIgnoreCase(attribute)){
			withData((JsonObject) value);
			return true;
		}
		if(PROPERTY_RECEIVER.equalsIgnoreCase(attribute)){
			withReceiver((NodeProxy) value);
			return true;
		}		
		if(PROPERTY_RECEIVED.equalsIgnoreCase(attribute)){
			withAddToReceived((String) value);
			return true;
		}
		return false;
	}
	
	public Object get(String attribute) {
		if(PROPERTY_HISTORYID.equalsIgnoreCase(attribute)){
			return historyId;
		}
		if(PROPERTY_PREVIOUSCHANGE.equalsIgnoreCase(attribute)){
			return getPrevChange();
		}
		if(PROPERTY_MSG.equalsIgnoreCase(attribute)){
			return getMessage();
		}
		if(PROPERTY_RECEIVER.equalsIgnoreCase(attribute)){
			return getReceiver();
		}
		if(PROPERTY_RECEIVED.equalsIgnoreCase(attribute)){
			return getReceived();
		}
		return null;
	}
	
	public Message withReceiver(NodeProxy value) {
		this.receiver = value;
		return this;
	}

	public Message withData(BaseItem value){
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

	boolean containsVisited(String key) {
		return received.contains(key);
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
		Message message = new Message().withSendAnyHow(true).withData(stringEntity);
		return message;
	}
	
	@Override
	public String toString() {
		return getMessage().toString();
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
}
