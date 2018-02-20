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
 *    James Sutton - Automatic Reconnect & Offline Buffering
 */
package de.uniks.networkparser.ext.mqtt.internal;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.mqtt.MqttException;
import de.uniks.networkparser.ext.mqtt.MqttMessage;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyMQTT;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

/**
 * Bridge between Receiver and the external API. This class gets called by
 * Receiver, and then converts the comms-centric MQTT message objects into ones
 * understood by the external API.
 * @author Paho Client
 */
public class CommsCallback implements Runnable {
	private static final int INBOUND_QUEUE_SIZE = 10;
	private SimpleEventCondition mqttCallback;
	private ClientComms clientComms;
	private Vector<MqttWireMessage> messageQueue;
	private Vector<Token> completeQueue;
	public boolean running = false;
	private boolean quiescing = false;
	private Object lifecycle = new Object();
	private Thread callbackThread;
	private Object workAvailable = new Object();
	private Object spaceAvailable = new Object();
	private ClientState clientState;
	private boolean manualAcks = false;
	private String threadName;
	private final Semaphore runningSemaphore = new Semaphore(1);
	private Future<?> callbackFuture;

	CommsCallback(ClientComms clientComms) {
		this.clientComms = clientComms;
		this.messageQueue = new Vector<MqttWireMessage>(INBOUND_QUEUE_SIZE);
		this.completeQueue = new Vector<Token>(INBOUND_QUEUE_SIZE);
	}

	public void setClientState(ClientState clientState) {
		this.clientState = clientState;
	}

	/**
	 * Starts up the Callback thread.
	 * @param threadName The name of the thread
	 * @param executorService the {@link ExecutorService}
	 */
	public void start(String threadName, ExecutorService executorService) {
		this.threadName = threadName;
		synchronized (lifecycle) {
			if (!running) {
				// Preparatory work before starting the background thread.
				// For safety ensure any old events are cleared.
				messageQueue.clear();
				completeQueue.clear();

				running = true;
				quiescing = false;
				callbackFuture = executorService.submit(this);
			}
		}
	}

	/**
	 * Stops the callback thread.
	 * This call will block until stop has completed.
	 */
	public void stop() {
		synchronized (lifecycle) {
			if (callbackFuture != null) {
				callbackFuture.cancel(true);
			}
			if (running) {
				// @TRACE 700=stopping
				running = false;
				if (!Thread.currentThread().equals(callbackThread)) {
					try {
						synchronized (workAvailable) {
							// @TRACE 701=notify workAvailable and wait for run
							// to finish
							workAvailable.notifyAll();
						}
						// Wait for the thread to finish.
						runningSemaphore.acquire();
					} catch (InterruptedException ex) {
					} finally {
						runningSemaphore.release();
					}
				}
			}
			callbackThread = null;
			// @TRACE 703=stopped
		}
	}

	public void setCallback(SimpleEventCondition mqttCallback) {
		this.mqttCallback = mqttCallback;
	}

	public void setManualAcks(boolean manualAcks) {
		this.manualAcks = manualAcks;
	}

	public void run() {
		callbackThread = Thread.currentThread();
		callbackThread.setName(threadName);

		try {
			runningSemaphore.acquire();
		} catch (InterruptedException e) {
			running = false;
			return;
		}

		while (running) {
			try {
				// If no work is currently available, then wait until there is some...
				try {
					synchronized (workAvailable) {
						if (running && messageQueue.isEmpty()
								&& completeQueue.isEmpty()) {
							// @TRACE 704=wait for workAvailable
							workAvailable.wait();
						}
					}
				} catch (InterruptedException e) {
				}

				if (running) {
					// Check for deliveryComplete callbacks...
					Token token = null;
					synchronized (completeQueue) {
					    if (!completeQueue.isEmpty()) {
						    // First call the delivery arrived callback if needed
						    token = (Token) completeQueue.elementAt(0);
						    completeQueue.removeElementAt(0);
					    }
					}
					if (null != token) {
						handleActionComplete(token);
					}

					// Check for messageArrived callbacks...
					MqttWireMessage message = null;
					synchronized (messageQueue) {
						if (!messageQueue.isEmpty()) {
							// Note, there is a window on connect where a publish
							// could arrive before we've
							// finished the connect logic.
							message = messageQueue.elementAt(0);
							messageQueue.removeElementAt(0);
						}
					}
					if (null != message) {
						handleMessage(message);
					}
				}

				if (quiescing) {
					clientState.checkQuiesceLock();
				}

			} catch (Throwable ex) {
				// Users code could throw an Error or Exception e.g. in the case
				// of class NoClassDefFoundError
				// @TRACE 714=callback threw exception
				running = false;
				clientComms.shutdownConnection(null, MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex));
			} finally {
				runningSemaphore.release();
			    synchronized (spaceAvailable) {
                    // Notify the spaceAvailable lock, to say that there's now
                    // some space on the queue...

                    // @TRACE 706=notify spaceAvailable
                    spaceAvailable.notifyAll();
                }
			}
		}
	}

	private void handleActionComplete(Token token)
			throws MqttException {
		synchronized (token) {
			// @TRACE 705=callback and notify for key={0}
			if (token.isComplete()) {
				// Finish by doing any post processing such as delete
				// from persistent store but only do so if the action
				// is complete
				clientState.notifyComplete(token);
			}

			// Unblock any waiters and if pending complete now set completed
			token.notifyComplete();

			if (token.isNotified() == false) {
				fireActionEvent(token);
				token.setNotified(true);
			}
		}
	}

	/**
	 * This method is called when the connection to the server is lost. If there
	 * is no cause then it was a clean disconnect. The connectionLost callback
	 * will be invoked if registered and run on the thread that requested
	 * shutdown e.g. receiver or sender thread. If the request was a user
	 * initiated disconnect then the disconnect token will be notified.
	 *
	 * @param cause  the reason behind the loss of connection.
	 */
	public void connectionLost(MqttException cause) {
		// If there was a problem and a client callback has been set inform
		// the connection lost listener of the problem.
		try {
			if (mqttCallback != null && cause != null) {
				// @TRACE 708=call connectionLost
				SimpleEvent event=new SimpleEvent(this.clientComms.getClient(), NodeProxyMQTT.EVENT_CONNECT, null, cause).withType(NodeProxyMQTT.EVENT_CONNECTLOST);
				mqttCallback.update(event);
//				mqttCallback.connectionLost(cause);
			}
		} catch (java.lang.Throwable t) {
			// Just log the fact that a throwable has caught connection lost
			// is called during shutdown processing so no need to do anything else
			// @TRACE 720=exception from connectionLost {0}
		}
	}

	/**
	 * An action has completed - if a completion listener has been set on the
	 * token then invoke it with the outcome of the action.
	 *
	 * @param token The {@link Token} that has completed
	 */
	public void fireActionEvent(Token token) {
		if (token != null) {
			if (token.getException() == null) {
				// @TRACE 716=call onSuccess key={0}
				clientComms.onSuccess(token);
			}else {
				// @TRACE 717=call onFailure key {0}
				clientComms.onFailure(token, token.getException());
			}
		}
	}

	/**
	 * This method is called when a message arrives on a topic. Messages are
	 * only added to the queue for inbound messages if the client is not
	 * quiescing.
	 *
	 * @param sendMessage the MQTT SEND message.
	 */
	public void messageArrived(MqttWireMessage sendMessage) {
		if (mqttCallback != null) {
			// If we already have enough messages queued up in memory, wait
			// until some more queue space becomes available. This helps
			// the client protect itself from getting flooded by messages
			// from the server.
			synchronized (spaceAvailable) {
				while (running && !quiescing && messageQueue.size() >= INBOUND_QUEUE_SIZE) {
					try {
						// @TRACE 709=wait for spaceAvailable
						spaceAvailable.wait(200);
					} catch (InterruptedException ex) {
					}
				}
			}
			if (!quiescing) {
				messageQueue.addElement(sendMessage);
				// Notify the CommsCallback thread that there's work to do...
				synchronized (workAvailable) {
					// @TRACE 710=new msg avail, notify workAvailable
					workAvailable.notifyAll();
				}
			}
		}
	}

	/**
	 * Let the call back thread quiesce. Prevent new inbound messages being
	 * added to the process queue and let existing work quiesce. (until the
	 * thread is told to shutdown).
	 */
	public void quiesce() {
		this.quiescing = true;
		synchronized (spaceAvailable) {
			// @TRACE 711=quiesce notify spaceAvailable
			// Unblock anything waiting for space...
			spaceAvailable.notifyAll();
		}
	}

	public boolean isQuiesced() {
		if (quiescing && completeQueue.size() == 0 && messageQueue.size() == 0) {
			return true;
		}
		return false;
	}

	private void handleMessage(MqttWireMessage publishMessage)
			throws MqttException, Exception {
		// If quisecing process any pending messages.

		String destName = publishMessage.getTopicName();

		// @TRACE 713=call messageArrived key={0} topic={1}
		deliverMessage(destName, publishMessage.getMessageId(),
				publishMessage.getMessage());

		if (!this.manualAcks) {
			if (publishMessage.getMessage().getQos() == 1) {
				this.clientComms.internalSend(
						MqttWireMessage.create(MqttWireMessage.MESSAGE_TYPE_PUBACK).withMessageId(publishMessage.getMessageId()),
						new Token(clientComms.getClient().getClientId()));
			} else if (publishMessage.getMessage().getQos() == 2) {
				this.clientComms.deliveryComplete(publishMessage);
			}
		}
	}

	public void messageArrivedComplete(int messageId, int qos)
		throws MqttException {
		if (qos == 1) {
			this.clientComms.internalSend(
					MqttWireMessage.create(MqttWireMessage.MESSAGE_TYPE_PUBACK).withMessageId(messageId),
					new Token(clientComms.getClient().getClientId()));
		} else if (qos == 2) {
			this.clientComms.deliveryComplete(messageId);
		}
	}

	public void asyncOperationComplete(Token token) {
		if (running) {
			// invoke callbacks on callback thread
			completeQueue.addElement(token);
			synchronized (workAvailable) {
				// @TRACE 715=new workAvailable. key={0}
				workAvailable.notifyAll();
			}
		} else {
			// invoke async callback on invokers thread
			try {
				handleActionComplete(token);
			} catch (Throwable ex) {
				// Users code could throw an Error or Exception e.g. in the case
				// of class NoClassDefFoundError
				// @TRACE 719=callback threw ex:

				// Shutdown likely already in progress but no harm to confirm
				clientComms.shutdownConnection(null, MqttException.withReason(MqttException.REASON_CODE_DEFAULT, ex));
			}

		}
	}

	/**
	 * Returns the thread used by this callback.
	 * @return The {@link Thread}
	 */
	protected Thread getThread() {
		return callbackThread;
	}

	protected boolean deliverMessage(String topicName, int messageId, MqttMessage aMessage) throws Exception
	{
		boolean delivered = false;

		/* if the message hasn't been delivered to a per subscription handler, give it to the default handler */
		if (mqttCallback != null && !delivered) {
			aMessage.setId(messageId);
			SimpleEvent event = new SimpleEvent(this.clientComms.getClient(), topicName, null, aMessage).withType(NodeProxyMQTT.EVENT_MESSAGE);
			mqttCallback.update(event);
			delivered = true;
		}

		return delivered;
	}

	public SimpleEventCondition getCallBack() {
		return this.mqttCallback;
	}
}
