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

import java.nio.charset.Charset;

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

	public ByteBuffer withBufferLength(int length) {
		super.withLength(length);
		this.buffer = new byte[length];
		return this;
	}

	@Override
	public char getChar() {
		char result = (char) getByte();
		result = (char) (result << 8 + (char) getByte());
		return result;
	}

	@Override
	public byte getByte() {
		return this.buffer[++position];
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
	
	public void write(byte[] bytes, int len) {
		int length=this.length;
		int bufferLen =0;
		if(this.buffer != null) {
			bufferLen = this.buffer.length;
		}
		if(length + len > bufferLen) {
			this.buffer = new byte[length + len];
			if(bufferLen >0) {
				byte[] oldBuffer = this.buffer;
				System.arraycopy(oldBuffer, 0, this.buffer, 0, length);
			}
			System.arraycopy(bytes, 0, this.buffer, length, len);
			
		} else {
			System.arraycopy(bytes, 0, this.buffer, length + 1, len);
		}
		this.length += len;
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

	public ByteBuffer flip(boolean preFirst) {
		if (preFirst) {
			this.position = -1;
			return this;
		}
		this.position = 0;
		return this;
	}

	public ByteBuffer getNewBuffer(int capacity) {
		return new ByteBuffer().withBufferLength(capacity);
	}

	public static ByteBuffer allocate(int len) {
		ByteBuffer bytesBuffer = new ByteBuffer();
		bytesBuffer.withBufferLength(len);
		return bytesBuffer;
	}

	public ByteBuffer getNewBuffer(byte[] array) {
		return new ByteBuffer().with(array);
	}
	public ByteBuffer with(String string) {
		this.buffer = string.getBytes(Charset.forName("UTF-8"));
		this.position = 0;
		this.length = buffer.length;
		return this;
	}

	public ByteBuffer with(byte[] array) {
		this.buffer = array;
		this.position = 0;
		this.length = array.length;
		return this;
	}

	public ByteBuffer with(byte value) {
		if(this.buffer == null) {
			this.buffer = new byte[]{value};
			this.length =1;
			this.position =0;
		}else if(this.length<this.buffer.length) {
			this.buffer[length++] = value;
		}
		return this;
	}

	@Override
	public String toString() {
		return String.valueOf(buffer);
	}
}
