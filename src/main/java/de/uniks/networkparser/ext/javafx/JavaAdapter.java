package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import netscape.javascript.JSObject;

public class JavaAdapter implements UpdateListener{
	private JavaBridge owner;
	
	public JavaAdapter(JavaBridge owner) {
		this.owner = owner;
	}
	
	public boolean executeChange(String value) {
		owner.setApplyingChangeMSG(true);
		JsonObject json = JsonObject.create(value);
		IdMap map = owner.getMap();
		Object encode = map.decode(json);
		if (encode == null) {
			SimpleObject newItem = SimpleObject.create(json);
			map.put(newItem.getId(), newItem);
		}
		owner.setApplyingChangeMSG(false);
		return true;
	}

	@Override
	public boolean update(Object value) {
		if(value instanceof String) {
			JsonObject data = new JsonObject().withValue(""+value);
			owner.fireEvent(data);
			return true;
		} else if(value instanceof JSObject){
			EventFX event = EventFX.create((JSObject) value);
			owner.fireEvent(event);
			return true;
		}
		return false;
	}
}
