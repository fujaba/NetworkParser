package de.uniks.jism.interfaces;

import java.nio.ByteBuffer;

import de.uniks.jism.bytes.ByteConverter;

public interface ByteItem {
	public String toString();
	public String toString(ByteConverter converter);
	public ByteBuffer getBytes(boolean isDynamic);
	public int calcLength(boolean isDynamic);
}
