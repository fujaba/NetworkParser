package de.uni.kassel.peermessage.interfaces;

import de.uni.kassel.peermessage.json.JsonObject;

public interface MapUpdateListener {
	public boolean sendUpdateMsg(JsonObject jsonObject);
}
