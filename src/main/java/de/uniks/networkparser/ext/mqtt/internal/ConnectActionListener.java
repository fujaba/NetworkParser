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
import de.uniks.networkparser.ext.mqtt.MqttClient;
import de.uniks.networkparser.ext.mqtt.MqttConnectOptions;
import de.uniks.networkparser.ext.mqtt.MqttException;
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
 */
public class ConnectActionListener {

	private SimpleKeyValueList<String, MqttPublish> persistence;
	private MqttClient client;
	private ClientComms comms;
	private MqttConnectOptions options;
	private Token userToken;
	private int originalMqttVersion;
	private SimpleEventCondition mqttCallback;
	private boolean reconnect;

	/**
	 * @param persistence
	 *            The {@link MqttClientPersistence} layer
	 * @param client
	 *            the {@link MqttClient}
	 * @param comms
	 *            {@link ClientComms}
	 * @param options
	 *            the {@link MqttConnectOptions}
	 * @param userToken
	 *            the {@link MqttToken}
	 * @param userContext
	 *            the User Context Object
	 * @param userCallback
	 *            the {@link IMqttActionListener} as the callback for the user
	 * @param reconnect
	 *            If true, this is a reconnect attempt
	 */
	public ConnectActionListener(MqttClient client, SimpleKeyValueList<String, MqttPublish> persistence,
			ClientComms comms, MqttConnectOptions options, Token userToken, boolean reconnect) {
		this.persistence = persistence;
		this.client = client;
		this.comms = comms;
		this.options = options;
		this.userToken = userToken;
		this.originalMqttVersion = options.getMqttVersion();
		this.reconnect = reconnect;
	}

	/**
	 * If the connect succeeded then call the users onSuccess callback
	 * 
	 * @param token
	 *            the {@link IMqttToken} from the successful connection
	 */
	public void onSuccess(Token token) {
		if (originalMqttVersion == MqttConnectOptions.MQTT_VERSION_DEFAULT) {
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
		}
		userToken.markComplete(token.getResponse(), null);
		userToken.notifyComplete();
		userToken.setClient(this.client); // fix bug 469527 - maybe should be set elsewhere?

		if (mqttCallback != null) {
			String serverURI = comms.getNetworkModules()[comms.getNetworkModuleIndex()].getServerURI();

			SimpleEvent event = new SimpleEvent(client, serverURI, null, reconnect).withType(MqttClient.EVENT_CONNECT);
			mqttCallback.update(event);
		}

	}

	/**
	 * The connect failed, so try the next URI on the list. If there are no more
	 * URIs, then fail the overall connect.
	 * 
	 * @param token
	 *            the {@link IMqttToken} from the failed connection attempt
	 * @param exception
	 *            the {@link Throwable} exception from the failed connection attempt
	 */
	public void onFailure(Token token, Throwable exception) {

		int numberOfURIs = comms.getNetworkModules().length;
		int index = comms.getNetworkModuleIndex();

		if ((index + 1) < numberOfURIs || (originalMqttVersion == MqttConnectOptions.MQTT_VERSION_DEFAULT
				&& options.getMqttVersion() == MqttConnectOptions.MQTT_VERSION_3_1_1)) {

			if (originalMqttVersion == MqttConnectOptions.MQTT_VERSION_DEFAULT) {
				if (options.getMqttVersion() == MqttConnectOptions.MQTT_VERSION_3_1_1) {
					options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
				} else {
					options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
					comms.setNetworkModuleIndex(index + 1);
				}
			} else {
				comms.setNetworkModuleIndex(index + 1);
			}
			try {
				connect();
			} catch (MqttException e) {
				onFailure(token, e); // try the next URI in the list
			}
		} else {
			if (originalMqttVersion == MqttConnectOptions.MQTT_VERSION_DEFAULT) {
				options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_DEFAULT);
			}
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
	}

	/**
	 * Start the connect processing
	 * 
	 * @throws MqttPersistenceException
	 *             if an error is thrown whilst setting up persistence
	 */
	public void connect() throws MqttException {
		Token token = new Token(client.getClientId());
		token.setActionCallback(this);
		if (options.isCleanSession()) {
			persistence.clear();
		}

		if (options.getMqttVersion() == MqttConnectOptions.MQTT_VERSION_DEFAULT) {
			options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
		}

		try {
			comms.connect(options, token);
		} catch (MqttException e) {
			onFailure(token, e);
		}
	}

	/**
	 * Set the MqttCallbackExtened callback to receive connectComplete callbacks
	 * 
	 * @param mqttCallbackExtended
	 *            the {@link MqttCallbackExtended} to be called when the connection
	 *            completes
	 */
	public void setMqttCallback(SimpleEventCondition mqttCallbackExtended) {
		this.mqttCallback = mqttCallbackExtended;
	}

}
