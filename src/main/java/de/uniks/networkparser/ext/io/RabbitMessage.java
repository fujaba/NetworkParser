package de.uniks.networkparser.ext.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.bytes.ByteTokener;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class RabbitMessage {
	public static final byte NULL = 0;
	
	public static final byte FRAME_METHOD = 1;
	public static final byte FRAME_HEADER = 2;
	public static final byte FRAME_BODY = 3;
	public static final byte FRAME_HEARTBEAT = 8;
	
	public static final short CONNECTION_CLASS=10;
	public static final short CHANNEL_CLASS=20;
	public static final short ACCESS_CLASS=30;
	public static final short Exchange_CLASS=40;
	public static final short STARTOK_METHOD=11;
	public static final short TUNE_METHOD=30;
	public static final short TUNEOK_METHOD=31;

	private byte[] headers=new byte[3];
	private static final byte FRAME_END =-50;
	private ByteBuffer payload;
	private ByteBuffer accumulator;
	private Map<String, Object> table;
	private SimpleList<Object> values = new SimpleList<Object>(); 
	private short classId;
	private short methodId;
	private SimpleKeyValueList<String, Object> payloadData = new SimpleKeyValueList<String, Object>();
	
	public RabbitMessage with(Object... values) {
		this.values.add(values);
		return this;
	}
	
	public RabbitMessage withAccumulator(Object... args) {
		if(accumulator == null) {
			accumulator = new ByteBuffer();
		}else {
			accumulator.clear();
		}
		byte[] ret=new byte[2];
		ret[1] = (byte)(this.classId & 0xff);
		ret[0] = (byte)((this.classId >> 8) & 0xff);
		accumulator.insert(ret);
		ret[1] = (byte)(this.methodId & 0xff);
		ret[0] = (byte)((this.methodId >> 8) & 0xff);
		accumulator.insert(ret);

		this.writeMap(this.table);
		if(args != null) {
			for(Object item : args) {
				writeValue( item );
			}
		}
		return this;
	}
	
	
	public RabbitMessage writeMap(Map<?, ?> map) {
		if (map == null) {
			accumulator.insert(NULL);
		}else {
			Set<?> keySet = map.keySet();
			for(Object key : keySet) {
				if(key instanceof String) {
					byte[] keyStr = ((String) key).getBytes();
					accumulator.insert((byte)keyStr.length);
					accumulator.insert(keyStr);
					writeFieldValue(map.get(key));
				}
			}
		}
		return this;
	}

	public boolean writeValue(Object value) {
		if(value == null) {
			accumulator.insert(NULL);
			return true;
		}
		if(value instanceof ByteEntity) {
			ByteEntity entity = (ByteEntity) value;
			byte type = entity.getType();
			byte group = EntityUtil.getGroup(type);
			byte subgroup = EntityUtil.getSubGroup(type);
			if(group == ByteTokener.DATATYPE_STRING) {
				byte[] bytes = entity.getValue();
				if(subgroup == ByteTokener.LEN_LITTLE) {
					accumulator.insert((byte)bytes.length);
				} else {
					accumulator.insert((Integer)bytes.length);
				}
				accumulator.insert(bytes);
			}
		}
		if(value instanceof String) {
			byte [] bytes = ((String)value).getBytes();
			accumulator.insert((Integer)bytes.length);
			accumulator.insert(bytes);
			return true;
		}
		if(value instanceof Integer ||
				value instanceof Byte ||
				value instanceof Double ||
				value instanceof Float ||
				value instanceof Long ||
				value instanceof Short ||
				value instanceof Boolean) {
			accumulator.insert(value);
			return true;
		}

		if(value instanceof Date) {
			Date date = (Date) value;
			accumulator.insert((long)date.getTime()/1000);
			return true;
		}
		
		if(value instanceof Map<?,?>) {
			writeMap((Map<?, ?>) value);
			return true;
		}
		if(value instanceof byte[]) {
			byte[] array = (byte[]) value;
			accumulator.insert((Integer) array.length);
			accumulator.insert(array);
			return true;
		}
		if(value instanceof List<?> || value instanceof Object[]) {
			int newPos, pos = accumulator.position();
			accumulator.insert((Integer) 1);
			if(value instanceof List<?>) {
				List<?> list = (List<?>) value;
				// Now Write Value
				for (Object item : list) {
					writeFieldValue(item);
				}
			} else {
				Object[] list = (Object[]) value;
				// Now Write Value
				for (Object item : list) {
					writeFieldValue(item);
				}
			}				
			newPos = accumulator.position();
			accumulator.withPosition(pos);
			accumulator.put(newPos - pos);
			accumulator.withPosition(newPos);
			return true;
		}
		return false;
	}	

	public boolean writeFieldValue(Object value) {
		if(value == null) {
			accumulator.insert('V');
			return true;
		}
		if(value instanceof String) {
			accumulator.insert('S');
			byte [] bytes = ((String)value).getBytes();
			accumulator.insert(bytes.length);
			accumulator.insert(bytes);
			return true;
		}
		if(value instanceof Integer) {
			accumulator.insert('I');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Date) {
			accumulator.insert('T');
			Date date = (Date) value;
			accumulator.insert((long)date.getTime()/1000);
			return true;
		}
		
		if(value instanceof Map<?,?>) {
			accumulator.insert('F');
			writeMap((Map<?, ?>) value);
			return true;
		}
		if(value instanceof Byte) {
			accumulator.insert('b');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Double) {
			accumulator.insert('d');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Float) {
			accumulator.insert('f');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Long) {
			accumulator.insert('l');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Short) {
			accumulator.insert('s');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof Boolean) {
			accumulator.insert('t');
			accumulator.insert(value);
			return true;
		}
		if(value instanceof byte[]) {
			accumulator.insert('x');
			byte[] array = (byte[]) value;
			accumulator.insert((Integer) array.length);
			accumulator.insert(array);
			return true;
		}
		if(value instanceof List<?> || value instanceof Object[]) {
			accumulator.insert('A');
			int newPos, pos = accumulator.position();
			accumulator.insert((Integer) 1);
			if(value instanceof List<?>) {
				List<?> list = (List<?>) value;
				// Now Write Value
				for (Object item : list) {
					writeFieldValue(item);
				}
			} else {
				Object[] list = (Object[]) value;
				// Now Write Value
				for (Object item : list) {
					writeFieldValue(item);
				}
			}				
			newPos = accumulator.position();
			accumulator.withPosition(pos);
			accumulator.put(newPos - pos);
			accumulator.withPosition(newPos);
			return true;
		}
		return false;
	}	
	public static RabbitMessage createStartOK(String... login) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(CONNECTION_CLASS, STARTOK_METHOD);
		String userStr ="guest", passwordStr = "guest";
		if(login.length>0) {
			userStr = login[0];
			if(login.length>1) {
				passwordStr = login[1]; 
			}
		}
//		SimpleList<String> list = new SimpleList<String>();
		CharacterBuffer sb=new CharacterBuffer();
		sb.with((char)0);
		sb.with(userStr);
		sb.with((char)0);
		sb.with(passwordStr);
		ByteEntity entity = ByteEntity.create(ByteTokener.DATATYPE_STRING + ByteTokener.LEN_LITTLE, "en_US");
		msg.with("PLAIN", sb.toString(), entity);
		return msg;
	}
	
	public static RabbitMessage createTuneOK() {
		RabbitMessage msg = new RabbitMessage();
		msg.withFrame(CONNECTION_CLASS, TUNEOK_METHOD);
		
		return null;
	}

	public RabbitMessage withFrame(short classId, short methodId) {
		this.classId = classId;
		this.methodId = methodId;
		return this;
	}

	public byte getType() {
		return headers[0];
	}
	
	public RabbitMessage withType(byte value) {
		this.headers[0] = value;
		return this;
	}
	
	public RabbitMessage withChannel(short value) {
		this.headers[1] = (byte) (value >>> 8);
		this.headers[2] = (byte) value;
		return this;
	}
	
	public short getChannel() {
		short result = headers[1];
		result = (short) (result << 8 + headers[2]);
		return result;
	}

	public boolean write(OutputStream stream) {
		try {
			stream.write(headers);
			if(accumulator == null && (table != null || values != null)) {
				withAccumulator(values.toArray(new Object[values.size()]));
			}
			if(accumulator != null) {
				// As 
				int length = accumulator.length();
				stream.write(ByteTokener.intToByte(length));
				stream.write(accumulator.array(), 0, length);
			}
			if(payload != null) {
				stream.write(payload.length());
				stream.write(payload.array());
			}
			stream.write(FRAME_END);
			stream.flush();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	* Protected API - Factory method to instantiate a Frame by reading an
	* AMQP-wire-protocol frame from the given input stream.
	* 
	* @param is DataInputStrem for reading
	*
	* @return a new RabbitMessage if we read a frame successfully, otherwise null
	*/
	public static RabbitMessage readFrom(DataInputStream is) throws IOException {

		byte type = (byte) is.readUnsignedByte();
		if (type == 'A') {
			return null;
		}
		short channel = (short) is.readUnsignedShort();
		int payloadSize = is.readInt();
		byte[] payload = new byte[payloadSize];
		is.readFully(payload);

		byte frameEndMarker = (byte) is.readUnsignedByte();
		if (frameEndMarker != RabbitMessage.FRAME_END) {
			return null;
		}
		RabbitMessage msg =new RabbitMessage();
		msg.withType(type);
		msg.withChannel(channel);
		msg.withPayload(payload);
		return msg;
	}

	public RabbitMessage withPayload(byte[] value) {
		this.payload = new ByteBuffer().with(value).flip(true);
		return this;
	}

	public boolean analysePayLoad() {
		classId = payload.getShort();
		methodId = payload.getShort();
		
		if(classId == CONNECTION_CLASS) {
			if(methodId == 10 ) { // START
				payloadData.add("version", payload.getByte() + "." + payload.getByte());
				payloadData.add("properties", readTable(payload));
				payloadData.add("mechanisms", new String(readBytes(payload)));
				return payloadData.add("locales", new String(readBytes(payload)));
			}
			if(methodId == STARTOK_METHOD) { // StartOk
				payloadData.add("clientProperties", readTable(payload));
				payloadData.add("mechanisms", payload.getShortstr());
				payloadData.add("response", new String(readBytes(payload)));
				return payloadData.add("locale", payload.getShortstr());
			}
			if(methodId == 20 || methodId == 21) {  // Secure, SecureOk
				return payloadData.add("challenge", new String(readBytes(payload)));
			}
			if(methodId == TUNEOK_METHOD || methodId == TUNEOK_METHOD) { // TuneOK, Tune
				payloadData.add("channelMax", payload.getShort());
				payloadData.add("frameMax", payload.getLong());
				return payloadData.add("heartbeat", payload.getShort());
			}
			if(methodId == 40) {  // Open
				return payloadData.add("outOfBand", payload.getShortstr());
			}
			if(methodId == 41) {  // OpenOK
				return payloadData.add("outOfBand", new String(readBytes(payload)));
			}
			if(methodId == 50) {  // Close`
				payloadData.add("replyCode", payload.getShort());
				payloadData.add("replyText", payload.getShortstr());
				payloadData.add("classId", payload.getShort());
				return payloadData.add("methodId", payload.getShort());
			}
			if(methodId == 51) {  // CloseOK
				return true;
			}
			if(methodId == 60) {  // Blocked
				return payloadData.add("reason", payload.getShortstr());
			}
			if(methodId == 61) {  // Unblocked
				return true;
			}
			return false;
		}
		if(classId == CHANNEL_CLASS) {
			if(methodId == 10) { // Open
				return payloadData.add("outOfBand", payload.getShortstr());
			}
			if(methodId == 11) { // OpenOK
				return payloadData.add("outOfBand", new String(readBytes(payload)));
			}
			if(methodId == 20) { // Flow
				return payloadData.add("active", payload.getBit());
			}
			if(methodId == 21) { //FlowOK
				return payloadData.add("active", payload.getBit());
			}
			if(methodId == 40) { // Close
				payloadData.add("replyCode", payload.getShort());
				payloadData.add("replyText", payload.getShortstr());
				payloadData.add("classId", payload.getShort());
				return payloadData.add("methodId", payload.getShort());
			}
			if(methodId == 41) { // CloseOK
				return true;
			}
			return false;
		}
		if(classId == ACCESS_CLASS) { // Request
			if(methodId == 10) {
				payloadData.add("realm", payload.getShortstr());
				payloadData.add("exclusive", payload.getBit());
				payloadData.add("passive", payload.getBit());
				payloadData.add("active", payload.getBit());
				payloadData.add("write", payload.getBit());
				return payloadData.add("read", payload.getBit());
			}
			if(methodId == 11) { //RequestOK
				return payloadData.add("ticket", payload.getShort());
			}
			return false;
		}
		if(classId == Exchange_CLASS) {
			if(methodId == 10) { // Declare
				payloadData.add("ticket", payload.getShort());
				payloadData.add("exchange", payload.getShortstr());
				payloadData.add("type", payload.getShortstr());
				payloadData.add("passive", payload.getBit());
				payloadData.add("durable", payload.getBit());
				payloadData.add("autoDelete", payload.getBit());
				payloadData.add("internal", payload.getBit());
				payloadData.add("nowait", payload.getBit());
				return payloadData.add("arguments", readTable(payload));
			}
			if(methodId == 11) { //DeclareOK
				return true;
			}
			if(methodId == 20) { // Delete
				payloadData.add("ticket", payload.getShort());
				payloadData.add("exchange", payload.getShortstr());
				payloadData.add("ifUnused", payload.getBit());
				return payloadData.add("nowait", payload.getBit());
			}
			if(methodId == 21) { // DeleteOk
				return true;
			}
			if(methodId == 30) { // Bind
				payloadData.add("ticket", payload.getShort());
				payloadData.add("destination", payload.getShortstr());
				payloadData.add("source", payload.getShortstr());
				payloadData.add("routingKey", payload.getShortstr());
				payloadData.add("nowait", payload.getBit());
				return payloadData.add("arguments", readTable(payload));
			}
			if(methodId == 31) { // BindOK
				return true;
			}
			if(methodId == 40) { // Unbind
				payloadData.add("ticket", payload.getShort());
				payloadData.add("destination", payload.getShortstr());
				payloadData.add("source", payload.getShortstr());
				payloadData.add("routingKey", payload.getShortstr());
				payloadData.add("nowait", payload.getBit());
				return payloadData.add("arguments", readTable(payload));
			}
			if(methodId == 51) { // UnbindOk
				return true;
			}
			return false;
		}
		return false;
//         case 50:
//             switch (methodId) {
//                 case 10: {
//                     return new Queue.Declare(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Queue.DeclareOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Queue.Bind(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Queue.BindOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 30: {
//                     return new Queue.Purge(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 31: {
//                     return new Queue.PurgeOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 40: {
//                     return new Queue.Delete(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 41: {
//                     return new Queue.DeleteOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 50: {
//                     return new Queue.Unbind(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 51: {
//                     return new Queue.UnbindOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 60:
//             switch (methodId) {
//                 case 10: {
//                     return new Basic.Qos(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Basic.QosOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Basic.Consume(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Basic.ConsumeOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 30: {
//                     return new Basic.Cancel(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 31: {
//                     return new Basic.CancelOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 40: {
//                     return new Basic.Publish(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 50: {
//                     return new Basic.Return(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 60: {
//                     return new Basic.Deliver(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 70: {
//                     return new Basic.Get(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 71: {
//                     return new Basic.GetOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 72: {
//                     return new Basic.GetEmpty(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 80: {
//                     return new Basic.Ack(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 90: {
//                     return new Basic.Reject(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 100: {
//                     return new Basic.RecoverAsync(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 110: {
//                     return new Basic.Recover(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 111: {
//                     return new Basic.RecoverOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 120: {
//                     return new Basic.Nack(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 90:
//             switch (methodId) {
//                 case 10: {
//                     return new Tx.Select(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Tx.SelectOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Tx.Commit(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Tx.CommitOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 30: {
//                     return new Tx.Rollback(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 31: {
//                     return new Tx.RollbackOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 85:
//             switch (methodId) {
//                 case 10: {
//                     return new Confirm.Select(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Confirm.SelectOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
	}

	/**
	 * Reads a table argument from a given stream.
	 */
	private static Map<String, Object> readTable(ByteBuffer in) {
		Map<String, Object> table = new SimpleKeyValueList<String, Object>();
		long endPos = in.getUnsignedInt();
		if (endPos == 0) return table;
		
		endPos += in.position();
		while(in.position() < endPos) {
			String name = in.getShortstr();
			Object value = readFieldValue(in);
			if(!table.containsKey(name)) {
				table.put(name, value);
			}
		}
		return table;
	}
	
	private static Object readFieldValue(ByteBuffer in) {
		byte type = in.getByte();
		if(type == 'S') {
			int len = in.getUnsignedInt();
			if(len < Integer.MAX_VALUE) {
				final byte [] buffer = in.getBytes(new byte[len]);
				return new String(buffer);
			}
			return null;
		}
		if(type == 'I') {
			return in.getInt();
		}
		if(type == 'D') {
			int scale = in.getByte();
			byte[] unscaled = in.getBytes(new byte[4]);
			return new BigDecimal(new BigInteger(unscaled), scale);
		}
		if(type == 'T') {
			return new Date(in.getLong()*1000);
		}
		if(type == 'F') {
			return readTable(in);
		}
		if(type == 'A') {
			return readArray(in);
		}
		if(type == 'b') {
			return in.getByte();
		}
		if(type == 'd') {
			return in.getDouble();
		}
		if(type == 'f') {
			return in.getFloat();
		}
		if(type == 'l') {
			return in.getLong();
		}
		if(type == 's') {
			return in.getShort();
		}
		if(type == 't') {
			return in.getBoolean();
		}
		if(type == 'x') {
			return readBytes(in);
		}
		if(type == 'V') {
			return null;
		}
		return null;
	}

	/** Read a field-array
	 * @param in Buffer for reading
	 * @return The new ArrayList 
	 */
	private static SimpleList<Object> readArray(ByteBuffer in) {
		SimpleList<Object> array = new SimpleList<Object>();
//			long length =  & INT_MASK;
		in.getInt();
		while(in.remaining() > 0) {
			Object value = readFieldValue(in);
			array.add(value);
		}
		return array;
	}
	
	/** Convenience method - reads a 32-bit-length-prefix
	 * byte vector from a DataInputStream.
	 * @param in Buffer for reading
	 * @return the readed bytes
	 */
	private static byte[] readBytes(ByteBuffer in) {
		final long contentLength = in.getUnsignedInt();
		if(contentLength < Integer.MAX_VALUE) {
			final byte [] buffer = in.getBytes(new byte[(int)contentLength]);
			return buffer;
		}			
		return null;
	}

	/**
	 * @return the payloadData
	 */
	public SimpleKeyValueList<String, Object> getPayloadData() {
		return payloadData;
	}
}
