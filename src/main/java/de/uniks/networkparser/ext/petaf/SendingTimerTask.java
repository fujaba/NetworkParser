package de.uniks.networkparser.ext.petaf;

public class SendingTimerTask extends SimpleTimerTask{
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
		if(sender != null) {
			getSpace().sendMessage(sender, getMessage());
		} else {
			getSpace().sendMessageToPeers(getMessage());
		}
		return false;
	}
}
