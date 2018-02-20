package de.uniks.networkparser.ext.petaf;


import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;

public class ModelExecutor extends SimpleEventCondition {

	private Object execute(SimpleEvent event) {
		final IdMap map = (IdMap) event.getSource();
		final JsonObject change = (JsonObject) event.getEntity();
		MapListener mapListener = map.getMapListener();
		if(mapListener instanceof UpdateListener) {
			UpdateListener listener = (UpdateListener) mapListener;
			Object result = listener.execute(change, map.getFilter());
			if (result != null) {
				return result;
			}
		}
		MapEntity mapEntry = (MapEntity) event.getOldValue();
		JsonTokener jsonTokener = (JsonTokener) event.getNewValue();
		return jsonTokener.decoding(change, mapEntry, false);
	}


	@Override
	public boolean update(final SimpleEvent event) {
		Object thread = ReflectionLoader.callChain(ReflectionLoader.TOOLKIT, "getFxUserThread", "getFxUserThread");
		if(thread == null) {
			Object result = this.execute(event);
			event.withModelValue(result);
			return result != null;
		}
		final FutureTask<Object> query = new FutureTask<Object>(new Callable<Object>() {
			@Override
			public Object call() {
				return execute(event);
			}
		});
		ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, Runnable.class, query);
		try {
			event.withModelValue(query.get());
		} catch (Exception e) {
		}
		return true;
	}
}
