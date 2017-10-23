package de.uniks.networkparser.ext.petaf;

import java.util.Timer;

import de.uniks.networkparser.DateTimeEntity;

public class TimerExecutor extends Timer implements TaskExecutor {
	private boolean isCancel;
	private Space space;
	private DateTimeEntity lastRun=new DateTimeEntity();
	
	
	public TimerExecutor withSpace(Space space) {
		this.space = space;
		return this;
	}
	
	@Override
	public Space getSpace() {
		return space;
	}
	
	public TimerExecutor(String value) {
		super(value);
	}
	@Override
	public void cancel() {
		this.isCancel=true;
		super.cancel();
	}
	public boolean isCancel(){
		return isCancel;
	}
	
	@Override
	public Object executeTask(Runnable task, int delay, int interval) {
		if(isCancel()){
			return null;
		}
		SimpleTimerTask newTask;
		if(task instanceof SimpleTimerTask) {
			newTask = (SimpleTimerTask) task;
		} else {
			newTask = new SimpleTimerTask(space).withTask(task);
		}
		newTask.withDateTime(lastRun);
		if(interval>0){
			schedule(newTask, delay, interval);
		}else{
			schedule(newTask, delay);
		}
		return null;
	}
	@Override
	public Object executeTask(Runnable task, int delay) {
		if(isCancel()){
			return null;
		}
		SimpleTimerTask newTask;
		if(task instanceof SimpleTimerTask) {
			newTask = (SimpleTimerTask) task;
		} else {
			newTask = new SimpleTimerTask(space).withTask(task);
		}
		newTask.withDateTime(lastRun);
		schedule(newTask, delay);
		return null;
	}

	@Override
	public void shutdown() {
		this.isCancel = true;
		this.cancel();
	}

	@Override
	public DateTimeEntity getLastRun() {
		return lastRun;
	}

	@Override
	public boolean handleMsg(Message message) {
		if(space != null) {
			return space.handleMsg(message);
		}
		return false;
	}
}
