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

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class MessageRequest implements Runnable {
	public static final int BUFFER = 100 * 1024;
	private final Socket requestSocket;
	private final NodeProxyTCP proxy;

	public MessageRequest() {
		this.requestSocket = null;
		this.proxy = null;
	}

	public MessageRequest(NodeProxyTCP proxy, Socket requestSocket) {
		this.requestSocket = requestSocket;
		this.proxy = proxy;
	}

	public void run() {
		try {
			proxy.executeInputStream(requestSocket);
		} catch (Exception e) {
			Space space = proxy.getSpace();
			if (space != null) {
				space.handleException(e);
			}
		}
	}

	public static void executeTask(NodeProxyTCP proxy, Socket requestSocket) {
		MessageRequest task = new MessageRequest(proxy, requestSocket);
		TaskExecutor executor = proxy.getExecutor();
		if (executor != null) {
			executor.executeTask(task, 0);
		}
	}
}
