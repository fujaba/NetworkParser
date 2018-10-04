package de.uniks.networkparser.ext.io;

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
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroker;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class RabbitMessage {
	public static final byte NULL = 0;

	public static final byte FRAME_METHOD = 1;
	public static final byte FRAME_HEADER = 2;
	public static final byte FRAME_BODY = 3;
	public static final byte FRAME_HEARTBEAT = 8;

	public static final short CONNECTION_CLASS = 10;
	public static final short CHANNEL_CLASS = 20;
	public static final short ACCESS_CLASS = 30;
	public static final short EXCHANGE_CLASS = 40;
	private static final short QUEUE_CLASS = 50;
	private static final short BASIC_CLASS = 60;
	private static final short CONFIRM_CLASS = 85;
	private static final short TX_CLASS = 90;

	public static final short STARTOK_METHOD = 11;
	public static final short TUNE_METHOD = 30;
	public static final short TUNEOK_METHOD = 31;
	public static final short OPEN_METHOD = 40;
	public static final short OPENCHANNEL_METHOD = 10;
	public static final short PUBLISH_METHOD = 40;
	public static final short CREATE_QUEUE_METHOD = 10;
	public static final short CONSUME_METHOD = 20;

	public static final byte BIT = 1;
	public static final byte BYTE = 2;
	public static final byte SHORT = 3;
	public static final byte INT = 4;
	public static final byte LONG = 5;
	public static final byte SHORTSTR = 6;
	public static final byte STRING = 7;
	public static final byte VERSION = 8;
	public static final byte TABLE = 9;

	private byte[] headers = new byte[3];
	private static final byte FRAME_END = -50;

	private ByteBuffer payload; // FOR INPUT BUFFER
	private ByteBuffer accumulator;
	private Map<String, Object> table;
	private short classId;
	private short methodId;
	private SimpleKeyValueList<String, Object> payloadData = new SimpleKeyValueList<String, Object>();

	// with values or ByteEntity
	public RabbitMessage withShortString(String value) {
		withValues(ByteEntity.create(ByteTokener.DATATYPE_STRING + ByteTokener.LEN_LITTLE, value));
		return this;
	}

	public RabbitMessage withValues(Object... args) {
		if (accumulator == null) {
			accumulator = new ByteBuffer();
			if (getType() != 3) {
				accumulator.insert(new byte[4], true);
			}
			// Only for
			if (table != null || (CONNECTION_CLASS == this.classId && STARTOK_METHOD == this.methodId)) {
				this.writeMap(this.table);
			}
		}
		if (args != null) {
			for (Object item : args) {
				if (item instanceof Boolean) {
					withBit((Boolean) item);
				} else {
					writeValue(item);
				}
			}
		}
		return this;
	}

	public RabbitMessage withEmptyValues() {
		if (accumulator == null) {
			accumulator = new ByteBuffer();
			accumulator.insert(new byte[4], true);
		}
		writeValue(null);
		return this;
	}

	public RabbitMessage withMap(Map<String, Object> map) {
		this.table = map;
		return this;
	}

	public RabbitMessage writeMap(Map<?, ?> map) {
		if (map == null) {
			accumulator.insert(NULL, true);
		} else {
			Set<?> keySet = map.keySet();
			for (Object key : keySet) {
				if (key instanceof String) {
					byte[] keyStr = ((String) key).getBytes();
					accumulator.insert((byte) keyStr.length, true);
					accumulator.insert(keyStr, true);
					writeFieldValue(map.get(key));
				}
			}
		}
		return this;
	}

	public boolean writeValue(Object value) {
		if (value == null) {
			accumulator.insert(NULL, true);
			return true;
		}
		if (value instanceof ByteEntity) {
			ByteEntity entity = (ByteEntity) value;
			byte type = entity.getType();
			byte group = EntityUtil.getGroup(type);
			byte subgroup = EntityUtil.getSubGroup(type);
			if (group == ByteTokener.DATATYPE_STRING) {
				byte[] bytes = entity.getValue();
				if (subgroup == ByteTokener.LEN_LITTLE) {
					accumulator.insert((byte) bytes.length, true);
				} else {
					accumulator.insert((Integer) bytes.length, true);
				}
				accumulator.insert(bytes, true);
			}
		}
		if (value instanceof String) {
			byte[] bytes = ((String) value).getBytes();
			accumulator.insert((Integer) bytes.length, true);
			accumulator.insert(bytes, true);
			return true;
		}
		if (value instanceof Integer || value instanceof Byte || value instanceof Double || value instanceof Float
				|| value instanceof Long || value instanceof Short || value instanceof Boolean) {
			accumulator.insert(value, true);
			return true;
		}

		if (value instanceof Date) {
			Date date = (Date) value;
			accumulator.insert((long) date.getTime() / 1000, true);
			return true;
		}

		if (value instanceof Map<?, ?>) {
			writeMap((Map<?, ?>) value);
			return true;
		}
		if (value instanceof byte[]) {
			byte[] array = (byte[]) value;
			accumulator.insert((Integer) array.length, true);
			accumulator.insert(array, true);
			return true;
		}
		if (value instanceof List<?> || value instanceof Object[]) {
			int newPos, pos = accumulator.position();
			accumulator.insert((Integer) 1, true);
			if (value instanceof List<?>) {
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
		if (value == null) {
			accumulator.insert('V', true);
			return true;
		}
		if (value instanceof String) {
			accumulator.insert('S', true);
			byte[] bytes = ((String) value).getBytes();
			accumulator.insert(bytes.length, true);
			accumulator.insert(bytes, true);
			return true;
		}
		if (value instanceof Integer) {
			accumulator.insert('I', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Date) {
			accumulator.insert('T', true);
			Date date = (Date) value;
			accumulator.insert((long) date.getTime() / 1000, true);
			return true;
		}

		if (value instanceof Map<?, ?>) {
			accumulator.insert('F', true);
			writeMap((Map<?, ?>) value);
			return true;
		}
		if (value instanceof Byte) {
			accumulator.insert('b', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Double) {
			accumulator.insert('d', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Float) {
			accumulator.insert('f', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Long) {
			accumulator.insert('l', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Short) {
			accumulator.insert('s', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof Boolean) {
			accumulator.insert('t', true);
			accumulator.insert(value, true);
			return true;
		}
		if (value instanceof byte[]) {
			accumulator.insert('x', true);
			byte[] array = (byte[]) value;
			accumulator.insert((Integer) array.length, true);
			accumulator.insert(array, true);
			return true;
		}
		if (value instanceof List<?> || value instanceof Object[]) {
			accumulator.insert('A', true);
			int newPos, pos = accumulator.position();
			accumulator.insert((Integer) 1, true);
			if (value instanceof List<?>) {
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
		if (stream == null) {
			return false;
		}
		try {
			stream.write(headers);

			if (accumulator != null) {
				int length = accumulator.length();
				if (getType() != 3) {
					accumulator.set(0, (byte) ((this.classId >> 8) & 0xff));
					accumulator.set(1, (byte) (this.classId & 0xff));
					accumulator.set(2, (byte) ((this.methodId >> 8) & 0xff));
					accumulator.set(3, (byte) (this.methodId & 0xff));
					stream.write(ByteTokener.intToByte(length));
				}
				// As
				stream.write(accumulator.array(), 0, length);
			} else if (payload != null) {
				stream.write(payload.length());
				stream.write(payload.array());
			}
			stream.write(FRAME_END);
			stream.flush();
		} catch (Exception e) {
			// Oh Error Write full Message
			e.printStackTrace();
			System.out.println("WRONG MESSGAE: " + getDebugString());
			return false;
		}
		return true;
	}

	public String getDebugString() {
		ByteBuffer errorMessage = new ByteBuffer();
		errorMessage.insert(headers, true);
		if (accumulator != null) {
			int length = accumulator.length();
			if (getType() != 3) {
				accumulator.set(0, (byte) ((this.classId >> 8) & 0xff));
				accumulator.set(1, (byte) (this.classId & 0xff));
				accumulator.set(2, (byte) ((this.methodId >> 8) & 0xff));
				accumulator.set(3, (byte) (this.methodId & 0xff));
				errorMessage.insert(ByteTokener.intToByte(length), true);
			}
			errorMessage.addBytes(accumulator.array(), length, false);
		} else if (payload != null) {
			errorMessage.insert(payload.length(), true);
			errorMessage.insert(payload.array(), true);
		}
		errorMessage.insert(FRAME_END, false);
		errorMessage.flip(false);
		return errorMessage.toArrayString();
	}

	/**
	 * Protected API - Factory method to instantiate a Frame by reading an
	 * AMQP-wire-protocol frame from the given input stream.
	 * 
	 * @param is DataInputStrem for reading
	 *
	 * @return a new RabbitMessage if we read a frame successfully, otherwise null
	 * @throws IOException Error on InputStream
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
		RabbitMessage msg = new RabbitMessage();
		msg.withType(type);
		msg.withChannel(channel);
		msg.withPayload(payload);
		return msg;
	}

	public RabbitMessage withPayload(byte[] value) {
		this.payload = new ByteBuffer().with(value).flip(true);
		return this;
	}

	// 1=Bit, 2, Byte, 3=Short, 4=Int, 5=ShortString, 6=String, 7 = Version, 8 =
	// Table
	private void initValues(NodeProxyBroker broker) {
		Object[][] data = new Object[][] {
				new Object[] { CONNECTION_CLASS, 10, "version", VERSION, "properties", TABLE, "mechanisms", STRING,
						"locales", STRING }, // START
				new Object[] { CONNECTION_CLASS, STARTOK_METHOD, "clientProperties", TABLE, "mechanisms", SHORTSTR,
						"response", STRING, "locale", SHORTSTR }, // StartOk
				new Object[] { CONNECTION_CLASS, 20, "challenge", STRING }, // Secure
				new Object[] { CONNECTION_CLASS, 21, "challenge", STRING }, // SecureOk
				new Object[] { CONNECTION_CLASS, TUNE_METHOD, "channelMax", SHORT, "frameMax", INT, "heartbeat",
						SHORT }, // Tune
				new Object[] { CONNECTION_CLASS, TUNEOK_METHOD, "channelMax", SHORT, "frameMax", INT, "heartbeat",
						SHORT }, // TuneOK
				new Object[] { CONNECTION_CLASS, OPEN_METHOD, "outOfBand", SHORTSTR }, // Open
				new Object[] { CONNECTION_CLASS, 41, "outOfBand", STRING }, // Open
				new Object[] { CONNECTION_CLASS, 50, "replyCode", SHORT, "replyText", SHORTSTR, "classId", SHORT,
						"methodId", SHORT }, // Close
				new Object[] { CONNECTION_CLASS, 51 }, // CloseOK
				new Object[] { CONNECTION_CLASS, 60, "reason", SHORTSTR }, // Blocked
				new Object[] { CONNECTION_CLASS, 61 }, // Unblocked
				new Object[] { CHANNEL_CLASS, 10, "outOfBand", SHORTSTR }, // Open
				new Object[] { CHANNEL_CLASS, 11, "outOfBand", STRING }, // OpenOK
				new Object[] { CHANNEL_CLASS, 20, "active", BIT }, // Flow
				new Object[] { CHANNEL_CLASS, 21, "active", BIT }, // FlowOK
				new Object[] { CHANNEL_CLASS, 40, "replyCode", SHORT, "replyText", SHORTSTR, "classId", SHORT,
						"methodId", SHORT }, // Close
				new Object[] { CHANNEL_CLASS, 41 }, // CloseOK
				new Object[] { ACCESS_CLASS, 10, "realm", SHORTSTR, "exclusive", BIT, "passive", BIT, "active", BIT,
						"write", BIT, "read", BIT }, // Request
				new Object[] { ACCESS_CLASS, 11, "ticket", SHORT }, // RequestOK
				new Object[] { EXCHANGE_CLASS, 10, "ticket", SHORT, "exchange", SHORTSTR, "type", SHORTSTR, "passive",
						BIT, "durable", BIT, "autoDelete", BIT, "internal", BIT, "nowait", BIT, "arguments", TABLE }, // Declare
				new Object[] { EXCHANGE_CLASS, 11 }, // DeclareOK
				new Object[] { EXCHANGE_CLASS, 20, "ticket", SHORT, "exchange", SHORTSTR, "ifUnused", BIT, "nowait",
						BIT }, // Delete
				new Object[] { EXCHANGE_CLASS, 21 }, // DeleteOk
				new Object[] { EXCHANGE_CLASS, 30, "ticket", SHORT, "destination", SHORTSTR, "source", SHORTSTR,
						"routingKey", SHORTSTR, "nowait", BIT, "arguments", TABLE }, // Bind
				new Object[] { EXCHANGE_CLASS, 31 }, // BindOK
				new Object[] { EXCHANGE_CLASS, 40, "ticket", SHORT, "destination", SHORTSTR, "source", SHORTSTR,
						"routingKey", SHORTSTR, "nowait", BIT, "arguments", TABLE }, // Unbind
				new Object[] { EXCHANGE_CLASS, 51 }, // UnbindOk
				new Object[] { QUEUE_CLASS, CREATE_QUEUE_METHOD, "ticket", SHORT, "queue", SHORTSTR, "passive", BIT,
						"durable", BIT, "exclusive", BIT, "autoDelete", BIT, "nowait", BIT, "arguments", TABLE }, // Declare
				new Object[] { QUEUE_CLASS, 11, "queue", SHORTSTR, "messageCount", INT, "consumerCount", INT }, // DeclareOk
				new Object[] { QUEUE_CLASS, 20, "ticket", SHORT, "queue", SHORTSTR, "exchange", SHORTSTR, "routingKey",
						SHORTSTR, "nowait", BIT, "arguments", TABLE }, // Bind
				new Object[] { QUEUE_CLASS, 21 }, // BindOk
				new Object[] { QUEUE_CLASS, 30, "ticket", SHORT, "queue", SHORTSTR, "nowait", BIT }, // Purge
				new Object[] { QUEUE_CLASS, 31, "messageCount", INT }, // PurgeOk
				new Object[] { QUEUE_CLASS, 40, "ticket", SHORT, "queue", SHORTSTR, "ifUnused", BIT, "ifEmpty", BIT,
						"nowait", BIT }, // Delete
				new Object[] { QUEUE_CLASS, 41, "messageCount", INT }, // DeleteOk
				new Object[] { QUEUE_CLASS, 50, "ticket", SHORT, "queue", SHORTSTR, "exchange", SHORTSTR, "routingKey",
						SHORTSTR, "arguments", TABLE }, // Unbind
				new Object[] { QUEUE_CLASS, 51 }, // UnbindOk
				new Object[] { BASIC_CLASS, 10, "prefetchSize", INT, "prefetchCount", SHORT, "global", BIT }, // Qos
				new Object[] { BASIC_CLASS, 11 }, // QosOk
				new Object[] { BASIC_CLASS, CONSUME_METHOD, "ticket", SHORT, "queue", SHORTSTR, "consumerTag", SHORTSTR,
						"noLocal", BIT, "noAck", BIT, "exclusive", BIT, "nowait", BIT, "arguments", TABLE }, // Consume
				new Object[] { BASIC_CLASS, 21, "consumerTag", SHORTSTR }, // ConsumeOk
				new Object[] { BASIC_CLASS, 30, "consumerTag", SHORTSTR, "nowait", BIT }, // Cancel
				new Object[] { BASIC_CLASS, 31, "consumerTag", SHORTSTR }, // CancelOk
				new Object[] { BASIC_CLASS, PUBLISH_METHOD, "ticket", SHORT, "exchange", SHORTSTR, "routingKey",
						SHORTSTR, "mandatory", BIT, "immediate", BIT }, // Publish
				new Object[] { BASIC_CLASS, 50, "replyCode", SHORT, "replyText", SHORTSTR, "exchange", SHORTSTR,
						"routingKey", SHORTSTR }, // Return
				new Object[] { BASIC_CLASS, 60, "consumerTag", SHORTSTR, "deliveryTag", LONG, "redelivered", BIT,
						"exchange", SHORTSTR, "routingKey", SHORTSTR }, // Deliver
				new Object[] { BASIC_CLASS, 70, "ticket", SHORT, "queue", SHORTSTR, "noAck", BIT }, // Get
				new Object[] { BASIC_CLASS, 71, "deliveryTag", LONG, "redelivered", BIT, "exchange", SHORTSTR,
						"routingKey", SHORTSTR, "messageCount", INT }, // GetOk
				new Object[] { BASIC_CLASS, 72, "clusterId", SHORTSTR }, // GetEmpty
				new Object[] { BASIC_CLASS, 80, "deliveryTag", LONG, "multiple", BIT }, // Ack
				new Object[] { BASIC_CLASS, 90, "deliveryTag", LONG, "requeue", BIT }, // Reject
				new Object[] { BASIC_CLASS, 100, "requeue", BIT }, // RecoverAsync
				new Object[] { BASIC_CLASS, 110, "requeue", BIT }, // Recover
				new Object[] { BASIC_CLASS, 111 }, // RecoverOk
				new Object[] { BASIC_CLASS, 120, "deliveryTag", LONG, "multiple", BIT, "requeue", BIT }, // Nack
				new Object[] { CONFIRM_CLASS, 10, "nowait", BIT }, // Select
				new Object[] { CONFIRM_CLASS, 11 }, // SelectOk
				new Object[] { TX_CLASS, 10 }, // Select
				new Object[] { TX_CLASS, 11 }, // SelectOk
				new Object[] { TX_CLASS, 20 }, // Commit
				new Object[] { TX_CLASS, 21 }, // CommitOk
				new Object[] { TX_CLASS, 30 }, // Rollback
				new Object[] { TX_CLASS, 31 } // RollbackOk
		};
		SimpleKeyValueList<Short, SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>> values = broker
				.getGrammar(true);
		for (Object[] items : data) {
			SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>> group = values.get(items[0]);
			if (group == null) {
				group = new SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>>();
				values.put((Short) items[0], group);
			}
			SimpleKeyValueList<String, Byte> msgValues = new SimpleKeyValueList<String, Byte>();
			for (int i = 2; i < items.length; i += 2) {
				msgValues.put((String) items[i], (Byte) items[i + 1]);
			}
			if (items[1] instanceof Short) {
				group.put((Short) items[1], msgValues);
			} else {
				int type = (Integer) items[1];
				group.put((short) type, msgValues);
			}
		}
	}

	public boolean analysePayLoad(NodeProxyBroker broker) {
		classId = payload.getShort();
		if (broker.getGrammar(false) == null) {
			initValues(broker);
		}
		SimpleKeyValueList<Short, SimpleKeyValueList<String, Byte>> group = broker.getGrammar(false)
				.get((Short) classId);
		if (group != null) {
			methodId = payload.getShort();
			SimpleKeyValueList<String, Byte> values = group.get((Short) methodId);
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					String name = values.getKeyByIndex(i);
					Byte type = values.getValueByIndex(i);
					if (type == BIT) {
						payloadData.add(name, payload.getBit());
					}
					if (type == BYTE) {
						payloadData.add(name, payload.getByte());
					}
					if (type == SHORT) {
						payloadData.add(name, payload.getShort());
					}
					if (type == INT) {
						payloadData.add(name, payload.getInt());
					}
					if (type == LONG) {
						payloadData.add(name, payload.getLong());
					}
					if (type == SHORTSTR) {
						payloadData.add(name, payload.getShortstr());
					}
					if (type == STRING) {
						payloadData.add(name, new String(readBytes(payload)));
					}
					if (type == VERSION) {
						payloadData.add(name, payload.getByte() + "." + payload.getByte());
					}
					if (type == TABLE) {
						payloadData.add(name, readTable(payload));
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Reads a table argument from a given stream.
	 */
	private static Map<String, Object> readTable(ByteBuffer in) {
		Map<String, Object> table = new SimpleKeyValueList<String, Object>();
		long endPos = in.getUnsignedInt();
		if (endPos == 0)
			return table;

		endPos += in.position();
		while (in.position() < endPos) {
			String name = in.getShortstr();
			Object value = readFieldValue(in);
			if (!table.containsKey(name)) {
				table.put(name, value);
			}
		}
		return table;
	}

	private static Object readFieldValue(ByteBuffer in) {
		byte type = in.getByte();
		if (type == 'S') {
			int len = in.getUnsignedInt();
			if (len < Integer.MAX_VALUE) {
				final byte[] buffer = in.getBytes(new byte[len]);
				return new String(buffer);
			}
			return null;
		}
		if (type == 'I') {
			return in.getInt();
		}
		if (type == 'D') {
			int scale = in.getByte();
			byte[] unscaled = in.getBytes(new byte[4]);
			return new BigDecimal(new BigInteger(unscaled), scale);
		}
		if (type == 'T') {
			return new Date(in.getInt() * 1000);
		}
		if (type == 'F') {
			return readTable(in);
		}
		if (type == 'A') {
			return readArray(in);
		}
		if (type == 'b') {
			return in.getByte();
		}
		if (type == 'd') {
			return in.getDouble();
		}
		if (type == 'f') {
			return in.getFloat();
		}
		if (type == 'l') {
			return in.getInt();
		}
		if (type == 's') {
			return in.getShort();
		}
		if (type == 't') {
			return in.getBoolean();
		}
		if (type == 'x') {
			return readBytes(in);
		}
		if (type == 'V') {
			return null;
		}
		return null;
	}

	/**
	 * Read a field-array
	 * 
	 * @param in Buffer for reading
	 * @return The new ArrayList
	 */
	private static SimpleList<Object> readArray(ByteBuffer in) {
		SimpleList<Object> array = new SimpleList<Object>();
//			long length =  & INT_MASK;
		in.getInt();
		while (in.remaining() > 0) {
			Object value = readFieldValue(in);
			array.add(value);
		}
		return array;
	}

	/**
	 * Convenience method - reads a 32-bit-length-prefix byte vector from a
	 * DataInputStream.
	 * 
	 * @param in Buffer for reading
	 * @return the readed bytes
	 */
	private static byte[] readBytes(ByteBuffer in) {
		final long contentLength = in.getUnsignedInt();
		if (contentLength < Integer.MAX_VALUE) {
			final byte[] buffer = in.getBytes(new byte[(int) contentLength]);
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

	public boolean hasData(String key) {
		if (payloadData != null) {
			return payloadData.containsKey(key);
		}
		return false;
	}

	public Object getData(String key) {
		if (payloadData != null) {
			return payloadData.getValue(key);
		}
		return null;
	}

	public String getText() {
		if (getType() != 3) {
			return null;
		}
		String text = new String(payload.array());
		return text;
	}

	public static RabbitMessage createStartOK(String... login) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(CONNECTION_CLASS, STARTOK_METHOD);
		String userStr = "guest", passwordStr = "guest";
		if (login.length > 0) {
			userStr = login[0];
			if (login.length > 1) {
				passwordStr = login[1];
			}
		}
//		SimpleList<String> list = new SimpleList<String>();
		CharacterBuffer sb = new CharacterBuffer();
		sb.with((char) 0);
		sb.with(userStr);
		sb.with((char) 0);
		sb.with(passwordStr);
		ByteEntity entity = ByteEntity.create(ByteTokener.DATATYPE_STRING + ByteTokener.LEN_LITTLE, "en_US");
		msg.withValues("PLAIN", sb.toString(), entity);
		return msg;
	}

	public static RabbitMessage createTuneOK(short channelMax, int frameMax, short heartbeat) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(CONNECTION_CLASS, TUNEOK_METHOD);
		msg.withValues(channelMax, frameMax, heartbeat);
		return msg;
	}

	public static RabbitMessage createConnectionOpen(String virtualHost) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(CONNECTION_CLASS, OPEN_METHOD);
		if (virtualHost == null) {
			virtualHost = "/";
		}
		msg.withShortString(virtualHost);
		msg.withShortString("");
		msg.withValues(false);
		return msg;
	}

	public static RabbitMessage createChannelOpen(NodeProxyBroker broker, String queue) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(CHANNEL_CLASS, OPENCHANNEL_METHOD);

		SimpleKeyValueList<String, String> topics = broker.getTopics();
		short no = (short) (topics.size() + 1);
		topics.add(queue, "" + no);
		msg.withChannel(no);
		msg.withEmptyValues();
		return msg;
	}

	/**
	 * Declare a queue
	 * 
	 * @param channel    Tthe Channelname
	 * @param queue      the name of the queue
	 * @param durable    true if we are declaring a durable queue (the queue will
	 *                   survive a server restart)
	 * @param exclusive  true if we are declaring an exclusive queue (restricted to
	 *                   this connection)
	 * @param autoDelete true if we are declaring an autodelete queue (server will
	 *                   delete it when no longer in use)
	 * @param table      other properties (construction arguments) for the queue
	 * @return a new Message
	 */
	public static RabbitMessage createQueue(short channel, String queue, boolean durable, boolean exclusive,
			boolean autoDelete, Map<String, Object> table) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(QUEUE_CLASS, CREATE_QUEUE_METHOD).withChannel(channel);
		short ticket = 0;
		msg.withValues(ticket);
		msg.withShortString(queue);
		boolean nowait = false;
		msg.withValues(durable, exclusive, autoDelete, nowait, table);
		return msg;
	}

	public static RabbitMessage createConsume(short channel, String queue, String consumerTag, boolean noLocal,
			boolean noAck, boolean exclusive, boolean nowait, Map<String, Object> table) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(BASIC_CLASS, CONSUME_METHOD).withChannel(channel);
		short ticket = 0;
		msg.withValues(ticket);
		msg.withShortString(queue);
		msg.withShortString(consumerTag);
		msg.withValues(noLocal, noAck, exclusive, nowait, table);
		return msg;
	}

	public static RabbitMessage createPublish(short channel, String queue, String routingKey, byte[] body) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(BASIC_CLASS, PUBLISH_METHOD).withChannel(channel);
		short ticket = 0;
		msg.withValues(ticket);
		msg.withShortString(queue);
		msg.withShortString(routingKey);
		msg.withValues(false);
//		msg.withValues(body);
		return msg;
	}

	public static RabbitMessage createExange(short channel, String exchange, String type) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(EXCHANGE_CLASS, OPENCHANNEL_METHOD).withChannel(channel);
		short ticket = 0;
		if (type == null) {
			type = "fanout";
		}
//		 DIRECT("direct"), FANOUT("fanout"), TOPIC("topic"), HEADERS("headers");
		msg.withValues(ticket);
		msg.withShortString(exchange);
		msg.withShortString(type);
		msg.withValues(false); // passive
		msg.withValues(false); // durable
		msg.withValues(false); // autoDelete
		msg.withValues(false); // internal
		msg.withValues(false); // nowait
//		msg.writeValue(null); // arguments
		return msg;
	}

	public static RabbitMessage createBind(short channel, String exchange, String queue) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_METHOD);
		msg.withFrame(QUEUE_CLASS, CONSUME_METHOD).withChannel(channel);
		short ticket = 0;
		msg.withValues(ticket);
		msg.withShortString(queue);
		msg.withShortString(exchange);
		msg.withShortString(""); // routingKey
		msg.withValues(false); // nowait
		msg.writeValue(null); // arguments
		return msg;
	}

	public static RabbitMessage createPublishHeader(short channel, String queue) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_HEADER);
		msg.withFrame(BASIC_CLASS, (short) 0).withChannel(channel);
		msg.withValues((long) queue.length());
		msg.withValues(false, false);
		return msg;
	}

	public static RabbitMessage createPublishBody(short channel, String queue) {
		RabbitMessage msg = new RabbitMessage().withType(FRAME_BODY);
		msg.withChannel(channel);
		msg.withValues(queue);
		return msg;
	}

	/**
	 * When encoding one or more bits, records whether a group of bits is waiting to
	 * be written
	 */
	private boolean needBitFlush;
	/** The current group of bits */
	private byte bitAccumulator;
	/** The current position within the group of bits */
	private int bitMask;

	/**
	 * Private API - called when we may be transitioning from encoding a group of
	 * bits to encoding a non-bit value.
	 */
	private final void bitflush() {
		if (needBitFlush) {
			this.withValues(bitAccumulator);
			resetBitAccumulator();
		}
	}

	/** Private API - called to reset the bit group variables. */
	private void resetBitAccumulator() {
		needBitFlush = false;
		bitAccumulator = 0;
		bitMask = 1;
	}

	public RabbitMessage withBit(boolean b) {
		if (bitMask > 0x80) {
			bitflush();
		}
		if (b) {
			bitAccumulator |= bitMask;
		} else {
			// um, don't set the bit.
		}
		bitMask = bitMask << 1;
		needBitFlush = true;
		return this;
	}

	public static RabbitMessage createClose(short channel) {
		RabbitMessage msg = new RabbitMessage();
		msg.withChannel(channel);
		if (channel > 0) {
			msg.withType(FRAME_HEADER);
			msg.withFrame(CHANNEL_CLASS, (short) 20).withChannel(channel);
		} else {
			msg.withType(FRAME_METHOD);
			msg.withFrame(CONNECTION_CLASS, (short) 10);
		}
		msg.withValues((short) 200);
		msg.withShortString("OK");
		msg.withValues((short) 0, (short) 0);
		return msg;
	}
}
