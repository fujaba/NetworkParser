package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SimpleExecutor implements TaskExecutor{
	protected final ErrorHandler handler = new ErrorHandler();
	private ObjectCondition listener;
	private DateTimeEntity lastRun=new DateTimeEntity();
	private Space space;

	@Override
	public Object executeTask(Runnable task, int delay, int interval) {
		try {
			this.lastRun.withValue(System.currentTimeMillis());
			if(task != null) {
				task.run();
			}
		} catch (Exception e) {
			 handler.saveException(e);
		}
		return null;
	}
	
	@Override
	public Object executeTask(Runnable task, int delay) {
		try {
			this.lastRun.withValue(System.currentTimeMillis());
			if(task != null) {
				task.run();
			}
		} catch (Exception e) {
			 handler.saveException(e);
		}
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

	@Override
	public void shutdown() {
	}

	@Override
	public SimpleExecutor withSpace(Space space) {
		this.space = space;
		return this;
	}

	@Override
	public Space getSpace() {
		return space;
	}

	@Override
	public DateTimeEntity getLastRun() {
		return lastRun;
	}
}
