package de.uniks.jism.logic;

import de.uniks.jism.Buffer;
import de.uniks.jism.IdMap;

public class False implements Condition {

	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		return false;
	}

	@Override
	public boolean matches(Buffer buffer) {
		return false;
	}

}
