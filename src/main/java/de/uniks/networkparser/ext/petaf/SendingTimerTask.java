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

/**
 * Task for Sending Message.
 *
 * @author Stefan Lindel
 */
public class SendingTimerTask extends SimpleTimerTask {
	private NodeProxy sender;

	/**
	 * Instantiates a new sending timer task.
	 *
	 * @param space the space
	 */
	public SendingTimerTask(Space space) {
		super(space);
	}

	/**
	 * With sender.
	 *
	 * @param sender the sender
	 * @return the sending timer task
	 */
	public SendingTimerTask withSender(NodeProxy sender) {
		this.sender = sender;
		return this;
	}

	/**
	 * Gets the sender.
	 *
	 * @return the sender
	 */
	public NodeProxy getSender() {
		return sender;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public Message getMessage() {
		return null;
	}

	/**
	 * Run task.
	 *
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean runTask() throws Exception {
		if (super.runTask()) {
			return true;
		}
		Message message = getMessage();
		if (message == null) {
			return false;
		}
		if (sender != null) {
			getSpace().sendMessage(message, false, sender);
		} else {
			getSpace().sendMessageToPeers(message);
		}
		return false;
	}
}
