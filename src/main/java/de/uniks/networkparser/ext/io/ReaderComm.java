package de.uniks.networkparser.ext.io;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class ReaderComm implements Runnable {
	private boolean running = false;
	private String threadName;
	private MessageSession session;
	private String channel;
	private ObjectCondition condition;

	public void start(String threadName) {
		this.threadName = threadName;
		if (!running) {
			running = true;
		}
	}
	
	public ReaderComm withSession(MessageSession session) {
		this.session = session;
		return this;
	}

	/**
	 * Stops the Receiver's thread.  This call will block.
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
		if(session == null) {
			return;
		}
		while (running && (session.isClose() == false)) {
			try {
				Object response = session.getServerResponse();
				
				// Answer
				if(condition != null) {
					if(response instanceof RabbitMessage) {
						RabbitMessage msg = (RabbitMessage) response;
						String text = msg.getText();
						if(text != null) {
							this.condition.update(new SimpleEvent(this, channel, null, text));
						}
					} else {
						this.condition.update(new SimpleEvent(this, channel, null, response));
					}
				}
			}catch (Exception e) {
			
			}}
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
