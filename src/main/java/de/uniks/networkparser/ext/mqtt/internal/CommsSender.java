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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import de.uniks.networkparser.ext.mqtt.MqttException;


public class CommsSender implements Runnable {
	//Sends MQTT packets to the server on its own thread
	private boolean running 		= false;
	private Object lifecycle 		= new Object();
	private ClientState clientState = null;
	private OutputStream out;
	private ClientComms clientComms = null;
	private CommsTokenStore tokenStore = null;
	private Thread 	sendThread		= null;

	private String threadName;
	private final Semaphore runningSemaphore = new Semaphore(1);
	private Future<?> senderFuture;

	public CommsSender(ClientComms clientComms, ClientState clientState, CommsTokenStore tokenStore, OutputStream out) {
		this.out = out;
		this.clientComms = clientComms;
		this.clientState = clientState;
		this.tokenStore = tokenStore;
	}

	/**
	 * Starts up the Sender thread.
	 * @param threadName the threadname
	 * @param executorService used to execute the thread
	 */
	public void start(String threadName, ExecutorService executorService) {
		this.threadName = threadName;
		synchronized (lifecycle) {
			if (!running) {
				running = true;
				senderFuture = executorService.submit(this);
			}
		}
	}

	/**
	 * Stops the Sender's thread.  This call will block.
	 */
	public void stop() {
		synchronized (lifecycle) {
			if (senderFuture != null) {
				senderFuture.cancel(true);
			}
			//@TRACE 800=stopping sender
			if (running) {
				running = false;
				if (!Thread.currentThread().equals(sendThread)) {
					try {
						while (running) {
							// first notify get routine to finish
							clientState.notifyQueueLock();
							// Wait for the thread to finish.
							runningSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS);
						}
					} catch (InterruptedException ex) {
					} finally {
						runningSemaphore.release();
					}
				}
			}
			sendThread=null;
			//@TRACE 801=stopped
		}
	}

	public void run() {
		sendThread = Thread.currentThread();
		sendThread.setName(threadName);
		MqttWireMessage message = null;

		try {
			runningSemaphore.acquire();
		} catch (InterruptedException e) {
			running = false;
			return;
		}

		try {
			while (running && (out != null)) {
				try {
					message = clientState.get();
					if (message != null) {
						//@TRACE 802=network send key={0} msg={1}

						if (MqttWireMessage.isMQTTAck(message)) {
							write(message);
							out.flush();
						} else {
							Token token = tokenStore.getToken(message);
							// While quiescing the tokenstore can be cleared so need
							// to check for null for the case where clear occurs
							// while trying to send a message.
							if (token != null) {
								synchronized (token) {
									write(message);
									try {
										out.flush();
									} catch (IOException ex) {
										// The flush has been seen to fail on disconnect of a SSL socket
										// as disconnect is in progress this should not be treated as an error
										if (!(message instanceof MqttDisconnect)) {
											throw ex;
										}
									}
									clientState.notifySent(message);
								}
							}
						}
					} else { // null message
						//@TRACE 803=get message returned null, stopping}

						running = false;
					}
				} catch (MqttException me) {
					handleRunException(message, me);
				} catch (Exception ex) {
					handleRunException(message, ex);
				}
			} // end while
		} finally {
			running = false;
			runningSemaphore.release();
		}
	}
	
    /**
     * Writes an <code>MqttWireMessage</code> to the stream.
     * @param message The {@link MqttWireMessage} to send
     * @throws IOException if an exception is thrown when writing to the output stream.
     * @throws MqttException if an exception is thrown when getting the header or payload
     */
    public void write(MqttWireMessage message) throws IOException, MqttException {
        byte[] bytes = message.getHeader();
        byte[] pl = message.getPayload();
//        out.write(message.getHeader());
//        out.write(message.getPayload());
        out.write(bytes,0,bytes.length);
        clientState.notifySentBytes(bytes.length);
        
        int offset = 0;
        int chunckSize = 1024;
        while (offset < pl.length) {
            int length = Math.min(chunckSize, pl.length - offset);
            out.write(pl, offset, length);
            offset += chunckSize;
            clientState.notifySentBytes(length);
        }        
        out.flush();
    }

	private void handleRunException(MqttWireMessage message, Exception ex) {
		//@TRACE 804=exception
		MqttException mex;
		if ( !(ex instanceof MqttException)) {
			mex = MqttException.withReason(MqttException.REASON_CODE_CONNECTION_LOST, ex);
		} else {
			mex = (MqttException)ex;
		}

		running = false;
		clientComms.shutdownConnection(null, mex);
	}
}
