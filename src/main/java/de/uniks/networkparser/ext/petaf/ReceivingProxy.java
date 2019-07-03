package de.uniks.networkparser.ext.petaf;

import java.util.Set;
import java.util.concurrent.Future;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

/**
 * Created by alexw on 06.10.2016.
 * 
 * @author alexw
 */
abstract class ReceivingProxy implements Runnable {

	protected Future<?> future = null;

	protected boolean handleMessage(String messageContent) {
		if (messageContent == null || "".equals(messageContent)) {
			return true;
		}
		return false;
	}

	protected abstract boolean lookForMessage();

	@Override
	public void run() {
		try {
			while (Thread.interrupted() == false) {
				lookForMessage();
			}
		} catch (Exception e) {
		}
	}

	protected Object recreateMessage(Space space, JsonObject jsonObject) {
		IdMap idMap = space.getMap();
		String className = jsonObject.getString("class");
		SendableEntityCreator creator = idMap.getCreator(className, true);
		if (creator == null) {
			return null;
		}
		Object object = creator.getSendableInstance(false);
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			creator.setValue(object, key, jsonObject.getValue(key), IdMap.VALUE);
		}
		return object;
	}
}
