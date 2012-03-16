package de.uni.kassel.peermessage.interfaces;

import de.uni.kassel.peermessage.json.JsonObject;

public interface UpdateListener {
	public void sendUpdateMsg(JsonObject jsonObject);
}
