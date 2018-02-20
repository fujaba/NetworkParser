/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corp.
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
 *   Ian Craggs - MQTT 3.1.1 support
 *   Ian Craggs - fix bug 469527
 *   James Sutton - Automatic Reconnect & Offline Buffering
 */
package de.uniks.networkparser.ext.mqtt.internal;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyMQTT;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * <p>
 * This class handles the connection of the AsyncClient to one of the available
 * URLs.
 * </p>
 * <p>
 * The URLs are supplied as either the singleton when the client is created, or
 * as a list in the connect options.
 * </p>
 * <p>
 * This class uses its own onSuccess and onFailure callbacks in preference to
 * the user supplied callbacks.
 * </p>
 * <p>
 * An attempt is made to connect to each URL in the list until either a
 * connection attempt succeeds or all the URLs have been tried
 * </p>
 * <p>
 * If a connection succeeds then the users token is notified and the users
 * onSuccess callback is called.
 * </p>
 * <p>
 * If a connection fails then another URL in the list is attempted, otherwise
 * the users token is notified and the users onFailure callback is called
 * </p>
 * @author Paho Client
 */
public class ConnectActionListener {

	private SimpleKeyValueList<String, MqttWireMessage> persistence;
	private NodeProxyMQTT client;
	private ClientComms comms;
	private Token userToken;
	private SimpleEventCondition mqttCallback;
	private boolean reconnect;

	/**
	 * @param persistence   The MqttClientPersistence layer
	 * @param client        the {@link NodeProxyMQTT}
	 * @param comms         {@link ClientComms}
	 * @param userToken     the {@link Token}
	 * @param reconnect     If true, this is a reconnect attempt
	 */
	public ConnectActionListener(NodeProxyMQTT client, SimpleKeyValueList<String, MqttWireMessage> persistence,
			ClientComms comms, Token userToken, boolean reconnect) {
		this.persistence = persistence;
		this.client = client;
		this.comms = comms;
		this.userToken = userToken;
		this.reconnect = reconnect;
	}

	/**
	 * If the connect succeeded then call the users onSuccess callback
	 *
	 * @param token the {@link Token} from the successful connection
	 */
	public void onSuccess(Token token) {
		userToken.markComplete(token.getResponse(), null);
		userToken.notifyComplete();
		userToken.setClient(this.client); // fix bug 469527 - maybe should be set elsewhere?

		if (mqttCallback != null) {
			String serverURI = comms.getNetworkModules().getServerURI();

			SimpleEvent event = new SimpleEvent(client, serverURI, null, reconnect).withType(NodeProxyMQTT.EVENT_CONNECT);
			mqttCallback.update(event);
		}

	}

	/**
	 * The connect failed, so try the next URI on the list. If there are no more
	 * URIs, then fail the overall connect.
	 *
	 * @param token the {@link Token} from the failed connection attempt
	 * @param exception the {@link Throwable} exception from the failed connection attempt
	 */
	public void onFailure(Token token, Throwable exception) {

		MqttException ex;
		if (exception instanceof MqttException) {
			ex = (MqttException) exception;
		} else {
			ex = MqttException.withReason(MqttException.REASON_CODE_DEFAULT, exception);
		}
		userToken.markComplete(null, ex);
		userToken.notifyComplete();
		userToken.setClient(this.client); // fix bug 469527 - maybe should be set elsewhere?
	}

	/**
	 * Start the connect processing
	 *
	 * @throws MqttException  if an error is thrown whilst setting up persistence
	 */
	public void connect() throws MqttException {
		Token token = new Token(client.getClientId());
		token.setActionCallback(this);
		if (client.isCleanSession()) {
			persistence.clear();
		}

		if (client.getMqttVersion() == NodeProxyMQTT.MQTT_VERSION_DEFAULT) {
			client.withMqttVersion(NodeProxyMQTT.MQTT_VERSION_3_1_1);
		}

		try {
			comms.connect(token);
		} catch (MqttException e) {
			onFailure(token, e);
		}
	}

	/**
	 * Set the MqttCallbackExtened callback to receive connectComplete callbacks
	 *
	 * @param mqttCallbackExtended the {@link SimpleEventCondition} to be called when the connection completes
	 */
	public void setMqttCallback(SimpleEventCondition mqttCallbackExtended) {
		this.mqttCallback = mqttCallbackExtended;
	}

}
