package de.uniks.jism.interfaces;

import java.nio.ByteBuffer;

public interface ByteItem {
	public String toString();
	public ByteBuffer getBytes(boolean isDynamic);
	public int calcLength(boolean isDynamic);
}
