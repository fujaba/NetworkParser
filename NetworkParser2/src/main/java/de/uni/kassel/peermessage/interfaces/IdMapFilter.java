package de.uni.kassel.peermessage.interfaces;

import de.uni.kassel.peermessage.IdMap;

public interface IdMapFilter {
	public boolean isConvertable(IdMap map, Object entity, String property, Object value);
}
