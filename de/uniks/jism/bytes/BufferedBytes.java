package de.uniks.jism.bytes;

import de.uniks.jism.Buffer;

public interface BufferedBytes extends Buffer{
	public byte getByte();
	
	public short getShort();
	
	public long getLong();

	public int getInt();
	
	public float getFloat();
	
	public double getDouble();
	
	public byte[] getValue(int len);

	public byte[] getValue(int start, int len);
	
	public byte[] array();

	public void put(byte value);
	
	public void put(short value);
	
	public void put(int value);
	
	public void put(long value);
		
	public void put(byte[] value);
	
	public void put(byte[] value, int offset, int length);
	
	public void flip();
	
	public BufferedBytes getNewBuffer(int capacity);
	
	public BufferedBytes getNewBuffer(byte[] array);
	
	
}
