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
 *    Dave Locke - initial API and implementation and/or initial documentation
 *    Ian Craggs - per subscription message handlers (bug 466579)
 *    Ian Craggs - ack control (bug 472172)
 *    James Sutton - checkForActivity Token (bug 473928)
 *    James Sutton - Automatic Reconnect & Offline Buffering.
 */
package de.uniks.networkparser.ext.mqtt.internal;

import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttTopic;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyMQTT;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * Handles client communications with the server.  Sends and receives MQTT V3
 * messages.
 * @author Paho Client
 */
public class ClientComms {
	private static final byte CONNECTED	= 0;
	private static final byte CONNECTING	= 1;
	private static final byte DISCONNECTING	= 2;
	private static final byte DISCONNECTED	= 3;
	private static final byte CLOSED	= 4;

	private NodeProxyMQTT 		client;
	private TCPNetworkModule			networkModule;
	private CommsReceiver 			receiver;
	private CommsSender 			sender;
	private CommsCallback 			callback;
	private ClientState	 			clientState;
	private CommsTokenStore 		tokenStore;
	private boolean 				stoppingComms = false;

	private byte	conState = DISCONNECTED;
	private Object	conLock = new Object();  	// Used to synchronize connection state
	private boolean	closePending = false;
	private boolean resting = false;

	private ExecutorService executorService;

	/**
	 * Creates a new ClientComms object, using the specified module to handle
	 * the network calls.
	 * @param client The {@link NodeProxyMQTT}
	 * @param persistence the MqttClientPersistence layer.
	 * @param executorService the {@link ExecutorService}
	 * @throws MqttException if an exception occurs whilst communicating with the server
	 */
	public ClientComms(NodeProxyMQTT client, SimpleKeyValueList<String, MqttWireMessage> persistence, ExecutorService executorService) throws MqttException {
		this.conState = DISCONNECTED;
		this.client 	= client;
		this.executorService = executorService;

		this.tokenStore = new CommsTokenStore(getClient().getClientId());
		this.callback 	= new CommsCallback(this);
		this.clientState = new ClientState(persistence, tokenStore, this.callback, this);

		callback.setClientState(clientState);
	}

	CommsReceiver getReceiver() {
		return receiver;
	}

	private void shutdownExecutorService() {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
				if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
				}
			}
		} catch (InterruptedException ie) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Sends a message to the server. Does not check if connected this validation must be done
	 * by invoking routines.
	 * @param message
	 * @param token
	 * @throws MqttException
	 */
	void internalSend(MqttWireMessage message, Token token) throws MqttException {
		//@TRACE 200=internalSend key={0} message={1} token={2}

		if (token.getClient() == null ) {
			// Associate the client with the token - also marks it as in use.
			token.setClient(getClient());
		} else {
			// Token is already in use - cannot reuse
			//@TRACE 213=fail: token in use: key={0} message={1} token={2}

			throw MqttException.withReason(MqttException.REASON_CODE_TOKEN_INUSE);
		}

		try {
			// Persist if needed and send the message
			this.clientState.send(message, token);
		} catch(MqttException e) {
			if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
				this.clientState.undo(message);
			}
			throw e;
		}
	}

	/**
	 * Sends a message to the broker if in connected state, but only waits for the message to be
	 * stored, before returning.
	 * @param message The {@link MqttWireMessage} to send
	 * @param token The {@link Token} to send.
	 * @throws MqttException if an error occurs sending the message
	 */
	public void sendNoWait(MqttWireMessage message, Token token) throws MqttException {
		if (isConnected() ||
				(!isConnected() && message.getType() == MqttWireMessage.MESSAGE_TYPE_CONNECT) ||
				(isDisconnecting() && message.getType() == MqttWireMessage.MESSAGE_TYPE_DISCONNECT)) {
				this.internalSend(message, token);
		} else {
			//@TRACE 208=failed: not connected
			throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
		}
	}

	/**
	 * Close and tidy up.
	 *
	 * Call each main class and let it tidy up e.g. releasing the token
	 * store which normally survives a disconnect.
	 * @param force Force close
	 * @throws MqttException  if not disconnected
	 */
	public void close(boolean force) throws MqttException {
		synchronized (conLock) {
			if (!isClosed()) {
				// Must be disconnected before close can take place or if we are being forced
				if (!isDisconnected() || force) {
					//@TRACE 224=failed: not disconnected

					if (isConnecting()) {
						throw MqttException.withReason(MqttException.REASON_CODE_CONNECT_IN_PROGRESS);
					} else if (isConnected()) {
						throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CONNECTED);
					} else if (isDisconnecting()) {
						closePending = true;
						return;
					}
				}

				conState = CLOSED;
				shutdownExecutorService();
				// ShutdownConnection has already cleaned most things
				clientState.close();
				clientState = null;
				callback = null;
				sender = null;
				receiver = null;
				networkModule = null;
				tokenStore = null;
			}
		}
	}

	/**
	 * Sends a connect message and waits for an ACK or NACK.
	 * Connecting is a special case which will also start up the
	 * network connection, receive thread, and keep alive thread.
	 * @param token The {@link Token} to track the connection
	 * @throws MqttException if an error occurs when connecting
	 */
	public void connect(Token token) throws MqttException {
		synchronized (conLock) {
			if (isDisconnected() && !closePending) {
				//@TRACE 214=state=CONNECTING

				conState = CONNECTING;

				MqttWireMessage connect = MqttWireMessage.create(MqttWireMessage.MESSAGE_TYPE_CONNECT);
				connect.withNames(client.getClientId(), client.getUserName(), client.getPassword());
				connect.withKeepAliveInterval(client.getKeepAliveInterval()); 
				connect.withCode(client.getMqttVersion());
				connect.withSession(client.isCleanSession());

				this.clientState.setKeepAliveSecs(client.getKeepAliveInterval());
				this.clientState.setCleanSession(client.isCleanSession());
				this.clientState.setMaxInflight(client.getMaxInflight());

				tokenStore.open();
				ConnectBG conbg = new ConnectBG(this, token, connect, executorService);
				conbg.start();
			}
			else {
				// @TRACE 207=connect failed: not disconnected {0}
				if (isClosed() || closePending) {
					throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CLOSED);
				} else if (isConnecting()) {
					throw MqttException.withReason(MqttException.REASON_CODE_CONNECT_IN_PROGRESS);
				} else if (isDisconnecting()) {
					throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECTING);
				} else {
					throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CONNECTED);
				}
			}
		}
	}

	public void connectComplete( MqttWireMessage cack, MqttException mex) throws MqttException {
		int rc = cack.getReturnCode();
		synchronized (conLock) {
			if (rc == 0) {
				// We've successfully connected
				// @TRACE 215=state=CONNECTED

				conState = CONNECTED;
				return;
			}
		}

		// @TRACE 204=connect failed: rc={0}
		throw mex;
	}

	/**
	 * Shuts down the connection to the server.
	 * This may have been invoked as a result of a user calling disconnect or
	 * an abnormal disconnection.  The method may be invoked multiple times
	 * in parallel as each thread when it receives an error uses this method
	 * to ensure that shutdown completes successfully.
	 * @param token the {@link Token} To track closing the connection
	 * @param reason the {@link MqttException} thrown requiring the connection to be shut down.
	 */
	public void shutdownConnection(Token token, MqttException reason) {
		boolean wasConnected;
		Token endToken = null; 		//Token to notify after disconnect completes

		// This method could concurrently be invoked from many places only allow it
		// to run once.
		synchronized(conLock) {
			if (stoppingComms || closePending || isClosed()) {
				return;
			}
			stoppingComms = true;

			//@TRACE 216=state=DISCONNECTING

			wasConnected = (isConnected() || isDisconnecting());
			conState = DISCONNECTING;
		}

		// Update the token with the reason for shutdown if it
		// is not already complete.
		if (token != null && !token.isComplete()) {
			token.setException(reason);
		}

		// Stop the thread that is used to call the user back
		// when actions complete
		if (callback!= null) {callback.stop(); }

		// Stop the thread that handles inbound work from the network
		if (receiver != null) {receiver.stop();}

		// Stop the network module, send and receive now not possible
		try {
			if (networkModule != null) {
				networkModule.stop();
			}
		} catch (Exception ioe) {
			// Ignore as we are shutting down
		}

		// Stop any new tokens being saved by app and throwing an exception if they do
		tokenStore.quiesce(MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECTING));

		// Notify any outstanding tokens with the exception of
		// con or discon which may be returned and will be notified at
		// the end
		endToken = handleOldTokens(token, reason);

		try {
			// Clean session handling and tidy up
			clientState.disconnected(reason);
		}catch(Exception ex) {
			// Ignore as we are shutting down
		}

		if (sender != null) { sender.stop(); }

		// All disconnect logic has been completed allowing the
		// client to be marked as disconnected.
		synchronized(conLock) {
			//@TRACE 217=state=DISCONNECTED
			conState = DISCONNECTED;
			stoppingComms = false;
		}

		// Internal disconnect processing has completed.  If there
		// is a disconnect token or a connect in error notify
		// it now. This is done at the end to allow a new connect
		// to be processed and now throw a currently disconnecting error.
		// any outstanding tokens and unblock any waiters
		if (endToken != null & callback != null) {
			callback.asyncOperationComplete(endToken);
		}

		if (wasConnected && callback != null) {
			// Let the user know client has disconnected either normally or abnormally
			callback.connectionLost(reason);
		}

		// While disconnecting, close may have been requested - try it now
		synchronized(conLock) {
			if (closePending) {
				try {
					close(true);
				} catch (Exception e) { // ignore any errors as closing
				}
			}
		}
	}

	// Tidy up. There may be tokens outstanding as the client was
	// not disconnected/quiseced cleanly! Work out what tokens still
	// need to be notified and waiters unblocked. Store the
	// disconnect or connect token to notify after disconnect is
	// complete.
	private Token handleOldTokens(Token token, MqttException reason) {
		//@TRACE 222=>

		Token tokToNotifyLater = null;
		try {
			// First the token that was related to the disconnect / shutdown may
			// not be in the token table - temporarily add it if not
			if (token != null) {
				if (tokenStore.getToken(token.getKey())==null) {
					tokenStore.saveToken(token, token.getKey());
				}
			}

			Vector<Token> toksToNot = clientState.resolveOldTokens(reason);
			Enumeration<Token> toksToNotE = toksToNot.elements();
			while(toksToNotE.hasMoreElements()) {
				Token tok = (Token)toksToNotE.nextElement();

				if (tok.getKey().equals(MqttWireMessage.KEY_DISCONNECT) ||
						tok.getKey().equals(MqttWireMessage.KEY_CONNECT)) {
					// Its con or discon so remember and notify @ end of disc routine
					tokToNotifyLater = tok;
				} else {
					// notify waiters and callbacks of outstanding tokens
					// that a problem has occurred and disconnect is in
					// progress
					callback.asyncOperationComplete(tok);
				}
			}
		}catch(Exception ex) {
			// Ignore as we are shutting down
		}
		return tokToNotifyLater;
	}

	public void disconnect(MqttWireMessage disconnect, long quiesceTimeout, Token token) throws MqttException {
		synchronized (conLock){
			if (isClosed()) {
				//@TRACE 223=failed: in closed state
				throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_CLOSED);
			} else if (isDisconnected()) {
				//@TRACE 211=failed: already disconnected
				throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_ALREADY_DISCONNECTED);
			} else if (isDisconnecting()) {
				//@TRACE 219=failed: already disconnecting
				throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECTING);
			} else if (Thread.currentThread() == callback.getThread()) {
				//@TRACE 210=failed: called on callback thread
				// Not allowed to call disconnect() from the callback, as it will deadlock.
				throw MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECT_PROHIBITED);
			}

			//@TRACE 218=state=DISCONNECTING
			conState = DISCONNECTING;
			DisconnectBG discbg = new DisconnectBG(disconnect,quiesceTimeout,token, executorService);
			discbg.start();
		}
	}

	public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) throws MqttException {
		disconnectForcibly(quiesceTimeout, disconnectTimeout, true);
	}

	/**
	 * Disconnect the connection and reset all the states.
	 * @param quiesceTimeout How long to wait whilst quiesing before messages are deleted.
	 * @param disconnectTimeout How long to wait whilst disconnecting
	 * @param sendDisconnectPacket If true, will send a disconnect packet
	 * @throws MqttException if an error occurs whilst disconnecting
	 */
	public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout, boolean sendDisconnectPacket) throws MqttException {
		// Allow current inbound and outbound work to complete
		if (clientState != null) {
			clientState.quiesce(quiesceTimeout);
		}
		Token token = new Token(client.getClientId());
		try {
			// Send disconnect packet
			if(sendDisconnectPacket) {
				internalSend(MqttWireMessage.create(MqttWireMessage.MESSAGE_TYPE_DISCONNECT), token);

				// Wait util the disconnect packet sent with timeout
				token.waitForCompletion(disconnectTimeout);
			}
		}
		catch (Exception ex) {
			// ignore, probably means we failed to send the disconnect packet.
		}
		finally {
			token.markComplete(null, null);
			shutdownConnection(token, null);
		}
	}

	public boolean isConnected() {
		synchronized (conLock) {
			return conState == CONNECTED;
		}
	}

	public boolean isConnecting() {
		synchronized (conLock) {
			return conState == CONNECTING;
		}
	}

	public boolean isDisconnected() {
		synchronized (conLock) {
			return conState == DISCONNECTED;
		}
	}

	public boolean isDisconnecting() {
		synchronized (conLock) {
			return conState == DISCONNECTING;
		}
	}

	public boolean isClosed() {
		synchronized (conLock) {
			return conState == CLOSED;
		}
	}

	public boolean isResting() {
		synchronized (conLock) {
			return resting;
		}
	}


	public void setCallback(SimpleEventCondition mqttCallback) {
		this.callback.setCallback(mqttCallback);
	}

	public SimpleEventCondition getCallback() {
		return callback.getCallBack();
	}

	public void setManualAcks(boolean manualAcks) {
		this.callback.setManualAcks(manualAcks);
	}

	public void messageArrivedComplete(int messageId, int qos) throws MqttException {
		this.callback.messageArrivedComplete(messageId, qos);
	}

	protected MqttTopic getTopic(String topic) {
		return new MqttTopic(topic, this);
	}

	public TCPNetworkModule getNetworkModules() {
		return networkModule;
	}
	public void setNetworkModules(TCPNetworkModule networkModules) {
		this.networkModule = networkModules;
	}

	protected void deliveryComplete(MqttWireMessage msg) throws MqttException {
		this.clientState.deliveryComplete(msg);
	}

	protected void deliveryComplete(int messageId) throws MqttException {
		this.clientState.deliveryComplete(messageId);
	}

	public NodeProxyMQTT getClient() {
		return client;
	}

	public long getKeepAlive() {
		return this.clientState.getKeepAlive();
	}

	public ClientState getClientState() {
		return clientState;
	}

	// Kick off the connect processing in the background so that it does not block. For instance
	// the socket could take time to create.
	private class ConnectBG implements Runnable {
		ClientComms 	clientComms = null;
		Token 		conToken;
		MqttWireMessage conPacket;
		private String threadName;

		ConnectBG(ClientComms cc, Token cToken, MqttWireMessage cPacket, ExecutorService executorService) {
			clientComms = cc;
			conToken 	= cToken;
			conPacket 	= cPacket;
			threadName = "MQTT Con: "+getClient().getClientId();
		}

		void start() {
			executorService.execute(this);
		}

		public void run() {
			Thread.currentThread().setName(threadName);
			MqttException mqttEx = null;
			//@TRACE 220=>

			try {
				// Save the connect token in tokenStore as failure can occur before send
				tokenStore.saveToken(conToken,conPacket);

				// Connect to the server at the network level e.g. TCP socket and then
				// start the background processing threads before sending the connect
				// packet.
				networkModule.start();
				receiver = new CommsReceiver(clientComms, clientState, tokenStore, networkModule.getInputStream());
				receiver.start("MQTT Rec: "+getClient().getClientId(), executorService);
				sender = new CommsSender(clientComms, clientState, tokenStore, networkModule.getOutputStream());
				sender.start("MQTT Snd: "+getClient().getClientId(), executorService);
				callback.start("MQTT Call: "+getClient().getClientId(), executorService);
				internalSend(conPacket, conToken);
			} catch (MqttException ex) {
				//@TRACE 212=connect failed: unexpected exception
				mqttEx = ex;
			} catch (Exception ex) {
				//@TRACE 209=connect failed: unexpected exception
				mqttEx = MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex);
			}

			if (mqttEx != null) {
				shutdownConnection(conToken, mqttEx);
			}
		}
	}

	// Kick off the disconnect processing in the background so that it does not block. For instance
	// the quiesce
	private class DisconnectBG implements Runnable {
		MqttWireMessage disconnect;
		long quiesceTimeout;
		Token token;
		private String threadName;

		DisconnectBG(MqttWireMessage disconnect, long quiesceTimeout, Token token, ExecutorService executorService) {
			this.disconnect = disconnect;
			this.quiesceTimeout = quiesceTimeout;
			this.token = token;
		}

		void start() {
			threadName = "MQTT Disc: "+getClient().getClientId();
			executorService.execute(this);
		}

		public void run() {
			Thread.currentThread().setName(threadName);
			//@TRACE 221=>

			// Allow current inbound and outbound work to complete
			clientState.quiesce(quiesceTimeout);
			try {
				internalSend(disconnect, token);
				token.waitUntilSent();
			}
			catch (MqttException ex) {
			}
			finally {
				token.markComplete(null, null);
				shutdownConnection(token, null);
			}
		}
	}

	/**
	 * When Automatic reconnect is enabled, we want ClientComs to enter the
	 * 'resting' state if disconnected. This will allow us to publish messages
	 * @param resting if true, resting is enabled
	 */
	public void setRestingState(boolean resting) {
		this.resting = resting;
	}

	public int getActualInFlight() {
		return this.clientState.getActualInFlight();
	}
}
