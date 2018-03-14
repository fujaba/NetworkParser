package de.uniks.networkparser.ext.io;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.list.SimpleList;

public class MQTTMessage {
	public static final byte MESSAGE_TYPE_CONNECT = 1;
	public static final byte MESSAGE_TYPE_CONNACK = 2;
	public static final byte MESSAGE_TYPE_PUBLISH = 3;
	public static final byte MESSAGE_TYPE_PUBACK = 4;
	public static final byte MESSAGE_TYPE_PUBREC = 5;
	public static final byte MESSAGE_TYPE_PUBREL = 6;
	public static final byte MESSAGE_TYPE_PUBCOMP = 7;
	public static final byte MESSAGE_TYPE_SUBSCRIBE = 8;
	public static final byte MESSAGE_TYPE_SUBACK = 9;
	public static final byte MESSAGE_TYPE_UNSUBSCRIBE = 10;
	public static final byte MESSAGE_TYPE_UNSUBACK = 11;
	public static final byte MESSAGE_TYPE_PINGREQ = 12;
	public static final byte MESSAGE_TYPE_PINGRESP = 13;
	public static final byte MESSAGE_TYPE_DISCONNECT = 14;

	private byte type;
	protected int msgId;
	protected boolean duplicate = false;

	// Sub Variable
	public static final String KEY_CONNACK    = "Con";
	public static final String KEY_DISCONNECT = "Disc";
	public static final String KEY_PING = "Ping";
	public static final String KEY_CONNECT = "Con";
	/** Mqtt Version 3.1.1 */
	public static final int MQTT_VERSION_3_1_1 = 4;

	protected int code;
	protected boolean session;
	protected int[] data;
	protected String[] names;
	protected Message message;
	protected int keepAliveInterval;
	private SimpleList<Object> values;
	
	public MQTTMessage withType(byte type) {
		this.type = type;
		return this;
	}

	public ByteBuffer getHeader() {
		int first = ((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f);
		byte[] varHeader = getVariableHeader();
		int remLen = varHeader.length + getPayload().length;
		ByteBuffer buffer=new ByteBuffer();

		buffer.insert(first, true);

		int numBytes = 0;
		// Encode the remaining length fields in the four bytes
		do {
			byte digit = (byte)(remLen % 128);
			remLen = remLen / 128;
			if (remLen > 0) {
				digit |= 0x80;
			}
			buffer.insert(digit, false);
			numBytes++;
		} while ( (remLen > 0) && (numBytes<4) );
		
		buffer.insert(varHeader, false);
		return buffer;
	}
	
	/** @return the type of the message. */
	public byte getType() {
		return type;
	}

	protected byte[] getVariableHeader() {
		if(type == MESSAGE_TYPE_PUBACK) {
			ByteBuffer buffer=new ByteBuffer();
			short id = (short) msgId;
			buffer.insert(id, false);
			return buffer.array();
		}

		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE || type == MESSAGE_TYPE_PUBLISH || type == MESSAGE_TYPE_CONNECT) {
			ByteBuffer buffer=new ByteBuffer();
			if(type == MESSAGE_TYPE_PUBLISH) {
				buffer.insert(names[0], false);
				if (message.getQos() > 0) {
					short id = (short) msgId;
					buffer.insert(id, false);
				}
			} else if(type == MESSAGE_TYPE_CONNECT) {
				if (code == 3) {
					buffer.insert("MQIsdp", false);
				}
				else if (code == 4) {
					buffer.insert("MQTT", false);
				}
				buffer.insert(code, false);
				byte connectFlags = 0;
				if (session) {
					connectFlags |= 0x02;
				}

				if (names[1] != null) {
					connectFlags |= 0x80;
					if (names[2] != null) {
						connectFlags |= 0x40;
					}
				}
				buffer.insert(connectFlags, false);
				short id = (short) keepAliveInterval;
				buffer.insert(id, false);
			} else {
				short id = (short) msgId;
				buffer.insert(id, false);
			}
			buffer.flip(true);
			return buffer.getBytes();
		}
		// Not needed, as the client never encodes a CONNACK
		return new byte[0];
	}
	
	/**
	 * Sub-classes should override this method to supply the payload bytes.
	 * @return The payload byte array
	 */
	public byte[] getPayload() {
		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE || type == MESSAGE_TYPE_CONNECT) {
			ByteBuffer buffer = new ByteBuffer();
			if(type == MESSAGE_TYPE_SUBSCRIBE) {
				for (int i=0; i<names.length; i++) {
					buffer.insert(names[i], false);
				}
			}
			if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
				for (int i=0; i<names.length; i++) {
					buffer.insert(names[i], false);
				}
			}
			if(type == MESSAGE_TYPE_CONNECT) {
				buffer.insert(names[0], false);
				if (names[1] != null) {
					buffer.insert(names[1], false);
					if (names[2] != null) {
						buffer.insert(names[2], false);
					}
				}
			}
			buffer.flip(true);
			return buffer.getBytes();
		}
		if(type == MESSAGE_TYPE_PUBLISH) {
			return message.getPayload();
		}
		return new byte[0];
	}

	/**
	 * Sub-classes should override this to encode the message info.
	 * Only the least-significant four bits will be used.
	 * @return The Message information byte
	 */
	protected byte getMessageInfo() {
		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE) {
			return (byte) (2 | (duplicate ? 8 : 0));
		}
		if(type == MESSAGE_TYPE_PUBLISH) {
			byte info = (byte) (message.getQos() << 1);
			if (message.isRetained()) {
				info |= 0x01;
			}
			if (message.isDuplicate() || duplicate ) {
				info |= 0x08;
			}

			return info;
		}
		return 0;
	}
	
	public static MQTTMessage create(byte type) {
		return create(type, (byte) 0, null);
	}
	
	/**
	 * Decodes a UTF-8 string
	 *
	 * @param input The input stream from which to read the encoded string
	 * @return a decoded String from the DataInputStream
	 */
	protected String decodeUTF8(ByteBuffer input) {
		int encodedLength;
		encodedLength = input.getShort();
		byte[] encodedString = input.getBytes(new byte[encodedLength]);
		return new String(encodedString);
	}
	
	public MQTTMessage withNames(String... names) {
		this.names = names;
		this.code = names.length;
		return this;
	}

	public static MQTTMessage create(byte type, byte info, byte[] variableHeader) {
		MQTTMessage message = new MQTTMessage().withType(type);
		if(type == MESSAGE_TYPE_DISCONNECT) {
			return message;
		}
		if(type == MESSAGE_TYPE_PUBLISH) {
			Message msg = new Message();
			message.message = msg;
			msg.setQos((info >> 1) & 0x03);
			if ((info & 0x01) == 0x01) {
				msg.setRetained(true);
			}
			if ((info & 0x08) == 0x08) {
				msg.setDuplicate(true);
			}
			if(variableHeader == null) {
				return message;
			}
			ByteBuffer buffer=new ByteBuffer().with(variableHeader);

			message.names = new String[] { message.decodeUTF8(buffer) };
			if (msg.getQos() > 0) {
				message.msgId = buffer.getShort();
			}
			byte[] payload = buffer.getBytes(new byte[variableHeader.length-buffer.position()]);
			msg.setPayload(payload);
		}
		if(variableHeader == null) {
			return message;
		}
	//		MqttWireMessage.MESSAGE_TYPE_CONNACK
	ByteBuffer buffer=new ByteBuffer().with(variableHeader);
	if(type == MESSAGE_TYPE_CONNECT) {
		// NEW
		String[] values = new String[3];
		message.names = values;
//		String protocol_name =
		message.decodeUTF8(buffer);
//		int protocol_version =
		buffer.getByte();
//		byte connect_flags =
		buffer.getByte();
		message.keepAliveInterval = buffer.getShort();
		values[0] = message.decodeUTF8(buffer);
		
	}
	if(type == MESSAGE_TYPE_CONNACK) {
		message.session = (buffer.getByte() & 0x01) == 0x01;
		message.code = buffer.getByte();
	}
	if(type == MESSAGE_TYPE_PUBACK) {
		message.msgId = buffer.getShort();
	}

	if(type == MESSAGE_TYPE_SUBACK) {
		message.msgId = buffer.getShort();
		int index = 0;
		message.data = new int[variableHeader.length-2];
		int qos = buffer.getByte();
		while (qos != -1) {
			message.data[index] = qos;
			index++;
			qos = buffer.getByte();
		}
	}
	if(type == MESSAGE_TYPE_SUBSCRIBE) {
		message.msgId = buffer.getShort();
		message.code = 0;
		message.names = new String[10];
		message.data = new int[10];
		boolean end = false;
		while (!end) {
			try {
				message.names[message.code] = message.decodeUTF8(buffer);
				message.data[message.code++] = buffer.getByte();
			} catch (Exception e) {
				end = true;
			}
		}
	}
	if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
		message.msgId = buffer.getShort();
		message.code = 0;
		message.names = new String[10];
		boolean end = false;
		while (!end) {
			try {
				message.names[message.code] = message.decodeUTF8(buffer);
			} catch (Exception e) {
				end = true;
			}
		}
	}
	return message;
}

	public MQTTMessage withValues(int keepAlive, int mqttVersion, boolean cleanSession) {
		if(this.values == null) {
			this.values = new SimpleList<Object>();
		}
		this.values.add(keepAlive, mqttVersion, cleanSession);
		return this;
	}
}
