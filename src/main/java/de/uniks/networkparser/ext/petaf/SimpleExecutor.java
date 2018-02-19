package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.DateTimeEntity;

public class SimpleExecutor implements TaskExecutor{
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
			if(space != null) {
				space.handleException(e);
			}
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
			if(space != null) {
				space.handleException(e);
			}
		}
		return null;
	}

	@Override
	public boolean handleMsg(Message message) {
		if(space != null) {
			return space.handleMsg(message);
		}
		return false;
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
