package de.uniks.networkparser.ext.petaf;

import java.util.TimerTask;
import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.ext.ErrorHandler;

public class SimpleTimerTask extends TimerTask {
	protected final ErrorHandler handler = new ErrorHandler();
	protected Runnable task;
	protected Space space;
	private DateTimeEntity lastRun;

	public SimpleTimerTask(Space space){
		handler.addListener(space);
		this.space = space;
	}
	
	public SimpleTimerTask withDateTime(DateTimeEntity entity) {
		this.lastRun = entity;
		return this;
	}
	
	
	public DateTimeEntity getLastRun() {
		return lastRun;
	}
	
	@Override
	public void run() {
         try{
			if (lastRun != null) {
				lastRun.withValue(System.currentTimeMillis());
			}
			if(space != null) {
				space.withLastTimerRun(lastRun);
			}
         	runTask();
         }catch(Exception e){
       		 handler.saveException(e);
         }
	}

	public boolean runTask() throws Exception {
		if(this.task != null) {
			task.run();
			return true;
		}
		return false;
	}
	public SimpleTimerTask withTask(Runnable task) {
		this.task = task;
		return this;
	}
	
	public Space getSpace() {
		return space;
	}
}
