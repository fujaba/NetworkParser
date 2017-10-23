package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.DateTimeEntity;

public interface TaskExecutor {
	public Object executeTask(Runnable task, int delay, int interval);
	public Object executeTask(Runnable task, int delay);
	public boolean handleMsg(Message message);
	public void shutdown();
	public TaskExecutor withSpace(Space space);
	public Space getSpace();
	public DateTimeEntity getLastRun();
}
