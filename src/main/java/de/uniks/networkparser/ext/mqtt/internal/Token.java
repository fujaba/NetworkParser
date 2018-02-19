/*******************************************************************************
 * Copyright (c) 2014 IBM Corp.
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
 */

package de.uniks.networkparser.ext.mqtt.internal;

import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyMQTT;

public class Token {
	private volatile boolean completed = false;
	private boolean pendingComplete = false;
	private boolean sent = false;
	
	private Object responseLock = new Object();
	private Object sentLock = new Object();
	
	protected MqttMessage message = null; 
	private MqttWireMessage response = null;
	private MqttException exception = null;
	private String[] topics = null;
	
	private String key;
	
	private ConnectActionListener callback = null;
	private NodeProxyMQTT client = null;
	
	private int messageID = 0;
	private boolean notified = false;
	
	public Token(String logContext) {
	}
	
	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}
	
	public boolean checkResult() throws MqttException {
		if ( getException() != null)  {
			throw getException();
		}
		return true;
	}

	public MqttException getException() {
		return exception;
	}

	public boolean isComplete() {
		return completed;
	}

	protected boolean isCompletePending() {
		return pendingComplete;
	}

	protected boolean isInUse() {
		return (getClient() != null && !isComplete());
	}

	public void setActionCallback(ConnectActionListener listener) {
		this.callback  = listener;

	}
	public ConnectActionListener getActionCallback() {
		return callback;
	}

	public void waitForCompletion() throws MqttException {
		waitForCompletion(-1);
	}

	public void waitForCompletion(long timeout) throws MqttException {
		//@TRACE 407=key={0} wait max={1} token={2}

		MqttWireMessage resp = waitForResponse(timeout);
		if (resp == null && !completed) {
			//@TRACE 406=key={0} timed out token={1}
			exception = MqttException.withReason(MqttException.REASON_CODE_CLIENT_TIMEOUT);
			throw exception;
		}
		checkResult();
	}
	
	/**
	 * Waits for the message delivery to complete, but doesn't throw an exception
	 * in the case of a NACK.  It does still throw an exception if something else
	 * goes wrong (e.g. an IOException).  This is used for packets like CONNECT, 
	 * which have useful information in the ACK that needs to be accessed.
	 * @return the {@link MqttWireMessage}
	 * @throws MqttException if there is an error whilst waiting for the response
	 */
	protected MqttWireMessage waitForResponse() throws MqttException {
		return waitForResponse(-1);
	}
	
	protected MqttWireMessage waitForResponse(long timeout) throws MqttException {
		synchronized (responseLock) {
			//@TRACE 400=>key={0} timeout={1} sent={2} completed={3} hasException={4} response={5} token={6}

			while (!this.completed) {
				if (this.exception == null) {
					try {
						//@TRACE 408=key={0} wait max={1}
	
						if (timeout <= 0) {
							responseLock.wait();
						} else {
							responseLock.wait(timeout);
						}
					} catch (InterruptedException e) {
						exception = MqttException.withReason(MqttException.REASON_CODE_DEFAULT, e);
					}
				}
				if (!this.completed) {
					if (this.exception != null) {
						//@TRACE 401=failed with exception
						throw exception;
					}
					
					if (timeout > 0) {
						// time up and still not completed
						break;
					}
				}
			}
		}
		//@TRACE 402=key={0} response={1}
		return this.response;
	}
	
	/**
	 * Mark the token as complete and ready for users to be notified.
	 * @param msg response message. Optional - there are no response messages for some flows
	 * @param ex if there was a problem store the exception in the token.
	 */
	protected void markComplete(MqttWireMessage msg, MqttException ex) {
		//@TRACE 404=>key={0} response={1} excep={2}
				
		synchronized(responseLock) {
			// ACK means that everything was OK, so mark the message for garbage collection.
			if (MqttWireMessage.isMQTTAck(msg)) {
				this.message = null;
			}
			this.pendingComplete = true;
			this.response = msg;
			this.exception = ex;
		}
	}
	/**
	 * Notifies this token that a response message (an ACK or NACK) has been
	 * received.
	 */
		protected void notifyComplete() {
			//@TRACE 411=>key={0} response={1} excep={2}

			synchronized (responseLock) {
				// If pending complete is set then normally the token can be marked
				// as complete and users notified. An abnormal error may have 
				// caused the client to shutdown beween pending complete being set
				// and notifying the user.  In this case - the action must be failed.
				if (exception == null && pendingComplete) {
					completed = true;
					pendingComplete = false;
				} else {
					pendingComplete = false;
				}
				
				responseLock.notifyAll();
			}
			synchronized (sentLock) {
				sent=true;	
				sentLock.notifyAll();
			}
		}
	
//	/**
//	 * Notifies this token that an exception has occurred.  This is only
//	 * used for things like IOException, and not for MQTT NACKs.
//	 */
//	protected void notifyException() {
//		final String methodName = "notifyException";
//		//@TRACE 405=token={0} excep={1}
//		log.fine(CLASS_NAME,methodName, "405",new Object[]{this,this.exception});
//		synchronized (responseLock) {
//			responseLock.notifyAll();
//		}
//		synchronized (sentLock) {
//			sentLock.notifyAll();
//		}
//	}

	public void waitUntilSent() throws MqttException {
		synchronized (sentLock) {
			synchronized (responseLock) {
				if (this.exception != null) {
					throw this.exception;
				}
			}
			while (!sent) {
				try {
					//@TRACE 409=wait key={0}

					sentLock.wait();
				} catch (InterruptedException e) {
				}
			}
			
			while (!sent) {
				if (this.exception == null) {
					throw MqttException.withReason(MqttException.REASON_CODE_UNEXPECTED_ERROR);
				}
				throw this.exception;
			}
		}
	}
	
	/**
	 * Notifies this token that the associated message has been sent
	 * (i.e. written to the TCP/IP socket).
	 */
	protected void notifySent() {
		//@TRACE 403=> key={0}
		synchronized (responseLock) {
			this.response = null;
			this.completed = false;
		}
		synchronized (sentLock) {
			sent = true;
			sentLock.notifyAll();
		}
	}
	
	public NodeProxyMQTT getClient() {
		return client;
	}
	
	protected void setClient(NodeProxyMQTT client) {
		this.client = client;
	}

	public void reset() throws MqttException {
		if (isInUse() ) {
			// Token is already in use - cannot reset 
			throw MqttException.withReason(MqttException.REASON_CODE_TOKEN_INUSE);
		}
		//@TRACE 410=> key={0}
		
		client = null;
		completed = false;
		response = null;
		sent = false;
		exception = null;
	}

	public MqttMessage getMessage() {
		return message;
	}
	
	public MqttWireMessage getWireMessage() {
		return response;
	}

	
	public void setMessage(MqttMessage msg) {
		this.message = msg;
	}
	
	public String[] getTopics() {
		return topics;
	}
	
	public void setTopics(String[] topics) {
		this.topics = topics;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setException(MqttException exception) {
		synchronized(responseLock) {
			this.exception = exception;
		}
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public String toString() {
		StringBuffer tok = new StringBuffer();
		tok.append("key=").append(getKey());
		tok.append(" ,topics=");
		if (getTopics() != null) {
			for (int i=0; i<getTopics().length; i++) {
				tok.append(getTopics()[i]).append(", ");
			} 
		}
		tok.append(" ,isComplete=").append(isComplete());
		tok.append(" ,isNotified=").append(isNotified());
		tok.append(" ,exception=").append(getException());
		tok.append(" ,actioncallback=").append(getActionCallback());
		return tok.toString();
	}
	
	public int[] getGrantedQos() {
		int[] val = new int[0];
		if (response.getType() == MqttWireMessage.MESSAGE_TYPE_SUBACK) {
			val = response.getGrantedQos();
		}
		return val;
	}
	
	public boolean getSessionPresent() {
		boolean val = false;
		if (response.getType() == MqttWireMessage.MESSAGE_TYPE_CONNACK) {
			val = response.getSessionPresent();
		}
		return val;
	}
	
	public MqttWireMessage getResponse() {
		return response;
	}

}
