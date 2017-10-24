package de.uniks.networkparser.ext.petaf;

import java.net.Socket;

import de.uniks.networkparser.ext.petaf.proxy.NodeProxyTCP;

public class MessageRequest implements Runnable {
		public static final int BUFFER = 100 * 1024;
		private final Socket requestSocket;
		private final NodeProxyTCP proxy; 

		public MessageRequest(NodeProxyTCP proxy, Socket requestSocket) {
			this.requestSocket = requestSocket;
			this.proxy = proxy;
		}
		
 		public void run() {
			try {
				proxy.readFromInputStream(requestSocket);
			} catch (Exception e) {
				Space space = proxy.getSpace();
				if(space!=null) {
					space.handleException(e);
				}
			}
		}
		
		public static void executeTask(NodeProxyTCP proxy, Socket requestSocket) {
			MessageRequest task = new MessageRequest(proxy, requestSocket);
			TaskExecutor executor = proxy.getExecutor();
			if(executor != null) {
				executor.executeTask(task, 0);
			}
		}
	}
