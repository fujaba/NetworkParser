package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.ext.ErrorHandler;
import de.uniks.networkparser.json.JsonObject;

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
	
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (attribute == null || entity instanceof ReceivingTimerTask == false) {
			return false;
		}
		ReceivingTimerTask msg = (ReceivingTimerTask) entity;
		Space space = msg.getSpace();
		if(space != null && PROPERTY_RECEIVED.equalsIgnoreCase(attribute)) {
			if (value instanceof NodeProxy) {
				// Find original Proxy
				NodeProxy proxy = (NodeProxy) value;
//				proxy.getI
				msg.withAddToReceived((NodeProxy) value);
			}
			return true;
		}
		return super.setValue(entity, attribute, value, type);
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
