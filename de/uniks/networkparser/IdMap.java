package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseEntity;

public abstract class IdMap extends IdMapEncoder{
	public abstract Object decode(BaseEntity value);
	public abstract Object decode(String value);
}
