package de.uniks.networkparser.ext.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.ext.mqtt.MqttException;

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

	protected static final String STRING_ENCODING = "UTF-8";

	private byte type;
	protected int msgId;

	protected boolean duplicate = false;

	// Sub Variable
	public static final String KEY_CONNACK    = "Con";
	public static final String KEY_DISCONNECT = "Disc";
	public static final String KEY_PING = "Ping";
	public static final String KEY_CONNECT = "Con";

	protected int code;
	protected boolean session;
	protected int[] data;
	protected String[] names;
	protected Message message;
	protected int keepAliveInterval;

	public byte[] getHeader() throws MqttException {
		try {
			int first = ((getType() & 0x0f) << 4) ^ (getMessageInfo() & 0x0f);
			byte[] varHeader = getVariableHeader();
			int remLen = varHeader.length + getPayload().length;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeByte(first);
			dos.write(encodeMBI(remLen));
			dos.write(varHeader);
			dos.flush();
			return baos.toByteArray();
		} catch(IOException ioe) {
			throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ioe);
		}
	}
	
	/** @return the type of the message. */
	public byte getType() {
		return type;
	}

	protected byte[] encodeMessageId() throws MqttException {
		ByteBuffer buffer=new ByteBuffer();
		short id = (short) msgId;
		buffer.insert(id, false);
		return buffer.array();
	}

	protected byte[] getVariableHeader() throws MqttException {
		if(type == MESSAGE_TYPE_PUBACK) {
			return encodeMessageId();
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
	 * @throws MqttException if an exception occurs whilst getting the payload
	 */
	public byte[] getPayload() throws MqttException {
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

	protected static byte[] encodeMBI( long number) {
		int numBytes = 0;
		long no = number;
		ByteBuffer bos = new ByteBuffer();
		// Encode the remaining length fields in the four bytes
		do {
			byte digit = (byte)(no % 128);
			no = no / 128;
			if (no > 0) {
				digit |= 0x80;
			}
			bos.add(digit);
			numBytes++;
		} while ( (no > 0) && (numBytes<4) );
		bos.flip(true);
		return bos.getBytes();
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
}
