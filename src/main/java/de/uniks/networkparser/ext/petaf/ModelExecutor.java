package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.concurrent.Callable;
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
	private Object execute(SimpleEvent event) {
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
		final FutureTask<Object> query = new FutureTask<Object>(new Callable<Object>() {
			@Override
			public Object call() {
				return execute(event);
			}
		});
		JavaAdapter.execute(query);
		try {
			event.withModelValue(query.get());
		} catch (Exception e) {
		}
		return true;
	}
}
