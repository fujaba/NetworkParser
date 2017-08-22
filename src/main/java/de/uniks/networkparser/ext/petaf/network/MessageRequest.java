package de.uniks.networkparser.ext.petaf.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class MessageRequest implements Runnable{
		public static int BUFFER = 100 * 1024;
		private final Socket requestSocket;
		private NodeProxy proxy;
//		private HashMap<String, Message> messages = new HashMap<String, Message>();
//
		public MessageRequest(NodeProxy proxy, Socket requestSocket) {
			this.requestSocket = requestSocket;
			this.proxy = proxy;
		}
		
		public void run() {
			try {
				InputStream is = requestSocket.getInputStream();
				StringBuffer message = new StringBuffer();
				byte[] messageArray = new byte[BUFFER];
				while (true) {
					int bytesRead = is.read(messageArray, 0, BUFFER);
					if (bytesRead <= 0)
						break; // <======= no more data
					message.append(new String(messageArray, 0, bytesRead, Charset.forName("UTF-8")));
				}
				requestSocket.close();
				System.err.println("Message: " + message.toString());
				handleMsg(message.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void handleMsg(String msg) {
			
		}

//			init();
//
//		public void init() {
//			// Add all Messages
////			addMessageTask(new AcceptTaskReceive());
////			addMessageTask(new BroadCastReceive());
////			addMessageTask(new ChatMessageReceive());
////			addMessageTask(new CommandTaskReceive());
////			addMessageTask(new ConnectTaskReceive());
////			addMessageTask(new FileRequestTaskReceive());
////			addMessageTask(new FileTransferTaskReceive());
//			addMessageTask(new GetObjectMessage());
//			addMessageTask(new JsonChangeMessage());
//			addMessageTask(new PingMessage());
////			addMessageTask(new TransferTaskReceive());
//		}
//
//		public void addMessageTask(Message task) {
//			this.messages.put(task.getTyp(), task);
//		}
//
//
//		public static long lastTimeStamp = 0;
//		
//		public void handleMsg(String msg, World node) throws Exception {
//			try {
//				JsonObject jsonObject = new JsonObject().withValue(msg);
//				String msgId = jsonObject.getString(Message.MSGTYP);
////				node.setMessage(new LogItem(msg, LogItem.INCOMING).setCategorie((String)jsonObject.get(NodeProxy.PROPERTY_URL)));
//				MessageReceive messageTask = messages.get(msgId);
//				if (messageTask != null) {
//					MessageReceive task = messageTask.getNewInstance(node, jsonObject);
//					long now = System.currentTimeMillis();
//					long delay = lastTimeStamp - now;
//					if (delay >= 0)
//					{
//					   lastTimeStamp++;
//					   delay++;
//					}
//					else
//					{
//					   delay = 0;
//					   lastTimeStamp = now;
//					}
//					
//					node.scheduleTask(task, (int) delay);
//				} else {
//					throw new RuntimeException("not implemented: Unknown Message: "
//							+ msgId);
//				}
//			} catch (Exception e) {
//			   node.addMessage(new LogItem("erroneous message ignored: "+e.getMessage()+"\n" + msg, LogItem.ERROR));
//			}
//		}
//
	}
