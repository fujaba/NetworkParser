package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.net.Socket;

import de.uniks.networkparser.interfaces.Condition;

/**
 * Request for Message.
 *
 * @author Stefan Lindel
 */
public class MessageRequest implements Runnable {
	
	/** The Constant BUFFER. */
	public static final int BUFFER = 100 * 1024;
	private final Socket requestSocket;
	private Condition<Socket> handler;

	/**
	 * Instantiates a new message request.
	 */
	public MessageRequest() {
		this.requestSocket = null;
	}

	/**
	 * Instantiates a new message request.
	 *
	 * @param requestSocket the request socket
	 * @param handler the handler
	 */
	public MessageRequest(Socket requestSocket, Condition<Socket> handler) {
		this.requestSocket = requestSocket;
		this.handler = handler;
	}

	/**
	 * Run.
	 */
	public void run() {
		handler.update(requestSocket);
	}

	/**
	 * Creates the task.
	 *
	 * @param requestSocket the request socket
	 * @param handler the handler
	 * @return the message request
	 */
	public MessageRequest createTask(Socket requestSocket, Condition<Socket> handler) {
		return new MessageRequest(requestSocket, handler);
	}
}
