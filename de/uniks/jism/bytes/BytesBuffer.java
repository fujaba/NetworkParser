package de.uniks.jism.bytes;

import de.uniks.jism.Buffer;

public class BytesBuffer implements BufferedBytes {
	/** The buffer. */
	protected byte[] buffer;

	/** The index. */
	protected int index;
	
	@Override
	public int length() {
		return buffer.length;
	}

	@Override
	public char charAt(int index) {
		return (char) buffer[index];
	}
	
	@Override
	public byte byteAt(int index) {
		return buffer[index];
	}

	@Override
	public String substring(int startTag, int length) {
		byte[] sub=new byte[length];
		for(int i=0;i<length;i++){
			sub[i]=buffer[startTag+length];
		}
		return new String(sub);
	}

	@Override
	public Buffer withLength(int length) {
		this.buffer=new byte[length];
		return this;
	}

	@Override
	public int position() {
		return index;
	}

	@Override
	public int remaining() {
		return buffer.length - index;
	}

	@Override
	public void back() {
		if(index>0){
			index--;
		}
	}

	@Override
	public boolean isEnd() {
		return buffer.length <= index;
	}

	@Override
	public Buffer withPosition(int value) {
		this.index = value;
		return this;
	}

	@Override
	public String toText() {
		return new String(buffer);
	}

	@Override
	public byte[] toArray() {
		return buffer;
	}

	@Override
	public byte getByte() {
		return this.buffer[index++];
	}

	
	private byte[] converter(int bits){
		int len = bits/8;
		byte[] buffer=new byte[len];
		
		for(int i=0;i<len;i++){
			buffer[i] = this.buffer[index++];
		}
		return buffer;
	}
	
	@Override
	public char getChar() {
		byte[] bytes= converter(Character.SIZE);
		return (char) ((bytes[0]<<8)+bytes[1]);
	}

	@Override
	public short getShort() {
		byte[] bytes= converter(Short.SIZE);
		return (short) ((bytes[0]<<8)+bytes[1]);
	}

	@Override
	public long getLong() {
		byte[] bytes= converter(Long.SIZE);
		return (long) (
					(bytes[0]<<56)+
					(bytes[1]<<48)+
					(bytes[2]<<40)+
					(bytes[3]<<32)+
					(bytes[4]<<24)+
					(bytes[5]<<16)+
					(bytes[6]<<8)+
					bytes[7]);
	}

	@Override
	public int getInt() {
		byte[] bytes= converter(Integer.SIZE);
		return (int) (
				(bytes[0]<<24)+
				(bytes[1]<<16)+
				(bytes[2]<<8)+
				bytes[3]);
	}

	@Override
	public float getFloat() {
		int asInt = getInt();
		return Float.intBitsToFloat(asInt);
	}

	@Override
	public double getDouble() {
		long asLong=getLong();
		return Double.longBitsToDouble(asLong);
	}

	public byte[] getValue(int len) {
		byte[] array = new byte[len];
		for(int i=0;i<len;i++){
			array[i]=getByte();
		}
		return array;
	}
	
	
	@Override
	public byte[] getValue(int start, int len) {
		this.withPosition(start);

		byte[] array = new byte[len];
		for(int i=0;i<len;i++){
			array[i]=getByte();
		}
		return array;
	}

	@Override
	public byte[] array() {
		return buffer;
	}

	@Override
	public void put(byte value) {
		this.buffer[index++] = value;
	}
	
	@Override
	public void put(short value) {
		this.buffer[index++] = (byte) (value & 0xff<<8);
		this.buffer[index++] = (byte) (value & 0xff);
	}
	
	@Override
	public void put(int value) {
		this.buffer[index++] = (byte) (value & 0xff<<24);
		this.buffer[index++] = (byte) (value & 0xff<<16);
		this.buffer[index++] = (byte) (value & 0xff<<8);
		this.buffer[index++] = (byte) (value & 0xff);
	}

	@Override
	public void put(long value) {
		this.buffer[index++] = (byte) (value & 0xff<<56);
		this.buffer[index++] = (byte) (value & 0xff<<48);
		this.buffer[index++] = (byte) (value & 0xff<<40);
		this.buffer[index++] = (byte) (value & 0xff<<32);
		this.buffer[index++] = (byte) (value & 0xff<<24);
		this.buffer[index++] = (byte) (value & 0xff<<16);
		this.buffer[index++] = (byte) (value & 0xff<<8);
		this.buffer[index++] = (byte) (value & 0xff);
	}
	

	@Override
	public void put(byte[] value) {
		for(int i=0;i<value.length;i++){
			put(value[i]);
		}
	}

	@Override
	public void put(byte[] value, int offset, int length) {
		for(int i=0;i<length;i++){
			put(value[offset+i]);
		}
	}

	@Override
	public void flip() {
		this.index=0;
	}

	@Override
	public BufferedBytes getNewBuffer(int capacity) {
		new BytesBuffer().withLength(capacity);
		return this;
	}

	@Override
	public BufferedBytes getNewBuffer(byte[] array) {
		return new BytesBuffer().withValue(array);
	}
	
	public BytesBuffer withValue(byte[] array){
		this.buffer=array;
		return this;
	}
}
