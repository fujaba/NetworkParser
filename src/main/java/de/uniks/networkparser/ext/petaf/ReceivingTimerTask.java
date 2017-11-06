package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.ext.ErrorHandler;

public abstract class ReceivingTimerTask extends Message implements Runnable {
	protected final ErrorHandler handler = new ErrorHandler();
	protected Runnable task;
	protected Space space;
	
	
	public ReceivingTimerTask withSpace(Space space){
		handler.addListener(space);
		this.space = space;
		return this;
	}
	
	@Override
	public void run() {
         try{
         	runTask();
         }catch(Exception e){
       		 handler.saveException(e, false);
         }
	}
	
	public Space getSpace() {
		return space;
	}

	public boolean runTask() throws Exception {
		if(this.task != null) {
			task.run();
			return true;
		}
		return false;
	}
}
