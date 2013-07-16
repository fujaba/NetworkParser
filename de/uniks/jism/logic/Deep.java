package de.uniks.jism.logic;

import de.uniks.jism.Buffer;
import de.uniks.jism.IdMap;

public class Deep implements Condition{
	private int deep;
	
	public Deep withDeep(int deep){
		this.deep = deep;
		return this;
	}
	
	@Override
	public boolean matches(IdMap map, Object entity, String property,
			Object value, boolean isMany, int deep) {
		return deep<=this.deep;
	}

	@Override
	public boolean matches(Buffer buffer) {
		return deep<=this.deep;
	}

}
