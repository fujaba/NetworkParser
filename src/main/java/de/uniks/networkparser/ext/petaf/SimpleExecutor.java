package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class SimpleExecutor implements TaskExecutor{
	private ObjectCondition listener;

	@Override
	public Object execute(Runnable task) {
		task.run();
		return null;
	}

	@Override
	public boolean handleMsg(Message message) {
		return this.listener.update(message);
	}

	@Override
	public SimpleExecutor withListener(ObjectCondition condition) {
		this.listener = condition;
		return this;
	}
}
