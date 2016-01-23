package de.uniks.networkparser.buffer;
/*
NetworkParser
Copyright (c) 2011 - 2015, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.
*/

public class ByteBuffer extends BufferedBuffer {
	/** The buffer. */
	protected byte[] buffer;

	@Override
	public char charAt(int index) {
		return (char) buffer[index];
	}
	
	@Override
	public byte byteAt(int index) {
		return buffer[index];
	}

	@Override
	public CharacterBuffer subSequence(int start, int length) {
		byte[] sub = new byte[length];
		for (int i = 0; i < length; i++) {
			sub[i] = buffer[start + length];
		}
		return new CharacterBuffer().with(sub);
	}

	@Override
	public ByteBuffer withLength(int length) {
		this.buffer = new byte[length];
		this.length = length;
		return this;
	}

	@Override
	public byte[] toArray() {
		return buffer;
	}

	public byte getByte() {
		if(this.buffer != null) {
			return this.buffer[position++];
		}
		return 0;
	}

	private byte[] converter(int bits) {
		int len = bits / 8;
		byte[] buffer = new byte[len];

		for (int i = 0; i < len; i++) {
			buffer[i] = this.buffer[position++];
		}
		return buffer;
	}

	@Override
	public char getChar() {
		byte[] bytes = converter(Character.SIZE);
		char result = (char) bytes[0];
		result = (char) (result << 8 + (char) bytes[1]);
		return result;
	}

	public short getShort() {
		byte[] bytes = converter(Short.SIZE);
		short result = bytes[0];
		result = (short) (result << 8 + bytes[1]);
		return result;
	}

	public long getLong() {
		byte[] bytes = converter(Long.SIZE);
		long result = bytes[0];
		result = result << 8 + bytes[1];
		result = result << 8 + bytes[2];
		result = result << 8 + bytes[3];
		result = result << 8 + bytes[4];
		result = result << 8 + bytes[5];
		result = result << 8 + bytes[6];
		result = result << 8 + bytes[7];
		return result;
	}

	public int getInt() {
		byte[] bytes = converter(Integer.SIZE);
		return (int) ((bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3]);
	}

	public float getFloat() {
		int asInt = getInt();
		return Float.intBitsToFloat(asInt);
	}

	public double getDouble() {
		long asLong = getLong();
		return Double.longBitsToDouble(asLong);
	}

	public byte[] getValue(int len) {
		byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = getByte();
		}
		return array;
	}

	public byte[] getValue(int start, int len) {
		this.withPosition(start);

		byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = getByte();
		}
		return array;
	}

	public byte[] array() {
		return buffer;
	}

	public void put(byte value) {
		if(this.buffer != null) {
			this.buffer[position++] = value;
		}
	}

	public void put(short value) {
		if(this.buffer != null) {
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
		}
	}

	public void put(int value) {
		if(this.buffer != null) {
			this.buffer[position++] = (byte) (value >>> 24);
			this.buffer[position++] = (byte) (value >>> 16);
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
		}
	}

	public void put(long value) {
		if(this.buffer != null) {
			this.buffer[position++] = (byte) (value >>> 56);
			this.buffer[position++] = (byte) (value >>> 48);
			this.buffer[position++] = (byte) (value >>> 40);
			this.buffer[position++] = (byte) (value >>> 32);
			this.buffer[position++] = (byte) (value >>> 24);
			this.buffer[position++] = (byte) (value >>> 16);
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
		}
	}

	public void put(char value) {
		if(this.buffer!=null) {
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
		}
	}

	public void put(float value) {
		int bits = Float.floatToIntBits(value);
		if(this.buffer != null) {
			this.buffer[position++] = (byte) (bits & 0xff);
			this.buffer[position++] = (byte) ((bits >> 8) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 16) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 24) & 0xff);
		}
	}

	public void put(double value) {
		long bits = Double.doubleToLongBits(value);
		if(this.buffer != null) {
			this.buffer[position++] = (byte) ((bits >> 56) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 48) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 40) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 32) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 24) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 16) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 8) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 0) & 0xff);
		}
	}

	public void put(byte[] value) {
		if(value!=null) {
			for (int i = 0; i < value.length; i++) {
				put(value[i]);
			}
		}
	}

	public void put(byte[] value, int offset, int length) {
		for (int i = 0; i < length; i++) {
			put(value[offset + i]);
		}
	}

	public byte[] flip() {
		this.position = 0;
		return buffer;
	}

	public ByteBuffer getNewBuffer(int capacity) {
		return new ByteBuffer().withLength(capacity);
	}

	public static ByteBuffer allocate(int len) {
		ByteBuffer bytesBuffer = new ByteBuffer();
		bytesBuffer.withLength(len);
		return bytesBuffer;
	}

	public ByteBuffer getNewBuffer(byte[] array) {
		return new ByteBuffer().with(array);
	}

	public ByteBuffer with(byte[] array) {
		this.buffer = array;
		this.position = 0;
		this.length = array.length;
		return this;
	}
	
	@Override
	public String toString() {
		return String.valueOf(buffer);
	}
}
