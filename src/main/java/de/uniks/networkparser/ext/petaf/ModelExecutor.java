package de.uniks.networkparser.ext.petaf;

import java.util.concurrent.FutureTask;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.JavaAdapter;
import de.uniks.networkparser.interfaces.MapListener;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;

public class ModelExecutor extends SimpleEventCondition {
	public Object execute(SimpleEvent event) {
		final IdMap map = (IdMap) event.getSource();
		final JsonObject change = (JsonObject) event.getEntity();
		MapListener mapListener = map.getMapListener();
		if (mapListener instanceof UpdateListener) {
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
		Object thread = ReflectionLoader.callChain(ReflectionLoader.TOOLKITFX, "getFxUserThread", "getFxUserThread");
		if (thread == null) {
			Object result = this.execute(event);
			event.withModelValue(result);
			return result != null;
		}
		SimpleTimerTask task = new SimpleTimerTask(event, this);

		final FutureTask<Object> query = new FutureTask<Object>(task);
		JavaAdapter.execute(query);
		try {
			event.withModelValue(query.get());
		} catch (Exception e) {
		}
		return true;
	}
}
