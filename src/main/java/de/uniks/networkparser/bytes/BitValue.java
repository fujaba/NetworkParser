package de.uniks.networkparser.bytes;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

/**
 * Bit Value.
 *
 * @author Stefan Lindel
 */
public class BitValue implements ByteItem {
	private byte start;
	private byte size;
	private byte type;
	private String property;
	private int orientation = 1;

	/**
	 * Instantiates a new bit value.
	 *
	 * @param start the start
	 * @param size the size
	 */
	public BitValue(int start, int size) {
		this.start = (byte) start;
		this.size = (byte) size;
	}

	/**
	 * Instantiates a new bit value.
	 *
	 * @param start the start
	 * @param len the len
	 */
	public BitValue(byte start, byte len) {
		this.start = start;
		this.size = len;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public byte getStart() {
		return start;
	}

	/**
	 * With start.
	 *
	 * @param start the start
	 * @return the bit value
	 */
	public BitValue withStart(byte start) {
		this.start = start;
		return this;
	}

	/**
	 * With size.
	 *
	 * @param len the len
	 * @return the bit value
	 */
	public BitValue withSize(byte len) {
		this.size = len;
		return this;
	}

	/**
	 * With orientation.
	 *
	 * @param value the value
	 * @return the bit value
	 */
	public BitValue withOrientation(int value) {
		this.orientation = value;
		return this;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * With len property.
	 *
	 * @param lenType the len type
	 * @param property the property
	 * @return the bit value
	 */
	public BitValue withLenProperty(byte lenType, String property) {
		this.type = lenType;
		this.property = property;
		return this;
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public byte getType() {
		return type;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	@Override
	public boolean isEmpty() {
		return size < 1;
	}

	/**
	 * Calc length.
	 *
	 * @param isDynamic the is dynamic
	 * @param isLast the is last
	 * @return the int
	 */
	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		return size;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ByteList();
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		if (converter instanceof ByteConverter) {
			return toString((ByteConverter) converter, false);
		}
		return toString(null, false);
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @param isDynamic the is dynamic
	 * @return the string
	 */
	@Override
	public String toString(ByteConverter converter, boolean isDynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this.getBytes(isDynamic));
	}

	/**
	 * Gets the bytes.
	 *
	 * @param isDynamic the is dynamic
	 * @return the bytes
	 */
	@Override
	public ByteBuffer getBytes(boolean isDynamic) {
		return null;
	}

	/**
	 * Write bytes.
	 *
	 * @param buffer the buffer
	 * @param isDynamic the is dynamic
	 * @param lastEntity the last entity
	 * @param isPrimitive the is primitive
	 */
	@Override
	public void writeBytes(ByteBuffer buffer, boolean isDynamic, boolean lastEntity, boolean isPrimitive) {
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		return false;
	}
}
