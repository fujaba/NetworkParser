package de.uniks.networkparser.ext.petaf;

public class SendingTimerTask extends SimpleTimerTask {
	private NodeProxy sender;
	public SendingTimerTask(Space space) {
		super(space);
	}
	public SendingTimerTask withSender(NodeProxy sender) {
		this.sender = sender;
		return this;
	}

	public NodeProxy getSender() {
		return sender;
	}
	public Message getMessage() {
		return null;
	}

	@Override
	public boolean runTask() throws Exception {
		if(super.runTask()) {
			return true;
		}
		Message message = getMessage();
		if(message == null) {
			return false;
		}
		if(sender != null) {
			getSpace().sendMessage(message, false, sender);
		} else {
			getSpace().sendMessageToPeers(message);
		}
		return false;
	}
}
