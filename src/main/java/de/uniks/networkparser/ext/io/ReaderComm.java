package de.uniks.networkparser.ext.io;

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
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.petaf.proxy.NodeProxyBroker;
import de.uniks.networkparser.interfaces.ObjectCondition;

/** ReaderComm */
public class ReaderComm implements Runnable {
	private boolean running = false;
	private String threadName;
	private MessageSession session;
	private String channel;
	private NodeProxyBroker broker;
	private ObjectCondition condition;

	public void start(NodeProxyBroker broker, String threadName) {
		this.threadName = threadName;
		this.broker = broker;
		if (running == false) {
			running = true;
		}
	}

	public ReaderComm withSession(MessageSession session) {
		this.session = session;
		return this;
	}

	/**
	 * Stops the Receiver's thread. This call will block.
	 * 
	 * @return boolean for success
	 */
	public boolean stop() {
		if (running) {
			running = false;
			return session.close();
		}
		return false;
	}

	@Override
	public void run() {
		Thread recThread = Thread.currentThread();
		recThread.setName(threadName);
		if (session == null) {
			return;
		}
		while (running && (session.isClose() == false)) {
			try {
				Object response = session.getServerResponse(broker);

				/* Answer */
				if (condition != null) {
					if (response instanceof RabbitMessage) {
						RabbitMessage msg = (RabbitMessage) response;
						String text = msg.getText();
						if (text != null) {
							this.condition.update(
									new SimpleEvent(this, channel, null, text).withType(NodeProxyBroker.EVENT_MESSAGE));
						}
					} else if (response instanceof MQTTMessage) {
						MQTTMessage msg = (MQTTMessage) response;
						String text = msg.getText();
						if (text != null) {
							String myChannel = channel;
							if (msg.getNames() != null && msg.getNames().length > 0) {
								myChannel = msg.getNames()[0];
							}

							this.condition.update(new SimpleEvent(this, myChannel, null, text)
									.withType(NodeProxyBroker.EVENT_MESSAGE));
						}
					}
				}
			} catch (Exception e) {

			}
		}
	}

	public ReaderComm withCondition(ObjectCondition condition) {
		this.condition = condition;
		return this;
	}

	public ReaderComm withChannel(String name) {
		this.channel = name;
		return this;
	}
}
