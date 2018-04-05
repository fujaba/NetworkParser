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
import java.net.SocketTimeoutException;
import de.uniks.networkparser.buffer.ByteBuffer;

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
	protected int keepAliveInterval;
	
	// Message Data
	private int messageQOS = 1;
	private byte[] messagePayload;
	private boolean messageRetained = false;

	public MQTTMessage withType(byte type) {
		this.type = type;
		return this;
	}

	public ByteBuffer getHeader() {
		byte first = (byte)(((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f));
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
	
	protected void encodeUTF8(ByteBuffer buffer, String stringToEncode) {
		try {
			byte[] encodedString = stringToEncode.getBytes("UTF-8");
			buffer.insert((short)encodedString.length, false);
			buffer.insert(encodedString, false);
		}catch (Exception e) {
		}
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
				encodeUTF8(buffer, names[0]);
				if (messageQOS > 0) {
					short id = (short) msgId;
					buffer.insert(id, false);
				}
			} else if(type == MESSAGE_TYPE_CONNECT) {
				if (code == 3) {
					encodeUTF8(buffer, "MQIsdp");
				}
				else if (code == 4) {
					encodeUTF8(buffer, "MQTT");
				}
				buffer.insert((byte)code, false);
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
				buffer.insert((byte)connectFlags, false);
				short id = (short) keepAliveInterval;
				buffer.insert(id, false);
			} else {
				short id = (short) msgId;
				buffer.insert(id, false);
			}
			return buffer.array();
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
					encodeUTF8(buffer, names[i]);
					buffer.addBytes((byte)data[i], 1, false);
				}
			}
			if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
				for (int i=0; i<names.length; i++) {
					encodeUTF8(buffer, names[i]);
				}
			}
			if(type == MESSAGE_TYPE_CONNECT) {
				encodeUTF8(buffer, names[0]);
				if (names[1] != null) {
					encodeUTF8(buffer, names[1]);
					if (names[2] != null) {
						encodeUTF8(buffer, names[2]);
					}
				}
			}
			return buffer.array();
		}
		if(type == MESSAGE_TYPE_PUBLISH) {
			return messagePayload;
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
			byte info = (byte) (messageQOS << 1);
			if (messageRetained) {
				info |= 0x01;
			}
			if (duplicate ) {
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
//			message.message = msg;
			message.messageQOS = ((info >> 1) & 0x03);
			if ((info & 0x01) == 0x01) {
				message.messageRetained = true;
			}
			if ((info & 0x08) == 0x08) {
				message.duplicate = true;
			}
			if(variableHeader == null) {
				return message;
			}
			ByteBuffer buffer=new ByteBuffer().with(variableHeader);
			buffer.flip(true);

			message.names = new String[] { message.decodeUTF8(buffer) };
			if (message.messageQOS > 0) {
				message.msgId = buffer.getShort();
			}
			message.messagePayload = buffer.getBytes(new byte[variableHeader.length-buffer.position() - 1]);
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
			for(int i=0;i<message.data.length;i++) {
				message.data[index] = buffer.getByte();
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

	/**
	 * Decodes an MQTT Multi-Byte Integer from the given stream.
	 * @param in the input stream
	 * @return long Value of Read
	 * @throws IOException if an exception occurs when reading the input stream
	 */
	protected static int readMBI(DataInputStream in) throws IOException {
		byte digit;
		int msgLength = 0;
		int multiplier = 1;

		do {
			digit = in.readByte();
			msgLength += ((digit & 0x7F) * multiplier);
			multiplier *= 128;
		} while ((digit & 0x80) != 0);

		return msgLength;
	}
	
	protected static void encodeMBI(ByteBuffer buffer, long number) {
		int numBytes = 0;
		long no = number;
		do {
			byte digit = (byte)(no % 128);
			no = no / 128;
			if (no > 0) {
				digit |= 0x80;
			}
			buffer.insert(digit, false);
			numBytes++;
		} while ( (no > 0) && (numBytes<4) );
	}

	public static MQTTMessage readFrom(DataInputStream in) {
		MQTTMessage message = null;
		try {
			// read header
			byte first = in.readByte();
			byte type = (byte) ((first >>> 4) & 0x0F);
			if ((type < MESSAGE_TYPE_CONNECT) ||
					(type > MESSAGE_TYPE_DISCONNECT)) {
				// Invalid MQTT message type...
				return null;
			}
			int remLen = readMBI(in);
			ByteBuffer buffer = new ByteBuffer();
			buffer.insert(first, false);

			// bit silly, we decode it then encode it
			encodeMBI(buffer, remLen);

			// read remaining packet
			if (remLen >= 0) {
				byte[] packet = new byte[remLen];
//				int count;
				try {
//					count = 
					in.read(packet,0, remLen);
				} catch (SocketTimeoutException e) {
					// remember the packet read so far
				}
				// reset packet parsing state
				remLen = -1;

				byte info = (byte) (first &= 0x0f);
				if(type == MESSAGE_TYPE_CONNACK ||
						type == MESSAGE_TYPE_DISCONNECT ||
						type == MESSAGE_TYPE_PUBACK ||
						type == MESSAGE_TYPE_SUBACK ||
						type == MESSAGE_TYPE_PINGREQ ||
						type == MESSAGE_TYPE_SUBSCRIBE ||
						type == MESSAGE_TYPE_UNSUBSCRIBE ||
						type == MESSAGE_TYPE_CONNECT ||
						type == MESSAGE_TYPE_PUBLISH) {
					return MQTTMessage.create(type, info, packet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// ignore socket read timeout
		}

		return message;
	}
	

	public MQTTMessage withCode(int value) {
		this.code = value;
		return this;
	}

	public MQTTMessage withQOS(int... qos) {
		this.data = qos;
		return this;
	}


	public MQTTMessage withKeepAliveInterval(int value) {
		this.keepAliveInterval = value;
		return this;
	}
	
	public MQTTMessage withSession(boolean value) {
		this.session = value;
		return this;
	}

	public static MQTTMessage createChannelOpen(String topic) {
		MQTTMessage msg = new MQTTMessage().withType(MESSAGE_TYPE_SUBSCRIBE);
		msg.withNames(topic).withQOS(1);
		return msg;
	}

	public MQTTMessage createMessage(String content) {
		this.messagePayload = content.getBytes();
		this.messageQOS = 1;
		return this;
	}

	/**
	 * @return whether or not this message needs to include a message ID.
	 */
	public boolean isMessageIdRequired() {
		if(type == MESSAGE_TYPE_CONNECT) {
			return false;
		}
		if(type == MESSAGE_TYPE_CONNACK) {
			return false;
		}
		if(type == MESSAGE_TYPE_DISCONNECT) {
			return false;
		}
		if(type == MESSAGE_TYPE_PINGREQ) {
			return false;
		}
		// FOR MQTTPUBLISH
		return true;
	}
	/**
	 * @return the MQTT message ID.
	 */
	public int getMessageId() {
		return msgId;
	}

	public int getMessageQOS() {
		return messageQOS;
	}

	public MQTTMessage withMessageId(int value) {
		this.msgId = value;
		return this;
	}

	public String getText() {
		if(type == MESSAGE_TYPE_PUBLISH) {
			return new String(this.messagePayload);
		}
		return null;
	}

	public String[] getNames() {
		return names;
	}
}
