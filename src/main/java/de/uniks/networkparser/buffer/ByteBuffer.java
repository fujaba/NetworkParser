package de.uniks.networkparser.buffer;

import java.nio.charset.Charset;

import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.converter.ByteConverterHTTP;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.interfaces.Converter;

public class ByteBuffer extends BufferedBuffer {
	/** The buffer. */
	protected byte[] buffer;

	@Override
	public char charAt(int index) {
		if (index < 0 || buffer == null || index >= buffer.length) {
			return 0;
		}
		return (char) buffer[index];
	}

	@Override
	public byte byteAt(int index) {
		if (index < 0 || buffer == null || index >= buffer.length) {
			return 0;
		}
		return buffer[index];
	}

	@Override
	public CharacterBuffer subSequence(int start, int end) {
		int len = end - start;
		if(len < 0 || buffer == null) {
			return new CharacterBuffer();
		}
		byte[] sub = new byte[len];
		if (start < buffer.length && len <= buffer.length) {
			for (int i = 0; i < len; i++) {
				sub[i] = buffer[start + i];
			}
		}
		return new CharacterBuffer().with(sub);
	}

	public ByteBuffer withBufferLength(int length) {
		super.withLength(length);
		if(length>=0) {
			this.buffer = new byte[length];
		}
		return this;
	}

	@Override
	public char getChar() {
		int no = getByte();
		if (no >= 0) {
			return (char) no;
		}
		char result = (char) (no << 8 + (char) getByte());
		return result;
	}

	@Override
	public byte getByte() {
		if (buffer == null || position >= buffer.length - 1) {
			return 0;
		}
		return this.buffer[++position];
	}

	/**
	 * Get a Byte Field
	 * 
	 * @param parameter Null for full, Number Length, or new Byte Array
	 * @return a ByteArray
	 */
	public byte[] getBytes(Object... parameter) {
		if (buffer == null || position >= buffer.length - 1) {
			return new byte[0];
		}

		int len = length;
		byte[] result = null;
		if (parameter != null && parameter.length > 0) {
			if (parameter[0] instanceof Integer) {
				len = (Integer) parameter[0];
			} else if (parameter[0] instanceof Byte[] || parameter[0] instanceof byte[]) {
				result = (byte[]) parameter[0];
				len = result.length;
			}
		}
		if (result == null) {
			result = new byte[len];
		}
		for (int i = 0; i < len; i++) {
			result[i] = this.buffer[++position];
		}
		return result;
	}

	public byte[] getValue(int start, int len) {
		this.withPosition(start);
		if(len<0) {
			len = 0;
		}
		byte[] array = new byte[len];
		for (int i = 0; i < len; i++) {
			array[i] = getByte();
		}
		return array;
	}

	public byte[] array() {
		return buffer;
	}

	public boolean add(Object... values) {
		if (values == null) {
			return true;
		}
		for (Object item : values) {
			insert(item, true);
		}
		return true;
	}

	public boolean insert(Object item, boolean bufferAdEnd) {
		if (item instanceof Byte) {
			return addBytes(item, 1, bufferAdEnd);
		}
		if (item instanceof Character) {
			return addBytes((Character) item, 1, bufferAdEnd);
		}
		if (item instanceof String) {
			String str = (String) item;
			byte[] array = str.getBytes();
			return addBytes(array, array.length, bufferAdEnd);
		}
		if (item instanceof CharSequence) {
			CharSequence str = (CharSequence) item;
			resize(str.length(), bufferAdEnd);
			for (int i = 0; i < str.length(); i++) {
				addBytes((Character) str.charAt(i), 1, bufferAdEnd);
			}
			return true;
		}
		if (item instanceof byte[]) {
			byte[] array = (byte[]) item;
			return addBytes(array, array.length, bufferAdEnd);
		}
		if (item instanceof Byte[]) {
			Byte[] array = (Byte[]) item;
			return addBytes(array, array.length, bufferAdEnd);
		}
		if (item instanceof Integer) {
			return addBytes(item, 4, bufferAdEnd);
		}
		if (item instanceof Short) {
			return addBytes(item, 2, bufferAdEnd);
		}
		if (item instanceof Long) {
			return addBytes(item, 8, bufferAdEnd);
		}
		if (item instanceof Boolean) {
			return addBytes(item, 1, bufferAdEnd);
		}
		return false;
	}

	private boolean resize(int len, boolean bufferAtEnd) {
		int bufferLen = 0;
		if (this.buffer != null) {
			bufferLen = this.buffer.length;
		}
		/* Add ad end of Array */
		if (position < 0) {
			/* New Size with Buffer */
			int newSize;
			if (bufferAtEnd) {
				newSize = (length + len) + (length + len) / 2 + 5;
			} else {
				newSize = length + len;
			}
			if(newSize<0) {
				newSize =0;
			}
			byte[] oldBuffer = this.buffer;
			this.buffer = new byte[newSize];
			int oldSize = 0;
			if (oldBuffer != null) {
				oldSize = oldBuffer.length;
				if(length>0) {
					System.arraycopy(oldBuffer, oldBuffer.length - length, this.buffer, newSize - length, length);
				}
			}
			position += newSize - oldSize;
			return true;
		} else if (position + len > bufferLen) {
			/* New Size with Buffer */
			if (bufferLen > 0) {
				int newSize;
				if (bufferAtEnd) {
					newSize = (length + len) + (length + len) / 2 + 5;
				} else {
					newSize = length + len;
				}
				byte[] oldBuffer = this.buffer;
				this.buffer = new byte[newSize];
				System.arraycopy(oldBuffer, 0, this.buffer, 0, position);
			} else {
				this.buffer = new byte[len];
			}
			return true;
		}
		return false;
	}

	public boolean addBytes(Object value, int len, boolean bufferAtEnd) {
		boolean addEnd = false;
		if (this.position == this.length) {
			addEnd = true;
		}
		resize(len, bufferAtEnd);
		/* one Byte */
		if (value instanceof Byte) {
			this.buffer[position] = (Byte) value;
		} else {
			if (value instanceof byte[]) {
				byte[] source = (byte[]) value;
				if (this.buffer != null && this.buffer.length >= position + len) {
					for (int i = 0; i < len; i++) {
						this.buffer[position + i] = source[i];
					}
				}
			} else if (value instanceof Byte[]) {
				Byte[] source = (Byte[]) value;
				if (this.buffer != null && this.buffer.length >= position + len) {
					for (int i = 0; i < len; i++) {
						this.buffer[position + i] = source[i];
					}
				}
			} else if (value instanceof Character) {
				char charItem = (Character) value;
				if (len == 1) {
					this.buffer[position] = (byte) (charItem);
				} else {
					this.buffer[position] = (byte) (charItem >>> 8);
					this.buffer[position + 1] = (byte) charItem;
				}
				/* two Bytes */
			} else if (value instanceof Short) {
				short item = (Short) value;
				this.buffer[position] = (byte) (item >>> 8);
				this.buffer[position + 1] = (byte) item;
				/* four Bytes */
			} else if (value instanceof Integer) { /* value instanceof Float */
				Integer item = (Integer) value;
				this.buffer[position] = (byte) (item >>> 24);
				this.buffer[position + 1] = (byte) (item >>> 16);
				this.buffer[position + 2] = (byte) (item >>> 8);
				this.buffer[position + 3] = (byte) (item & 0xff);
			} else if (value instanceof Long) { /* value instanceof Double */
				Long item = (Long) value;
				this.buffer[position] = (byte) (item >>> 56);
				this.buffer[position + 1] = (byte) (item >>> 48);
				this.buffer[position + 2] = (byte) (item >>> 40);
				this.buffer[position + 3] = (byte) (item >>> 32);
				this.buffer[position + 4] = (byte) (item >>> 24);
				this.buffer[position + 5] = (byte) (item >>> 16);
				this.buffer[position + 6] = (byte) (item >>> 8);
				this.buffer[position + 7] = (byte) (item & 0xff);
			} else if (value instanceof Boolean) { /* value instanceof Float */
				Boolean item = (Boolean) value;
				if (item) {
					this.buffer[position] = 1;
				} else {
					this.buffer[position] = 0;
				}
			}
		}
		this.length += len;
		if (addEnd) {
			this.position = this.length;
		}
		return true;
	}

	public boolean put(byte value) {
		if (this.buffer != null && position + 1 <= buffer.length) {
			this.buffer[position++] = value;
			return true;
		}
		return false;
	}

	public boolean put(short value) {
		if (this.buffer != null && position + 2 <= buffer.length) {
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
			return true;
		}
		return false;
	}

	public boolean put(int value) {
		if (this.buffer != null && position + 4 <= buffer.length) {
			this.buffer[position++] = (byte) (value >>> 24);
			this.buffer[position++] = (byte) (value >>> 16);
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
			return true;
		}
		return false;
	}

	public boolean put(long value) {
		if (this.buffer != null && position + 8 <= buffer.length) {
			this.buffer[position++] = (byte) (value >>> 56);
			this.buffer[position++] = (byte) (value >>> 48);
			this.buffer[position++] = (byte) (value >>> 40);
			this.buffer[position++] = (byte) (value >>> 32);
			this.buffer[position++] = (byte) (value >>> 24);
			this.buffer[position++] = (byte) (value >>> 16);
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
			return true;
		}
		return false;
	}

	public boolean put(char value) {
		if (this.buffer != null && position + 2 <= buffer.length) {
			this.buffer[position++] = (byte) (value >>> 8);
			this.buffer[position++] = (byte) value;
			return true;
		}
		return false;
	}

	public boolean put(float value) {
		int bits = Float.floatToIntBits(value);
		if (this.buffer != null && position + 4 <= buffer.length) {
			this.buffer[position++] = (byte) (bits & 0xff);
			this.buffer[position++] = (byte) ((bits >> 8) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 16) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 24) & 0xff);
			return true;
		}
		return false;
	}

	public boolean put(double value) {
		long bits = Double.doubleToLongBits(value);
		if (this.buffer != null && position + 8 <= buffer.length) {
			this.buffer[position++] = (byte) ((bits >> 56) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 48) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 40) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 32) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 24) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 16) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 8) & 0xff);
			this.buffer[position++] = (byte) ((bits >> 0) & 0xff);
			return true;
		}
		return false;
	}

	public boolean put(byte[] value) {
		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				put(value[i]);
			}
			return true;
		}
		return false;
	}

	public void put(byte[] value, int offset, int length) {
		if (value == null || offset < 0 || offset > value.length) {
			return;
		}
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
		if (len < 1) {
			return null;
		}
		ByteBuffer bytesBuffer = new ByteBuffer();
		bytesBuffer.withBufferLength(len);
		return bytesBuffer;
	}

	public ByteBuffer getNewBuffer(byte[] array) {
		return new ByteBuffer().with(array);
	}

	public ByteBuffer with(CharSequence... string) {
		if (string != null && string.length > 0 && string[0] instanceof String) {
			this.buffer = ((String) string[0]).getBytes(Charset.forName(ENCODING));
			this.position = 0;
			this.length = buffer.length;
		}
		return this;
	}

	public ByteBuffer with(byte[] array) {
		this.buffer = array;
		this.position = 0;
		if(array != null) {
			this.length = array.length;
		}else {
			this.length = 0;
		}
		return this;
	}

	public ByteBuffer with(byte value) {
		if (this.buffer == null) {
			this.buffer = new byte[] { value };
			this.length = 1;
			this.position = 0;
		} else if (this.length < this.buffer.length) {
			this.buffer[length++] = value;
		}
		return this;
	}

	@Override
	public String toString() {
		return toString(new ByteConverterString());
	}

	@Override
	public ByteBuffer getNewList(boolean keyValue) {
		return new ByteBuffer();
	}

	@Override
	public int size() {
		return length();
	}

	public String string() {
		return new String(buffer);
	}

	public String toString(Converter converter) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		if (converter instanceof ByteConverter) {
			return ((ByteConverter) converter).toString(this.toBytes());
		}
		return String.valueOf(buffer);
	}

	public ByteBuffer with(byte[] array, int len) {
		this.position = 0;
		if(array == null) {
			return this;
		}
		if (len < 0 || len > array.length) {
			len = array.length;
		}
		if (this.buffer == null) {
			this.buffer = array;
			this.length = len;
		} else {
			/* Resize */
			byte[] oldBuffer = this.buffer;
			this.buffer = new byte[this.length * 2 + len];
			System.arraycopy(oldBuffer, 0, this.buffer, 0, this.length);
			System.arraycopy(array, 0, this.buffer, this.length, len);
			this.length += len;
		}
		return this;
	}

	public ByteBuffer with(char[] values, int start, int len) {
		if (values == null || start + length > values.length) {
			return this;
		}
		if (this.length < 0) {
			this.length = this.buffer.length;
		}
		int newLen = length + this.length;
		if (buffer == null || newLen + this.start > buffer.length) {
			byte[] oldValue = this.buffer;
			this.buffer = new byte[(newLen * 2 + 2)];
			if (oldValue != null) {
				System.arraycopy(oldValue, start, this.buffer, 0, this.length);
			}
			this.start = 0;
			this.position = 0;
		}
		System.arraycopy(values, start, this.buffer, this.length, length);
		this.length = newLen;
		return this;
	}

	private int bits = 0;
	private int nextBitMask = 0x100; /* triggers readOctet first time */

	/**
	 * Public API - reads a bit/boolean argument.
	 * 
	 * @return boolean
	 */

	public boolean getBit() {
		if (nextBitMask > 0x80) {
			bits = getByte();
			nextBitMask = 0x01;
		}
		boolean result = (bits & nextBitMask) != 0;
		nextBitMask = nextBitMask << 1;
		return result;
	}

	/**
	 * Convenience method - reads a short string from a DataInput Stream.
	 * 
	 * @return a new String
	 */
	public String getShortstr() {
		final int contentLength = (int) (getByte() & 0xff);
		byte[] b = getBytes(new byte[contentLength]);
		return new String(b);
	}

	public boolean set(int pos, byte value) {
		if (pos >= 0 && pos <= this.length && this.buffer != null) {
			this.buffer[pos] = value;
			return true;
		}
		return false;
	}

}
