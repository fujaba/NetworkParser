/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */
package de.uniks.networkparser.ext.mqtt.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttMessage;


/**
 * An on-the-wire representation of an MQTT message.
 * @author Paho Client
 */
public class MqttWireMessage {
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

	private static final String PACKET_NAMES[] = { "reserved", "CONNECT", "CONNACK", "PUBLISH",
			"PUBACK", "PUBREC", "PUBREL", "PUBCOMP", "SUBSCRIBE", "SUBACK",
			"UNSUBSCRIBE", "UNSUBACK", "PINGREQ", "PINGRESP", "DISCONNECT" };

	//The type of the message (e.g. CONNECT, PUBLISH, PUBACK)
	private byte type;
	//The MQTT message ID
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

	protected int keepAliveInterval;
	protected MqttMessage message;

	public MqttWireMessage(byte type) {
		this.type = type;
		// Use zero as the default message ID.  Can't use -1, as that is serialized
		// as 65535, which would be a valid ID.
		this.msgId = 0;
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

	/**
	 * Sub-classes should override this method to supply the payload bytes.
	 * @return The payload byte array
	 * @throws MqttException if an exception occurs whilst getting the payload
	 */
	public byte[] getPayload() throws MqttException {
		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE || type == MESSAGE_TYPE_CONNECT) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				if(type == MESSAGE_TYPE_SUBSCRIBE) {
					for (int i=0; i<names.length; i++) {
						encodeUTF8(dos,names[i]);
						dos.writeByte(data[i]);
					}
				}
				if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
					for (int i=0; i<names.length; i++) {
						encodeUTF8(dos,names[i]);
					}
				}
				if(type == MESSAGE_TYPE_CONNECT) {
					encodeUTF8(dos,names[0]);
					if (names[1] != null) {
						encodeUTF8(dos, names[1]);
						if (names[2] != null) {
							encodeUTF8(dos, names[2]);
						}
					}
				}

				dos.flush();
				return baos.toByteArray();
			} catch (IOException ex) {
				throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex);
			}
		}
		if(type == MESSAGE_TYPE_PUBLISH) {
			return message.getPayload();
		}
		return new byte[0];
	}

	/**
	 * @return the type of the message.
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @return the MQTT message ID.
	 */
	public int getMessageId() {
		return msgId;
	}

	/**
	 * @return the MQTT message ID as String.
	 */
	public String getKey() {
		if(type == MESSAGE_TYPE_CONNECT) {
			return KEY_CONNECT;
		}
		if(type==MESSAGE_TYPE_CONNACK) {
			return KEY_CONNACK;
		}
		if(type==MESSAGE_TYPE_DISCONNECT) {
			return KEY_DISCONNECT;
		}
		if(type==MESSAGE_TYPE_PINGREQ) {
			return KEY_PING;
		}
		return ""+msgId;
	}

	/**
	 * Sets the MQTT message ID.
	 * @param msgId the MQTT message ID
	 * @return ThisComponent
	 */
	public MqttWireMessage withMessageId(int msgId) {
		this.msgId = msgId;
		if (message != null) {
			message.setId(msgId);
		}
		return this;
	}

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

	protected byte[] getVariableHeader() throws MqttException {
		if(type == MESSAGE_TYPE_PUBACK) {
			return encodeMessageId();
		}

		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE || type == MESSAGE_TYPE_PUBLISH || type == MESSAGE_TYPE_CONNECT) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);

				if(type == MESSAGE_TYPE_PUBLISH) {
					encodeUTF8(dos, names[0]);
					if (message.getQos() > 0) {
						dos.writeShort(msgId);
					}
				} else if(type == MESSAGE_TYPE_CONNECT) {
					if (code == 3) {
						encodeUTF8(dos,"MQIsdp");
					}
					else if (code == 4) {
						encodeUTF8(dos,"MQTT");
					}
					dos.write(code);

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
					dos.write(connectFlags);
					dos.writeShort(keepAliveInterval);
				} else {
					dos.writeShort(msgId);
				}
				dos.flush();
				return baos.toByteArray();
			} catch (IOException ex) {
			}
			return null;
		}
		// Not needed, as the client never encodes a CONNACK
		return new byte[0];
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
	
	public boolean isCleanSession() {
		return session;
	}

	protected static byte[] encodeMBI( long number) {
		int numBytes = 0;
		long no = number;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// Encode the remaining length fields in the four bytes
		do {
			byte digit = (byte)(no % 128);
			no = no / 128;
			if (no > 0) {
				digit |= 0x80;
			}
			bos.write(digit);
			numBytes++;
		} while ( (no > 0) && (numBytes<4) );

		return bos.toByteArray();
	}

	/**
	 * Decodes an MQTT Multi-Byte Integer from the given stream.
	 * @param in the input stream
	 * @return long Value of Read
	 * @throws IOException if an exception occurs when reading the input stream
	 */
	protected static long readMBI(DataInputStream in) throws IOException {
		byte digit;
		long msgLength = 0;
		int multiplier = 1;

		do {
			digit = in.readByte();
			msgLength += ((digit & 0x7F) * multiplier);
			multiplier *= 128;
		} while ((digit & 0x80) != 0);

		return msgLength;
	}

	protected byte[] encodeMessageId() throws MqttException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(msgId);
			dos.flush();
			return baos.toByteArray();
		}
		catch (IOException ex) {
			throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex);
		}
	}

	public boolean isRetryable() {
		if(type == MESSAGE_TYPE_SUBSCRIBE || type == MESSAGE_TYPE_UNSUBSCRIBE) {
			return true;
		}
		return false;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	/**
	 * Encodes a String given into UTF-8, before writing this to the DataOutputStream the length of the
	 * encoded string is encoded into two bytes and then written to the DataOutputStream. @link{DataOutputStream#writeUFT(String)}
	 * should be no longer used. @link{DataOutputStream#writeUFT(String)} does not correctly encode UTF-16 surrogate characters.
	 *
	 * @param dos The stream to write the encoded UTF-8 String to.
	 * @param stringToEncode The String to be encoded
	 * @throws MqttException Thrown when an error occurs with either the encoding or writing the data to the stream
	 */
	protected void encodeUTF8(DataOutputStream dos, String stringToEncode) throws MqttException
	{
		try {

			byte[] encodedString = stringToEncode.getBytes("UTF-8");
			byte byte1 = (byte) ((encodedString.length >>> 8) & 0xFF);
			byte byte2 =  (byte) ((encodedString.length >>> 0) & 0xFF);


			dos.write(byte1);
			dos.write(byte2);
			dos.write(encodedString);
		}
		catch(UnsupportedEncodingException ex)
		{
			throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex);
		} catch (IOException ex) {
			throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex);
		}
	}

	/**
	 * Decodes a UTF-8 string from the DataInputStream provided. @link(DataInoutStream#readUTF()) should be no longer used, because  @link(DataInoutStream#readUTF())
	 * does not decode UTF-16 surrogate characters correctly.
	 *
	 * @param input The input stream from which to read the encoded string
	 * @return a decoded String from the DataInputStream
	 */
	protected String decodeUTF8(DataInputStream input) {
		int encodedLength;
		try {
			encodedLength = input.readUnsignedShort();

			byte[] encodedString = new byte[encodedLength];
				input.readFully(encodedString);

			return new String(encodedString, "UTF-8");
		} catch (IOException ex) {
		}
		return null;
	}

	public String toStringPublish() {
		// Convert the first few bytes of the payload into a hex string
		StringBuffer hex = new StringBuffer();
		byte[] payload = message.getPayload();
		int limit = Math.min(payload.length, 20);
		for (int i = 0; i < limit; i++) {
			byte b = payload[i];
			String ch = Integer.toHexString(b);
			if (ch.length() == 1) {
				ch = "0" + ch;
			}
			hex.append(ch);
		}

		// It will not always be possible to convert the binary payload into
		// characters, but never-the-less we attempt to do this as it is often
		// useful
		String string = null;
		try {
			string = new String(payload, 0, limit, "UTF-8");
		} catch (Exception e) {
			string = "?";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(super.toString());
		sb.append(" qos:").append(message.getQos());
		if (message.getQos() > 0) {
			sb.append(" msgId:").append(msgId);
		}
		sb.append(" retained:").append(message.isRetained());
		sb.append(" dup:").append(duplicate);
		sb.append(" topic:\"").append(names[0]).append("\"");
		sb.append(" payload:[hex:").append(hex);
		sb.append(" utf8:\"").append(string).append("\"");
		sb.append(" length:").append(payload.length).append("]");

		return sb.toString();
	}

	public String toString() {
		if(type == MESSAGE_TYPE_PUBLISH) {
			return toStringPublish();
		}
		// CONNACK
		CharacterBuffer sb = new CharacterBuffer();
		sb.with(PACKET_NAMES[type]);
		if(type == MESSAGE_TYPE_CONNECT) {
			sb.with(" clientId ", names[0], " keepAliveInterval ", ""+keepAliveInterval);
			return sb.toString();
		}
		if(type == MESSAGE_TYPE_CONNACK) {
			sb.with(" session present:",  ""+session," return code: ", ""+code);
			return sb.toString();
		}
		if(type == MESSAGE_TYPE_SUBSCRIBE) {
			sb.with(" names:[");
			for (int i = 0; i < code; i++) {
				if (i > 0) {
					sb.with(", ");
				}
				sb.with("\"").with(names[i]).with("\"");
			}
			sb.with("] qos:[");
			sb.withCollection(", ", code);
			sb.with("]");
			return sb.toString();
		}
		if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
			sb.with(" names:[");
			for (int i = 0; i < code; i++) {
				if (i > 0) {
					sb.with(", ");
				}
				sb.with("\"" + names[i] + "\"");
			}
			sb.with("]");
			return sb.toString();
		}
		if(type == MESSAGE_TYPE_SUBACK) {
			sb.with(" granted Qos");
			sb.withCollection(" ", data);
			return sb.toString();
		}
		return PACKET_NAMES[type];
	}


	public static final boolean isMQTTAck(MqttWireMessage message) {
		if(message == null) {
			return false;
		}
		byte type = message.getType();
		return type == MESSAGE_TYPE_SUBACK || type == MESSAGE_TYPE_PUBACK || type == MESSAGE_TYPE_CONNACK;
	}

	public int getReturnCode() {
		return code;
	}

	public boolean getSessionPresent() {
		return session;
	}

	public static MqttWireMessage createWireMessage(InputStream inputStream) throws MqttException {
		try {
			MqttInputStream counter = new MqttInputStream(inputStream);
			DataInputStream in = new DataInputStream(counter);
			int first = in.readUnsignedByte();
			byte type = (byte) (first >> 4);
			byte info = (byte) (first &= 0x0f);
			long remLen = readMBI(in);
			long totalToRead = counter.getCounter() + remLen;

			long remainder = totalToRead - counter.getCounter();
			byte[] data = new byte[0];
			// The remaining bytes must be the payload...
			if (remainder > 0) {
				data = new byte[(int) remainder];
				in.readFully(data, 0, data.length);
			}

			if(type == MESSAGE_TYPE_CONNACK ||
					type == MESSAGE_TYPE_DISCONNECT ||
					type == MESSAGE_TYPE_PUBACK ||
					type == MESSAGE_TYPE_SUBACK ||
					type == MESSAGE_TYPE_PINGREQ ||
					type == MESSAGE_TYPE_SUBSCRIBE ||
					type == MESSAGE_TYPE_UNSUBSCRIBE ||
					type == MESSAGE_TYPE_CONNECT ||
					type == MESSAGE_TYPE_PUBLISH) {
				return MqttWireMessage.create(type, info, data);
			}

			throw MqttException.withReason(MqttException.REASON_CODE_UNEXPECTED_ERROR);
		} catch(IOException io) {
			throw MqttException.withReason(MqttException.REASON_CODE_DEFAULT, io);
		}
	}

	public static MqttWireMessage create(byte type) {
		try {
			return create(type, (byte) 0, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MqttWireMessage create(byte type, byte info, byte[] variableHeader) throws IOException {
		MqttWireMessage message = new MqttWireMessage(type);
		if(type == MESSAGE_TYPE_DISCONNECT) {
			return message;
		}

		if(type == MESSAGE_TYPE_PUBLISH) {
			MqttMessage msg = new MqttMessage();
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
			ByteArrayInputStream bais = new ByteArrayInputStream(variableHeader);
			MqttInputStream counter = new MqttInputStream(bais);
			DataInputStream dis = new DataInputStream(counter);
			message.names = new String[] { message.decodeUTF8(dis) };
			if (msg.getQos() > 0) {
				message.msgId = dis.readUnsignedShort();
			}
			byte[] payload = new byte[variableHeader.length-counter.getCounter()];
			dis.readFully(payload);
			dis.close();
			msg.setPayload(payload);
		}
		if(variableHeader == null) {
			return message;
		}

		//		MqttWireMessage.MESSAGE_TYPE_CONNACK
		ByteArrayInputStream bais = new ByteArrayInputStream(variableHeader);
		DataInputStream dis = new DataInputStream(bais);
		if(type == MESSAGE_TYPE_CONNECT) {
			// NEW
			String[] values = new String[3];
			message.names = values;
//			String protocol_name =
			message.decodeUTF8(dis);
//			int protocol_version =
					dis.readByte();
//			byte connect_flags =
					dis.readByte();
			message.keepAliveInterval = dis.readUnsignedShort();
			values[0] = message.decodeUTF8(dis);
			
		}
		if(type == MESSAGE_TYPE_CONNACK) {
			message.session = (dis.readUnsignedByte() & 0x01) == 0x01;
			message.code = dis.readUnsignedByte();
		}
		if(type == MESSAGE_TYPE_PUBACK) {
			message.msgId = dis.readUnsignedShort();
		}

		if(type == MESSAGE_TYPE_SUBACK) {
			message.msgId = dis.readUnsignedShort();
			int index = 0;
			message.data = new int[variableHeader.length-2];
			int qos = dis.read();
			while (qos != -1) {
				message.data[index] = qos;
				index++;
				qos = dis.read();
			}
		}
		if(type == MESSAGE_TYPE_SUBSCRIBE) {
			message.msgId = dis.readUnsignedShort();
			message.code = 0;
			message.names = new String[10];
			message.data = new int[10];
			boolean end = false;
			while (!end) {
				try {
					message.names[message.code] = message.decodeUTF8(dis);
					message.data[message.code++] = dis.readByte();
				} catch (Exception e) {
					end = true;
				}
			}
		}
		if(type == MESSAGE_TYPE_UNSUBSCRIBE) {
			message.msgId = dis.readUnsignedShort();
			message.code = 0;
			message.names = new String[10];
			boolean end = false;
			while (!end) {
				try {
					message.names[message.code] = message.decodeUTF8(dis);
				} catch (Exception e) {
					end = true;
				}
			}
		}
		dis.close();
		return message;
	}

	public MqttWireMessage withNames(String... names) {
		this.names = names;
		this.code = names.length;
		return this;
	}
	public MqttWireMessage withQOS(int[] qos) {
		this.data = qos;
		for (int i=0;i<qos.length;i++) {
			MqttMessage.validateQos(qos[i]);
		}
		return this;
	}


	public int[] getGrantedQos() {
		return data;
	}

	public MqttMessage getMessage() {
		return message;
	}


	public String getTopicName() {
		if(names != null && names.length>0) {
			return names[0];
		}
		return "";
	}

	public MqttWireMessage withMessage(MqttMessage message) {
		this.message = message;
		return this;
	}

	public MqttWireMessage withKeepAliveInterval(int value) {
		this.keepAliveInterval = value;
		return this;
	}
	
	public MqttWireMessage withCode(int value) {
		this.code = value;
		return this;
	}

	public MqttWireMessage withSession(boolean value) {
		this.session = value;
		return this;
	}
}
