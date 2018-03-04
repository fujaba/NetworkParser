package de.uniks.networkparser.ext.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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

	public static final short START_CLASS=10;
	public static final short TUNE_CLASS=10;
	public static final short START_METHOD=11;
	public static final short TUNE_METHOD=31;
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
		msg.withFrame(START_CLASS, START_METHOD);
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
		msg.withFrame(TUNE_CLASS, TUNE_METHOD);
		
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
	* @return a new Frame if we read a frame successfully, otherwise null
	*/
	public static RabbitMessage readFrom(DataInputStream is) throws IOException {
		byte type;
		short channel;

		try {
			type = (byte) is.readUnsignedByte();
		} catch (SocketTimeoutException ste) {
			return null; // failed
		}
		if (type == 'A') {
			return null;
		}
		channel = (short) is.readUnsignedShort();
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
		this.payload = new ByteBuffer().with(value);
		return this;
	}

	public void analysePayLoad() {
		int classId = payload.getShort();
		int methodId = payload.getShort();
		
		switch (classId) {
			case 10:
				switch (methodId) {
					case 10: {
						payloadData.add("versionMajor", payload.getByte());
						payloadData.add("versionMinor", payload.getByte());
						
//					public Start(int versionMajor, int versionMinor, 
						//Map<String,Object> serverProperties, LongString mechanisms, LongString locales) {
//					this(rdr.readOctet(), rdr.readOctet(), rdr.readTable(), rdr.readLongstr(), rdr.readLongstr());C
//					return new Connection.Start(new MethodArgumentReader(new ValueReader(in)));
					}
				}
//                 case 11: {
//                     return new Connection.StartOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Connection.Secure(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Connection.SecureOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 30: {
//                     return new Connection.Tune(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 31: {
//                     return new Connection.TuneOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 40: {
//                     return new Connection.Open(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 41: {
//                     return new Connection.OpenOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 50: {
//                     return new Connection.Close(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 51: {
//                     return new Connection.CloseOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 60: {
//                     return new Connection.Blocked(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 61: {
//                     return new Connection.Unblocked(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 20:
//             switch (methodId) {
//                 case 10: {
//                     return new Channel.Open(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Channel.OpenOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Channel.Flow(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Channel.FlowOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 40: {
//                     return new Channel.Close(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 41: {
//                     return new Channel.CloseOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 30:
//             switch (methodId) {
//                 case 10: {
//                     return new Access.Request(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Access.RequestOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
//         case 40:
//             switch (methodId) {
//                 case 10: {
//                     return new Exchange.Declare(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 11: {
//                     return new Exchange.DeclareOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 20: {
//                     return new Exchange.Delete(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 21: {
//                     return new Exchange.DeleteOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 30: {
//                     return new Exchange.Bind(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 31: {
//                     return new Exchange.BindOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 40: {
//                     return new Exchange.Unbind(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 case 51: {
//                     return new Exchange.UnbindOk(new MethodArgumentReader(new ValueReader(in)));
//                 }
//                 default: break;
//             } break;
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
//     }
		}
	}

	private static final long INT_MASK = 0xffffffffL;

	/**
	 * Protected API - Cast an int to a long without extending the
	 * sign bit of the int out into the high half of the long.
	 */
	private static long unsignedExtend(int value) {
		long extended = value;
		return extended & INT_MASK;
	}

	/**
	 * Reads a table argument from a given stream.
	 */
	private static Map<String, Object> readTable(DataInputStream in) {
		Map<String, Object> table = new SimpleKeyValueList<String, Object>();
		try {
			long tableLength = unsignedExtend(in.readInt());
			if (tableLength == 0) return table;;

			while(in.available() > 0) {
				String name = readShortstr(in);
				Object value = readFieldValue(in);
				if(!table.containsKey(name)) {
					table.put(name, value);
				}
			}
		}catch (Exception e) {
		}
		return table;
	}
	
	private static Object readFieldValue(DataInputStream in) {
		Object value = null;
		try {
			switch(in.readUnsignedByte()) {
				case 'S':
					value = in.readLong();
					break;
				case 'I':
					value = in.readInt();
					break;
				case 'D':
					int scale = in.readUnsignedByte();
					byte [] unscaled = new byte[4];
					in.readFully(unscaled);
					value = new BigDecimal(new BigInteger(unscaled), scale);
					break;
				case 'T':
					value = new Date(in.readLong()*1000);
					break;
				case 'F':
					value = readTable(in);
					break;
				case 'A':
					value = readArray(in);
					break;
				case 'b':
					value = in.readByte();
					break;
				case 'd':
					value = in.readDouble();
					break;
				case 'f':
					value = in.readFloat();
					break;
				case 'l':
					value = in.readLong();
					break;
				case 's':
					value = in.readShort();
					break;
				case 't':
					value = in.readBoolean();
					break;
				case 'x':
					value = readBytes(in);
					break;
				case 'V':
					value = null;
					break;
				default:
					throw new RuntimeException("Unrecognised type in table");
			}
		}catch (Exception e) {
		}
		return value;
	}

	/** Read a field-array */
	private static List<Object> readArray(DataInputStream in) {
		List<Object> array = new ArrayList<Object>();
		try {
//			long length = 
			unsignedExtend(in.readInt());
			while(in.available() > 0) {
				Object value = readFieldValue(in);
				array.add(value);
			}
		}catch (Exception e) {
		}
		return array;
	}
	
	/** Convenience method - reads a 32-bit-length-prefix
	 * byte vector from a DataInputStream.
	 */
	private static byte[] readBytes(final DataInputStream in) {
		try {
			final long contentLength = unsignedExtend(in.readInt());
			if(contentLength < Integer.MAX_VALUE) {
				final byte [] buffer = new byte[(int)contentLength];
				in.readFully(buffer);
				return buffer;
			}			
		}catch (Exception e) {
		}
		return null;
	}

	/** Convenience method - reads a short string from a DataInput Stream.  */
	private static String readShortstr(DataInputStream in) {
		try {
			byte[] b = new byte[in.readUnsignedByte()];
			in.readFully(b);
			return new String(b, "utf-8");
		} catch (IOException e) {
		}
		return null;
	}
}
