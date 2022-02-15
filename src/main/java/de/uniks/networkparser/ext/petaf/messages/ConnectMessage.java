package de.uniks.networkparser.ext.petaf.messages;

import de.uniks.networkparser.ext.petaf.NodeProxy;
import de.uniks.networkparser.ext.petaf.ReceivingTimerTask;

/**
 * Sending Connection Link with all Input Proxies and Filter.
 *
 * @author Stefan Lindel
 */
public class ConnectMessage extends ReceivingTimerTask {
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "connect";

	/**
	 * Creates the.
	 *
	 * @return the connect message
	 */
	public static ConnectMessage create() {
		ConnectMessage msg = new ConnectMessage();
		msg.withSendAnyHow(true);
		return msg;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ConnectMessage();
	}

	/**
	 * Run task.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean runTask() {
		if (super.runTask()) {
			return true;
		}

		AcceptMessage acceptTaskSend = AcceptMessage.create();
		NodeProxy sender = this.getReceiver();
		if (sender != null) {
			if (sender.sendMessage(acceptTaskSend)) {
				this.getReceiver().withOnline(true);
			}
		}
		return true;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public String getType() {
		return PROPERTY_TYPE;
	}

	/**
	 * Checks if is sending to peers.
	 *
	 * @return true, if is sending to peers
	 */
	@Override
	public boolean isSendingToPeers() {
		return false;
	}
}