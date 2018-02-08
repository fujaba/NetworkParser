/*******************************************************************************
 * Copyright (c) 2009, 2015 IBM Corp.
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
 *    Ian Craggs - ack control (bug 472172)
 */
package de.uniks.networkparser.ext.mqtt.internal;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import de.uniks.networkparser.ext.mqtt.MqttException;



/**
 * An on-the-wire representation of an MQTT PUBACK message.
 */
public class MqttPubAck extends MqttWireMessage {
	public MqttPubAck(byte info, byte[] data) throws IOException {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bais);
		msgId = dis.readUnsignedShort();
		dis.close();
	}
	
	public MqttPubAck(MqttPublish publish) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		msgId = publish.getMessageId();
	}
	
	protected byte getMessageInfo() {
		return 0;
	}
	
	public MqttPubAck(int messageId) {
		super(MqttWireMessage.MESSAGE_TYPE_PUBACK);
		msgId = messageId;
	}
	
	protected byte[] getVariableHeader() throws MqttException {
		return encodeMessageId();
	}
}
