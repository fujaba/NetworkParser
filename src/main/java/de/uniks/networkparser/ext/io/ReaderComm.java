package de.uniks.networkparser.ext.io;

import de.uniks.networkparser.buffer.BufferedBuffer;

public class ReaderComm implements Runnable {
	private boolean running = false;
	private String threadName;
	private MessageSession session;

	public void start(String threadName) {
		this.threadName = threadName;
		if (!running) {
			running = true;
		}
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
				BufferedBuffer response = session.getResponse();
				System.out.println(response.toString());
			}catch (Exception e) {
			
			}}
	}
}
