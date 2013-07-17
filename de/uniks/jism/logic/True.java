package de.uniks.jism.logic;

import de.uniks.jism.Buffer;
import de.uniks.jism.IdMap;

public class True implements Condition {

	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		return true;
	}

	@Override
	public boolean matches(Buffer buffer) {
		return true;
	}

}
