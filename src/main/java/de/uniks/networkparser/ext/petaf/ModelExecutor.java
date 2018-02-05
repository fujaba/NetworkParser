package de.uniks.networkparser.ext.petaf;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;

public class ModelExecutor extends SimpleEventCondition implements Runnable {
	private SimpleEvent event;
	@Override
	public boolean update(SimpleEvent event) {
		this.event = event;
		
		return true;
	}

	@Override
	public void run() {
		Filter filter = (Filter) event.getNewValue();
		UpdateListener listener = (UpdateListener) event.getSource();
		JsonObject change = (JsonObject) event.getEntity();
		
		final FutureTask<Object> query = new FutureTask<Object>(new Callable<Object>() {
		    @Override
		    public Object call() {
		    	return listener.execute(change, filter);
		    }
		});
		
		ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, Runnable.class, query);
		
		try {
			event.withModelValue(query.get());
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
	}

}
