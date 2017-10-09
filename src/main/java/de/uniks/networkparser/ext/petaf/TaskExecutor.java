package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.interfaces.ObjectCondition;

public interface TaskExecutor {
	public Object executeTask(Runnable task, int delay, int interval);
	public Object executeTask(Runnable task, int delay);
	public TaskExecutor withListener(ObjectCondition condition);
	public boolean handleMsg(Message message);
	public void shutdown();
}
