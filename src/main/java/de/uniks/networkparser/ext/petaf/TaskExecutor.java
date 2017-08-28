package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.interfaces.ObjectCondition;

public interface TaskExecutor {
	public Object execute(Runnable task);
	public TaskExecutor withListener(ObjectCondition condition);
	public boolean handleMsg(Message message);
	public void shutdown();
}
