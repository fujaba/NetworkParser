package de.uniks.jism.bytes;

import de.uniks.jism.IdMapFilter;

public class ByteFilter extends IdMapFilter{
	private ByteIdMap map;

	public ByteFilter(ByteIdMap map){
		this.map=map;
	}

	public ByteIdMap getMap() {
		return this.map;
	}
}
