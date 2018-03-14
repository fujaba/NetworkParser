/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corp and others.
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
 *    Ian Craggs - fix duplicate message id (Bug 466853)
 *    Ian Craggs - ack control (bug 472172)
 *    James Sutton - Ping Callback (bug 473928)
 *    Ian Craggs - fix for NPE bug 470718
 *    James Sutton - Automatic Reconnect & Offline Buffering
 *    Jens Reimann - Fix issue #370
 */
package de.uniks.networkparser.ext.mqtt.internal;

import java.io.EOFException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import de.uniks.networkparser.ext.io.Message;
import de.uniks.networkparser.ext.io.StringInputStream;
import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The core of the client, which holds the state information for pending and
 * in-flight messages.
 *
 * Messages that have been accepted for delivery are moved between several objects
 * while being delivered.
 *
 * 1) When the client is not running messages are stored in a persistent store that
 * implements the MqttClientPersistent Interface. The default is MqttDefaultFilePersistencew
 * which stores messages safely across failures and system restarts. If no persistence
 * is specified there is a fall back to MemoryPersistence which will maintain the messages
 * while the Mqtt client is instantiated.
 *
 * 2) When the client or specifically ClientState is instantiated the messages are
 * read from the persistent store into:
 * - outboundqos2 hashtable if a QoS 2 PUBLISH or PUBREL
 * - outboundqos1 hashtable if a QoS 1 PUBLISH
 * (see restoreState)
 *
 * 3) On Connect, copy messages from the outbound hashtables to the pendingMessages or
 * pendingFlows vector in messageid order.
 * - Initial message publish goes onto the pendingmessages buffer.
 * - PUBREL goes onto the pendingflows buffer
 * (see restoreInflightMessages)
 *
 * 4) Sender thread reads messages from the pendingflows and pendingmessages buffer
 * one at a time.  The message is removed from the pendingbuffer but remains on the
 * outbound* hashtable.  The hashtable is the place where the full set of outstanding
 * messages are stored in memory. (Persistence is only used at start up)
 *
 * 5) Receiver thread - receives wire messages:
 *  - if QoS 1 then remove from persistence and outboundqos1
 *  - if QoS 2 PUBREC send PUBREL. Updating the outboundqos2 entry with the PUBREL
 *    and update persistence.
 *  - if QoS 2 PUBCOMP remove from persistence and outboundqos2
 *
 * Notes:
 * because of the multithreaded nature of the client it is vital that any changes to this
 * class take concurrency into account.  For instance as soon as a flow / message is put on
 * the wire it is possible for the receiving thread to receive the ack and to be processing
 * the response before the sending side has finished processing.  For instance a connect may
 * be sent, the conack received before the connect notify send has been processed!
 * @author Paho Client
 */
public class ClientState {
	private static final String PERSISTENCE_SENT_PREFIX = "s-";
	private static final String PERSISTENCE_SENT_BUFFERED_PREFIX = "sb-";
	private static final String PERSISTENCE_CONFIRMED_PREFIX = "sc-";
	private static final String PERSISTENCE_RECEIVED_PREFIX = "r-";

	private static final int MIN_MSG_ID = 1;		// Lowest possible MQTT message ID to use
	private static final int MAX_MSG_ID = 65535;	// Highest possible MQTT message ID to use
	private int nextMsgId = MIN_MSG_ID - 1;			// The next available message ID to use
	private SimpleKeyValueList<Integer, Integer> inUseMsgIds = new SimpleKeyValueList<Integer, Integer>();					// Used to store a set of in-use message IDs

	volatile private Vector<MqttWireMessage> pendingMessages;
	volatile private Vector<MqttWireMessage> pendingFlows = new Vector<MqttWireMessage>();

	private CommsTokenStore tokenStore;
	private ClientComms clientComms = null;
	private CommsCallback callback = null;
	private long keepAlive;
	private boolean cleanSession;
	private SimpleKeyValueList<String, MqttWireMessage> persistence;

	private int maxInflight = 0;
	private int actualInFlight = 0;
	private int inFlightPubRels = 0;

	private Object queueLock = new Object();
	private Object quiesceLock = new Object();
	private boolean quiescing = false;

	private long lastOutboundActivity = 0;
	private long lastInboundActivity = 0;
	private Object pingOutstandingLock = new Object();
	private int pingOutstanding = 0;

	private boolean connected = false;

	private SimpleKeyValueList<Integer, MqttWireMessage> outboundQoS0 = new SimpleKeyValueList<Integer, MqttWireMessage>();
	private SimpleKeyValueList<Integer, MqttWireMessage> outboundQoS1 = new SimpleKeyValueList<Integer, MqttWireMessage>();
	private SimpleKeyValueList<Integer, MqttWireMessage> outboundQoS2 = new SimpleKeyValueList<Integer, MqttWireMessage>();
	private SimpleKeyValueList<Integer, MqttWireMessage> inboundQoS2 = new SimpleKeyValueList<Integer, MqttWireMessage>();

	protected ClientState(SimpleKeyValueList<String, MqttWireMessage> persistence, CommsTokenStore tokenStore,
			CommsCallback callback, ClientComms clientComms) throws MqttException {
		inFlightPubRels = 0;
		actualInFlight = 0;

		this.persistence = persistence;
		this.callback = callback;
		this.tokenStore = tokenStore;
		this.clientComms = clientComms;

		restoreState();
	}

	protected void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
        pendingMessages = new Vector<MqttWireMessage>(this.maxInflight);
	}
    protected void setKeepAliveSecs(long keepAliveSecs) {
		this.keepAlive = keepAliveSecs*1000;
	}
	protected long getKeepAlive() {
		return this.keepAlive;
	}
	protected void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}
	protected boolean getCleanSession() {
		return this.cleanSession;
	}

	private String getSendPersistenceKey(MqttWireMessage message) {
		return PERSISTENCE_SENT_PREFIX + message.getMessageId();
	}

	private String getSendConfirmPersistenceKey(MqttWireMessage message) {
		return PERSISTENCE_CONFIRMED_PREFIX + message.getMessageId();
	}

	private String getReceivedPersistenceKey(MqttWireMessage message) {
		return PERSISTENCE_RECEIVED_PREFIX + message.getMessageId();
	}

	private String getReceivedPersistenceKey(int messageId) {
		return PERSISTENCE_RECEIVED_PREFIX + messageId;
	}

	private String getSendBufferedPersistenceKey(MqttWireMessage message){
		return PERSISTENCE_SENT_BUFFERED_PREFIX + message.getMessageId();
	}

	protected void clearState() throws MqttException {
		//@TRACE 603=clearState

		persistence.clear();
		inUseMsgIds.clear();
		pendingMessages.clear();
		pendingFlows.clear();
		outboundQoS2.clear();
		outboundQoS1.clear();
		outboundQoS0.clear();
		inboundQoS2.clear();
		tokenStore.clear();
	}

	private MqttWireMessage restoreMessage(String key, MqttWireMessage persistable) throws MqttException {
		MqttWireMessage message = null;

		try {
			StringInputStream mbais = new StringInputStream();
			mbais.with(persistable.getHeader());
			mbais.with(persistable.getPayload());

			message = MqttWireMessage.createWireMessage(mbais);
		}
		catch (MqttException ex) {
			//@TRACE 602=key={0} exception
			if (ex.getCause() instanceof EOFException) {
				// Premature end-of-file means that the message is corrupted
				if (key != null) {
					persistence.remove(key);
				}
			}
			else {
				throw ex;
			}
		}
		//@TRACE 601=key={0} message={1}
		return message;
	}

	/**
	 * Inserts a new message to the list, ensuring that list is ordered from lowest to highest in terms of the message id's.
	 * @param list the list to insert the message into
	 * @param newMsg the message to insert into the list
	 */
	private void insertInOrder(Vector<MqttWireMessage> list, MqttWireMessage newMsg) {
		int newMsgId = newMsg.getMessageId();
		for (int i = 0; i < list.size(); i++) {
			MqttWireMessage otherMsg = (MqttWireMessage) list.elementAt(i);
			int otherMsgId = otherMsg.getMessageId();
			if (otherMsgId > newMsgId) {
				list.insertElementAt(newMsg, i);
				return;
			}
		}
		list.addElement(newMsg);
	}

	/**
	 * Produces a new list with the messages properly ordered according to their message id's.
	 * @param list the list containing the messages to produce a new reordered list for
	 * - this will not be modified or replaced, i.e., be read-only to this method
	 * @return a new reordered list
	 */
	private Vector<MqttWireMessage> reOrder(Vector<MqttWireMessage> list) {

		// here up the new list
		Vector<MqttWireMessage> newList = new Vector<MqttWireMessage>();

		if (list.size() == 0) {
			return newList; // nothing to reorder
		}

		int previousMsgId = 0;
		int largestGap = 0;
		int largestGapMsgIdPosInList = 0;
		for (int i = 0; i < list.size(); i++) {
			int currentMsgId = ((MqttWireMessage) list.elementAt(i)).getMessageId();
			if (currentMsgId - previousMsgId > largestGap) {
				largestGap = currentMsgId - previousMsgId;
				largestGapMsgIdPosInList = i;
			}
			previousMsgId = currentMsgId;
		}
		int lowestMsgId = ((MqttWireMessage) list.elementAt(0)).getMessageId();
		int highestMsgId = previousMsgId; // last in the sorted list

		// we need to check that the gap after highest msg id to the lowest msg id is not beaten
		if (MAX_MSG_ID - highestMsgId + lowestMsgId > largestGap) {
			largestGapMsgIdPosInList = 0;
		}

		// starting message has been located, let's start from this point on
		for (int i = largestGapMsgIdPosInList; i < list.size(); i++) {
			newList.addElement(list.elementAt(i));
		}

		// and any wrapping back to the beginning
		for (int i = 0; i < largestGapMsgIdPosInList; i++) {
			newList.addElement(list.elementAt(i));
		}

		return newList;
	}

	/**
	 * Restores the state information from persistence.
	 * @throws MqttException if an exception occurs whilst restoring state
	 */
	protected void restoreState() throws MqttException {
		Iterator<String> messageKeys = persistence.keySet().iterator();
		MqttWireMessage persistable;
		String key;
		int highestMsgId = nextMsgId;
		Vector<String> orphanedPubRels = new Vector<String>();
		//@TRACE 600=>
		while (messageKeys.hasNext()) {
			key = (String) messageKeys.next();
			persistable = persistence.get(key);
			MqttWireMessage message = restoreMessage(key, persistable);
			if (message != null) {
				if (key.startsWith(PERSISTENCE_RECEIVED_PREFIX)) {
					//@TRACE 604=inbound QoS 2 publish key={0} message={1}

					// The inbound messages that we have persisted will be QoS 2
					inboundQoS2.put(Integer.valueOf(message.getMessageId()),message);
				} else if (key.startsWith(PERSISTENCE_SENT_PREFIX)) {
					highestMsgId = Math.max(message.getMessageId(), highestMsgId);
					if (persistence.containsKey(getSendConfirmPersistenceKey(message))) {
						// QoS 2, and CONFIRM has already been sent...
						// NO DUP flag is allowed for 3.1.1 spec while it's not clear for 3.1 spec
						// So we just remove DUP
					} else {
						// QoS 1 or 2, with no CONFIRM sent...
						// Put the SEND to the list of pending messages, ensuring message ID ordering...
						message.setDuplicate(true);
						if (message.getMessage().getQos() == 2) {
							//@TRACE 607=outbound QoS 2 publish key={0} message={1}

							outboundQoS2.put(Integer.valueOf(message.getMessageId()),message);
						} else {
							//@TRACE 608=outbound QoS 1 publish key={0} message={1}

							outboundQoS1.put(Integer.valueOf(message.getMessageId()),message);
						}
					}
					Token tok = tokenStore.restoreToken(message);
					tok.setClient(clientComms.getClient());
					inUseMsgIds.put(Integer.valueOf(message.getMessageId()), Integer.valueOf(message.getMessageId()));
				} else if(key.startsWith(PERSISTENCE_SENT_BUFFERED_PREFIX)){

					// Buffered outgoing messages that have not yet been sent at all
					highestMsgId = Math.max(message.getMessageId(), highestMsgId);
					if(message.getMessage().getQos() == 2){
						//@TRACE 607=outbound QoS 2 publish key={0} message={1}
						outboundQoS2.put(Integer.valueOf(message.getMessageId()), message);
					} else if(message.getMessage().getQos() == 1){
						//@TRACE 608=outbound QoS 1 publish key={0} message={1}

						outboundQoS1.put(Integer.valueOf(message.getMessageId()), message);

					} else {
						//@TRACE 511=outbound QoS 0 publish key={0} message={1}
						outboundQoS0.put(Integer.valueOf(message.getMessageId()), message);
						// Because there is no Puback, we have to trust that this is enough to send the message
						persistence.remove(key);

					}

					Token tok = tokenStore.restoreToken(message);
					tok.setClient(clientComms.getClient());
					inUseMsgIds.put(Integer.valueOf(message.getMessageId()), Integer.valueOf(message.getMessageId()));
				}
			}
		}
		messageKeys = orphanedPubRels.iterator();
		while(messageKeys.hasNext()) {
			key = (String) messageKeys.next();
			//@TRACE 609=removing orphaned pubrel key={0}

			persistence.remove(key);
		}

		nextMsgId = highestMsgId;
	}

	private void restoreInflightMessages() {
		pendingMessages = new Vector<MqttWireMessage>(this.maxInflight);
		pendingFlows = new Vector<MqttWireMessage>();


		Set<Integer> keys = outboundQoS2.keySet();
		for(Integer key : keys) {
			MqttWireMessage msg = outboundQoS2.get(key);
			if (msg.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
				//@TRACE 610=QoS 2 publish key={0}
				// set DUP flag only for PUBLISH, but NOT for PUBREL (spec 3.1.1)
				msg.setDuplicate(true);
				insertInOrder(pendingMessages, msg);
			}
		}
		keys = outboundQoS1.keySet();
		for(Integer key : keys) {
			MqttWireMessage msg = outboundQoS1.get(key);
			msg.setDuplicate(true);
			//@TRACE 612=QoS 1 publish key={0}

			insertInOrder(pendingMessages, msg);
		}
		keys = outboundQoS0.keySet();
		for(Integer key : keys) {
			MqttWireMessage msg = outboundQoS0.get(key);
			//@TRACE 512=QoS 0 publish key={0}
			insertInOrder(pendingMessages, msg);
		}
		this.pendingFlows = reOrder(pendingFlows);
		this.pendingMessages = reOrder(pendingMessages);
	}

	/**
	 * Submits a message for delivery. This method will block until there is
	 * room in the inFlightWindow for the message. The message is put into
	 * persistence before returning.
	 *
	 * @param message  the message to send
	 * @param token the token that can be used to track delivery of the message
	 * @throws MqttException if an exception occurs whilst sending the message
	 */
	public void send(MqttWireMessage message, Token token) throws MqttException {
		if (message.isMessageIdRequired() && (message.getMessageId() == 0)) {
				if(message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH && (message.getMessage().getQos() != 0)){
						message.withMessageId(getNextMessageId());
				}else if(
						message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBACK ||
						message.getType() == MqttWireMessage.MESSAGE_TYPE_SUBACK ||
						message.getType() == MqttWireMessage.MESSAGE_TYPE_SUBSCRIBE ||
						message.getType() == MqttWireMessage.MESSAGE_TYPE_UNSUBSCRIBE){
					message.withMessageId(getNextMessageId());
				}
		}
		if (token != null ) {
			token.setMessageID(message.getMessageId());
		}

		if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
			synchronized (queueLock) {
				if (actualInFlight >= this.maxInflight) {
					//@TRACE 613= sending {0} msgs at max inflight window

					throw MqttException.withReason(MqttException.REASON_CODE_MAX_INFLIGHT);
				}

				Message innerMessage = message.getMessage();
				//@TRACE 628=pending publish key={0} qos={1} message={2}

				switch(innerMessage.getQos()) {
					case 2:
						outboundQoS2.put(Integer.valueOf(message.getMessageId()), message);
						persistence.put(getSendPersistenceKey(message), message);
						break;
					case 1:
						outboundQoS1.put(Integer.valueOf(message.getMessageId()), message);
						persistence.put(getSendPersistenceKey(message), message);
						break;
				}
				tokenStore.saveToken(token, message);
				pendingMessages.addElement(message);
				queueLock.notifyAll();
			}
		} else {
			//@TRACE 615=pending send key={0} message {1}

			if (message.getType() == MqttWireMessage.MESSAGE_TYPE_CONNECT) {
				synchronized (queueLock) {
					// Add the connect action at the head of the pending queue ensuring it jumps
					// ahead of any of other pending actions.
					tokenStore.saveToken(token, message);
					pendingFlows.insertElementAt(message,0);
					queueLock.notifyAll();
				}
			} else {
				synchronized (queueLock) {
					if(MqttWireMessage.isMQTTAck(message) == false) {
						tokenStore.saveToken(token, message);
					}
					pendingFlows.addElement(message);
					queueLock.notifyAll();
				}
			}
		}
	}

	/**
	 * Persists a buffered message to the persistence layer
	 *
	 * @param message The {@link Message} to persist
	 */
	public void persistBufferedMessage(MqttWireMessage message) {
		String key = getSendBufferedPersistenceKey(message);

		// Because the client will have disconnected, we will want to re-open persistence
		try {
			message.withMessageId(getNextMessageId());
			key = getSendBufferedPersistenceKey(message);
			persistence.put(key, message);
			//@TRACE 513=Persisted Buffered Message key={0}
		} catch (MqttException ex){
			//@TRACE 514=Failed to persist buffered message key={0}
		}
	}

	/**
	 * Remove Message
	 * @param message The {@link Message} to un-persist
	 */
	public void unPersistBufferedMessage(MqttWireMessage message){
		//@TRACE 517=Un-Persisting Buffered message key={0}
		persistence.remove(getSendBufferedPersistenceKey(message));
	}

	/**
	 * This removes the MqttSend message from the outbound queue and persistence.
	 * @param message the {@link Message} message to be removed
	 * @throws MqttException if an exception occurs whilst removing the message
	 */
	protected void undo(MqttWireMessage message) throws MqttException {
		synchronized (queueLock) {
			//@TRACE 618=key={0} QoS={1}

			if (message.getMessage().getQos() == 1) {
				outboundQoS1.remove(Integer.valueOf(message.getMessageId()));
			} else {
				outboundQoS2.remove(Integer.valueOf(message.getMessageId()));
			}
			pendingMessages.removeElement(message);
			persistence.remove(getSendPersistenceKey(message));
			tokenStore.removeToken(message);
			if(message.getMessage().getQos() > 0){
				//Free this message Id so it can be used again
				releaseMessageId(message.getMessageId());
				//Set the messageId to 0 so if it's ever retried, it will get a new messageId
				message.withMessageId(0);
			}

			checkQuiesceLock();
		}
	}

	/**
	 * This returns the next piece of work, ie message, for the CommsSender
	 * to send over the network.
	 * Calls to this method block until either:
	 *  - there is a message to be sent
	 *  - the keepAlive interval is exceeded, which triggers a ping message
	 *    to be returned
	 *  - {@link ClientState#disconnected(MqttException)} is called
	 * @return the next message to send, or null if the client is disconnected
	 * @throws MqttException if an exception occurs whilst returning the next piece of work
	 */
	protected MqttWireMessage get() throws MqttException {
		MqttWireMessage result = null;

		synchronized (queueLock) {
			while (result == null) {

				// If there is no work wait until there is work.
				// If the inflight window is full and no flows are pending wait until space is freed.
				// In both cases queueLock will be notified.
				if ((pendingMessages.isEmpty() && pendingFlows.isEmpty()) ||
					(pendingFlows.isEmpty() && actualInFlight >= this.maxInflight)) {
					try {
						//@TRACE 644=wait for new work or for space in the inflight window

						queueLock.wait();

						//@TRACE 647=new work or ping arrived
					} catch (InterruptedException e) {
					}
				}

				// Handle the case where not connected. This should only be the case if:
				// - in the process of disconnecting / shutting down
				// - in the process of connecting
				if (!connected &&
						(pendingFlows.isEmpty() || pendingFlows.elementAt(0).getType() != MqttWireMessage.MESSAGE_TYPE_CONNECT)) {
					//@TRACE 621=no outstanding flows and not connected

					return null;
				}

				// Check if there is a need to send a ping to keep the session alive.
				// Note this check is done before processing messages. If not done first
				// an app that only publishes QoS 0 messages will prevent keepalive processing
				// from functioning.
//				checkForActivity(); //Use pinger, don't check here

				// Now process any queued flows or messages
				if (!pendingFlows.isEmpty()) {
					// Process the first "flow" in the queue
					result = (MqttWireMessage)pendingFlows.remove(0);

					checkQuiesceLock();
				} else if (!pendingMessages.isEmpty()) {

					// If the inflight window is full then messages are not
					// processed until the inflight window has space.
					if (actualInFlight < this.maxInflight) {
						// The in flight window is not full so process the
						// first message in the queue
						result = (MqttWireMessage)pendingMessages.elementAt(0);
						pendingMessages.removeElementAt(0);
						actualInFlight++;

						//@TRACE 623=+1 actualInFlight={0}
					} else {
						//@TRACE 622=inflight window full
					}
				}
			}
		}
		return result;
	}

	public void setKeepAliveInterval(long interval) {
		this.keepAlive = interval;
	}

    public void notifySentBytes(int sentBytesCount) {
        if (sentBytesCount > 0) {
        	this.lastOutboundActivity = System.currentTimeMillis();
        }
        // @TRACE 643=sent bytes count={0}
    }


	/**
	 * Called by the CommsSender when a message has been sent
	 * @param message the {@link Message} to notify
	 */
	protected void notifySent(MqttWireMessage message) {

		this.lastOutboundActivity = System.currentTimeMillis();
		//@TRACE 625=key={0}

		Token token = tokenStore.getToken(message);
		token.notifySent();
		if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PINGREQ) {
			synchronized (pingOutstandingLock) {
				synchronized (pingOutstandingLock) {
					pingOutstanding++;
				}
				//@TRACE 635=ping sent. pingOutstanding: {0}
			}
		} else if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
			if (message.getMessage().getQos() == 0) {
				// once a QoS 0 message is sent we can clean up its records straight away as
				// we won't be hearing about it again
				token.markComplete(null, null);
				callback.asyncOperationComplete(token);
				decrementInFlight();
				releaseMessageId(message.getMessageId());
				tokenStore.removeToken(message);
				checkQuiesceLock();
			}
		}
	}

	private void decrementInFlight() {
		synchronized (queueLock) {
			actualInFlight--;
			//@TRACE 646=-1 actualInFlight={0}

			if (!checkQuiesceLock()) {
				queueLock.notifyAll();
			}
		}
	}

	protected boolean checkQuiesceLock() {
//		if (quiescing && actualInFlight == 0 && pendingFlows.size() == 0 && inFlightPubRels == 0 && callback.isQuiesced()) {
		int tokC = tokenStore.count();
		if (quiescing && tokC == 0 && pendingFlows.size() == 0 && callback.isQuiesced()) {
			//@TRACE 626=quiescing={0} actualInFlight={1} pendingFlows={2} inFlightPubRels={3} callbackQuiesce={4} tokens={5}
			synchronized (quiesceLock) {
				quiesceLock.notifyAll();
			}
			return true;
		}
		return false;
	}

	public void notifyReceivedBytes(int receivedBytesCount) {
		if (receivedBytesCount > 0) {
			this.lastInboundActivity = System.currentTimeMillis();
		}
		// @TRACE 630=received bytes count={0}
	}

	/**
	 * Called by the CommsReceiver when an ack has arrived.
	 *
	 * @param ack The {@link Message} that has arrived
	 * @throws MqttException if an exception occurs when sending / notifying
	 */
	protected void notifyReceivedAck(MqttWireMessage ack) throws MqttException {
		this.lastInboundActivity = System.currentTimeMillis();

		// @TRACE 627=received key={0} message={1}

		Token token = tokenStore.getToken(ack);
		MqttException mex = null;

		if (token == null) {
			// @TRACE 662=no message found for ack id={0}
		} else if (ack.getType() == MqttWireMessage.MESSAGE_TYPE_PUBACK) {
			// QoS 1 & 2 notify users of result before removing from
			// persistence
			notifyResult(ack, token, mex);
			// Do not remove publish / delivery token at this stage
			// do this when the persistence is removed later
		} else if (ack.getType() == MqttWireMessage.MESSAGE_TYPE_CONNACK) {
			int rc = ack.getReturnCode();
			if (rc == 0) {
				synchronized (queueLock) {
					if (cleanSession) {
						clearState();
						// Add the connect token back in so that users can be
						// notified when connect completes.
						tokenStore.saveToken(token,ack);
					}
					inFlightPubRels = 0;
					actualInFlight = 0;
					restoreInflightMessages();
					connected();
				}
			} else {
				mex = MqttException.withReason((short)rc);
				throw mex;
			}

			clientComms.connectComplete(ack, mex);
			notifyResult(ack, token, mex);
			tokenStore.removeToken(ack);

			// Notify the sender thread that there maybe work for it to do now
			synchronized (queueLock) {
				queueLock.notifyAll();
			}
		} else {
			notifyResult(ack, token, mex);
			releaseMessageId(ack.getMessageId());
			tokenStore.removeToken(ack);
		}

		checkQuiesceLock();
	}

	/**
	 * Called by the CommsReceiver when a message has been received.
	 * Handles inbound messages and other flows such as PUBREL.
	 *
	 * @param message The {@link Message} that has been received
	 * @throws MqttException when an exception occurs whilst notifying
	 */
	protected void notifyReceivedMsg(MqttWireMessage message) throws MqttException {
		this.lastInboundActivity = System.currentTimeMillis();

		// @TRACE 651=received key={0} message={1}
		if (!quiescing) {
			if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBLISH) {
				switch (message.getMessage().getQos()) {
				case 0:
				case 1:
					if (callback != null) {
						callback.messageArrived(message);
					}
					break;
				case 2:
					persistence.put(getReceivedPersistenceKey(message), message);
					inboundQoS2.put(Integer.valueOf(message.getMessageId()), message);
					break;

				default:
					//should NOT reach here
				}
			}
		}
	}


	/**
	 * Called when waiters and callbacks have processed the message. For
	 * messages where delivery is complete the message can be removed from
	 * persistence and counters adjusted accordingly. Also tidy up by removing
	 * token from store...
	 *
	 * @param token The {@link Token} that will be used to notify
	 * @throws MqttException if an exception occurs during notification
	 */
	protected void notifyComplete(Token token) throws MqttException {
		MqttWireMessage message = token.getWireMessage();

		if (message != null && MqttWireMessage.isMQTTAck(message)) {

			// @TRACE 629=received key={0} token={1} message={2}
			if (message.getType() == MqttWireMessage.MESSAGE_TYPE_PUBACK) {
				// QoS 1 - user notified now remove from persistence...
				persistence.remove(getSendPersistenceKey(message));
				persistence.remove(getSendBufferedPersistenceKey(message));
				outboundQoS1.remove(Integer.valueOf(message.getMessageId()));
				decrementInFlight();
				releaseMessageId(message.getMessageId());
				tokenStore.removeToken(message);
				// @TRACE 650=removed Qos 1 publish. key={0}
			}
			checkQuiesceLock();
		}
	}

	protected void notifyResult(MqttWireMessage ack, Token token, MqttException ex) {
		// unblock any threads waiting on the token
		token.markComplete(ack, ex);
		token.notifyComplete();

		// Let the user know an async operation has completed and then remove the token
		if (ack != null && MqttWireMessage.isMQTTAck(ack)) {
			//@TRACE 648=key{0}, msg={1}, excep={2}
			callback.asyncOperationComplete(token);
		}
		// There are cases where there is no ack as the operation failed before
		// an ack was received
		if (ack == null ) {
			//@TRACE 649=key={0},excep={1}
			callback.asyncOperationComplete(token);
		}
	}

	/**
	 * Called when the client has successfully connected to the broker
	 */
	public void connected() {
		//@TRACE 631=connected
		this.connected = true;
	}

	/**
	 * Called during shutdown to work out if there are any tokens still
	 * to be notified and waiters to be unblocked.  Notifying and unblocking
	 * takes place after most shutdown processing has completed. The tokenstore
	 * is tidied up so it only contains outstanding delivery tokens which are
	 * valid after reconnect (if clean session is false)
	 * @param reason The root cause of the disconnection, or null if it is a clean disconnect
	 * @return Vektor of Tokens {@link Vector}
	 */
	public Vector<Token> resolveOldTokens(MqttException reason) {
		//@TRACE 632=reason {0}

		// If any outstanding let the user know the reason why it is still
		// outstanding by putting the reason shutdown is occurring into the
		// token.
		MqttException shutReason = reason;
		if (reason == null) {
			shutReason = MqttException.withReason(MqttException.REASON_CODE_CLIENT_DISCONNECTING);
		}

		// Set the token up so it is ready to be notified after disconnect
		// processing has completed. Do not
		// remove the token from the store if it is a delivery token, it is
		// valid after a reconnect.
		Vector<Token> outT = tokenStore.getOutstandingTokens();
		Enumeration<Token> outTE = outT.elements();
		while (outTE.hasMoreElements()) {
			Token tok = (Token)outTE.nextElement();
			synchronized (tok) {
				if (!tok.isComplete() && !tok.isCompletePending() && tok.getException() == null) {
					tok.setException(shutReason);
				}
			}
				tokenStore.removeToken(tok.getKey());
		}
		return outT;
	}

	/**
	 * Called when the client has been disconnected from the broker.
	 * @param reason The root cause of the disconnection, or null if it is a clean disconnect
	 */
	public void disconnected(MqttException reason) {
		//@TRACE 633=disconnected
		this.connected = false;
		try {
			if (cleanSession) {
				clearState();
			}

			pendingMessages.clear();
			pendingFlows.clear();
			synchronized (pingOutstandingLock) {
				// Reset pingOutstanding to allow reconnects to assume no previous ping.
			    pingOutstanding = 0;
			}		
		} catch (MqttException e) {
			// Ignore as we have disconnected at this point
		}
	}

	/**
	 * Releases a message ID back into the pool of available message IDs.
	 * If the supplied message ID is not in use, then nothing will happen.
	 *
	 * @param msgId A message ID that can be freed up for re-use.
	 */
	private synchronized void releaseMessageId(int msgId) {
		inUseMsgIds.remove(Integer.valueOf(msgId));
	}

	/**
	 * Get the next MQTT message ID that is not already in use, and marks
	 * it as now being in use.
	 *
	 * @return the next MQTT message ID to use
	 */
	private synchronized int getNextMessageId() throws MqttException {
		int startingMessageId = nextMsgId;
		// Allow two complete passes of the message ID range. This gives
		// any asynchronous releases a chance to occur
		int loopCount = 0;
	    do {
	        nextMsgId++;
	        if ( nextMsgId > MAX_MSG_ID ) {
	            nextMsgId = MIN_MSG_ID;
	        }
	        if (nextMsgId == startingMessageId) {
	        	loopCount++;
	        	if (loopCount == 2) {
	        		throw MqttException.withReason(MqttException.REASON_CODE_NO_MESSAGE_IDS_AVAILABLE);
	        	}
	        }
	    } while( inUseMsgIds.containsKey( Integer.valueOf(nextMsgId) ) );
	    Integer id = Integer.valueOf(nextMsgId);
	    inUseMsgIds.put(id, id);
	    return nextMsgId;
	}

	/**
	 * Quiesce the client state, preventing any new messages getting sent,
	 * and preventing the callback on any newly received messages.
	 * After the timeout expires, delete any pending messages except for
	 * outbound ACKs, and wait for those ACKs to complete.
	 * @param timeout How long to wait during Quiescing
	 */
	public void quiesce(long timeout) {
		// If the timeout is greater than zero t
		if (timeout > 0 ) {
			//@TRACE 637=timeout={0}
			synchronized (queueLock) {
				this.quiescing = true;
			}
			// We don't want to handle any new inbound messages
			callback.quiesce();
			notifyQueueLock();

			synchronized (quiesceLock) {
				try {
					// If token count is not zero there is outbound work to process and
					// if pending flows is not zero there is outstanding work to complete and
					// if call back is not quiseced there it needs to complete.
					int tokc = tokenStore.count();
					if (tokc > 0 || pendingFlows.size() >0 || !callback.isQuiesced()) {
						//@TRACE 639=wait for outstanding: actualInFlight={0} pendingFlows={1} inFlightPubRels={2} tokens={3}

						// wait for outstanding in flight messages to complete and
						// any pending flows to complete
						quiesceLock.wait(timeout);
					}
				}
				catch (InterruptedException ex) {
					// Don't care, as we're shutting down anyway
				}
			}

			// Quiesce time up or inflight messages delivered.  Ensure pending delivery
			// vectors are cleared ready for disconnect to be sent as the final flow.
			synchronized (queueLock) {
				pendingMessages.clear();
				pendingFlows.clear();
				quiescing = false;
				actualInFlight = 0;
			}
			//@TRACE 640=finished
		}
	}

	public void notifyQueueLock() {
		synchronized (queueLock) {
			//@TRACE 638=notifying queueLock holders
			queueLock.notifyAll();
		}
	}

	protected void deliveryComplete(MqttWireMessage message) throws MqttException {
		//@TRACE 641=remove publish from persistence. key={0}

		persistence.remove(getReceivedPersistenceKey(message));
		inboundQoS2.remove(Integer.valueOf(message.getMessageId()));
	}

	protected void deliveryComplete(int messageId) throws MqttException {
		//@TRACE 641=remove publish from persistence. key={0}

		persistence.remove(getReceivedPersistenceKey(messageId));
		inboundQoS2.remove(Integer.valueOf(messageId));
	}

	public int getActualInFlight(){
		return actualInFlight;
	}

	public int getMaxInFlight(){
		return maxInflight;
	}

	/**
	 * Tidy up
	 * - ensure that tokens are released as they are maintained over a
	 * disconnect / connect cycle.
	 */
	protected void close() {
		inUseMsgIds.clear();
		if (pendingMessages != null) {
			pendingMessages.clear();
		}
		pendingFlows.clear();
		outboundQoS2.clear();
		outboundQoS1.clear();
		outboundQoS0.clear();
		inboundQoS2.clear();
		tokenStore.clear();
		inUseMsgIds = null;
		pendingMessages = null;
		pendingFlows = null;
		outboundQoS2 = null;
		outboundQoS1 = null;
		outboundQoS0 = null;
		inboundQoS2 = null;
		tokenStore = null;
		callback = null;
		clientComms = null;
		persistence = null;
	}

	public Properties getDebug() {
		Properties props = new Properties();
		props.put("In use msgids", inUseMsgIds);
		props.put("pendingMessages", pendingMessages);
		props.put("pendingFlows", pendingFlows);
		props.put("maxInflight", maxInflight);
		props.put("nextMsgID", nextMsgId);
		props.put("actualInFlight", actualInFlight);
		props.put("inFlightPubRels", inFlightPubRels);
		props.put("quiescing", quiescing);
		props.put("pingoutstanding", pingOutstanding);
		props.put("lastOutboundActivity", lastOutboundActivity);
		props.put("lastInboundActivity", lastInboundActivity);
		props.put("outboundQoS2", outboundQoS2);
		props.put("outboundQoS1", outboundQoS1);
		props.put("outboundQoS0", outboundQoS0);
		props.put("inboundQoS2", inboundQoS2);
		props.put("tokens", tokenStore);
		return props;
	}

	public void clearPersistence() {
		this.persistence.clear();
	}
}
