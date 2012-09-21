package de.uni.kassel.peermessage.bytes;

import de.uni.kassel.peermessage.IdMapFilter;

public class ByteFilter extends IdMapFilter{
	private ByteIdMap map;

	public ByteFilter(ByteIdMap map){
		this.map=map;
	}

	public ByteIdMap getMap() {
		return this.map;
	}
}
