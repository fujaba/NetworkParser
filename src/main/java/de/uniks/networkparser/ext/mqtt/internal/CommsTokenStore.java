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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.uniks.networkparser.ext.mqtt.MqttException;


/**
 * Provides a "token" based system for storing and tracking actions across 
 * multiple threads. 
 * When a message is sent, a token is associated with the message
 * and saved using the {@link CommsTokenStore#saveToken(Token, MqttWireMessage)} method. Anyone interested
 * in tacking the state can call one of the wait methods on the token or using 
 * the asynchronous listener callback method on the operation. 
 * The {@link CommsReceiver} class, on another thread, reads responses back from 
 * the network. It uses the response to find the relevant token, which it can then 
 * notify. 
 * 
 * Note:
 *   Ping, connect and disconnect do not have a unique message id as
 *   only one outstanding request of each type is allowed to be outstanding
 *   @author Paho Client
 */
public class CommsTokenStore {
	// Maps message-specific data (usually message IDs) to tokens
	private Hashtable<String, Token> tokens;
	private String logContext;
	private MqttException closedResponse = null;

	public CommsTokenStore(String logContext) {
		this.tokens = new Hashtable<String, Token>();
		this.logContext = logContext;
		//@TRACE 308=<>

	}

	/**
	 * Based on the message type that has just been received return the associated
	 * token from the token store or null if one does not exist.
	 * @param message whose token is to be returned 
	 * @return token for the requested message
	 */
	public Token getToken(MqttWireMessage message) {
		String key = ""+message.getMessageId(); 
		return (Token)tokens.get(key);
	}

	public Token getToken(String key) {
		return (Token)tokens.get(key);
	}

	
	public Token removeToken(MqttWireMessage message) {
		if (message != null) {
			return removeToken(""+message.getMessageId());
		}
		return null;
	}
	
	public Token removeToken(String key) {
		//@TRACE 306=key={0}
		
		if ( null != key ){
		    return (Token) tokens.remove(key);
		}
		
		return null;
	}
		
	/**
	 * Restores a token after a client restart.  This method could be called
	 * for a SEND of CONFIRM, but either way, the original SEND is what's 
	 * needed to re-build the token.
	 * @param message The {@link MqttWireMessage} message to restore
	 * @return a Token
	 */
	protected Token restoreToken(MqttWireMessage message) {
		Token token;
		synchronized(tokens) {
			String key = ""+message.getMessageId();
			if (this.tokens.containsKey(key)) {
				token = (Token)this.tokens.get(key);
				//@TRACE 302=existing key={0} message={1} token={2}
			} else {
				token = new Token(logContext);
				token.setKey(key);
				this.tokens.put(key, token);
				//@TRACE 303=creating new token key={0} message={1} token={2}
			}
		}
		return token;
	}
	
	// For outbound messages store the token in the token store 
	// For pubrel use the existing publish token 
	protected void saveToken(Token token, MqttWireMessage message) throws MqttException {
		synchronized(tokens) {
			if (closedResponse == null) {
				String key = ""+message.getMessageId();
				//@TRACE 300=key={0} message={1}
				
				saveToken(token,key);
			} else {
				throw closedResponse;
			}
		}
	}
	
	protected void saveToken(Token token, String key) {
		synchronized(tokens) {
			//@TRACE 307=key={0} token={1}
			token.setKey(key);
			this.tokens.put(key, token);
		}
	}

	protected void quiesce(MqttException quiesceResponse) {
		synchronized(tokens) {
			//@TRACE 309=resp={0}
			closedResponse = quiesceResponse;
		}
	}
	
	public void open() {
		synchronized(tokens) {
			//@TRACE 310=>

			closedResponse = null;
		}
	}

	public Token[] getOutstandingDelTokens() {
		synchronized(tokens) {
			//@TRACE 311=>

			Vector<Token> list = new Vector<Token>();
			Enumeration<Token> enumeration = tokens.elements();
			Token token;
			while(enumeration.hasMoreElements()) {
				token = (Token)enumeration.nextElement();
				if (token != null 
					&& !token.isNotified()) {
					list.addElement(token);
				}
			}
	
			Token[] result = new Token[list.size()];
			return (Token[]) list.toArray(result);
		}
	}
	
	public Vector<Token> getOutstandingTokens() {
		synchronized(tokens) {
			//@TRACE 312=>

			Vector<Token> list = new Vector<Token>();
			Enumeration<Token> enumeration = tokens.elements();
			Token token;
			while(enumeration.hasMoreElements()) {
				token = enumeration.nextElement();
				if (token != null) {
					list.addElement(token);
				}
			}
			return list;
		}
	}

	/**
	 * Empties the token store without notifying any of the tokens.
	 */
	public void clear() {
		//@TRACE 305=> {0} tokens
		synchronized(tokens) {
			tokens.clear();
		}
	}
	
	public int count() {
		synchronized(tokens) {
			return tokens.size();
		}
	}
	public String toString() {
		String lineSep = System.getProperty("line.separator","\n");
		StringBuffer toks = new StringBuffer();
		synchronized(tokens) {
			Enumeration<Token> enumeration = tokens.elements();
			Token token;
			while(enumeration.hasMoreElements()) {
				token = enumeration.nextElement();
					toks.append("{"+token+"}"+lineSep);
			}
			return toks.toString();
		}
	}
}
