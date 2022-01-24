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
 * Bit Value
 * @author Stefan Lindel
 */
public class BitValue implements ByteItem {
	private byte start;
	private byte size;
	private byte type;
	private String property;
	private int orientation = 1;

	public BitValue(int start, int size) {
		this.start = (byte) start;
		this.size = (byte) size;
	}

	public BitValue(byte start, byte len) {
		this.start = start;
		this.size = len;
	}

	public byte getStart() {
		return start;
	}

	public BitValue withStart(byte start) {
		this.start = start;
		return this;
	}

	public BitValue withSize(byte len) {
		this.size = len;
		return this;
	}

	public BitValue withOrientation(int value) {
		this.orientation = value;
		return this;
	}

	public int getOrientation() {
		return orientation;
	}

	public String getProperty() {
		return property;
	}

	public BitValue withLenProperty(byte lenType, String property) {
		this.type = lenType;
		this.property = property;
		return this;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public byte getType() {
		return type;
	}

	@Override
	public boolean isEmpty() {
		return size < 1;
	}

	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		return size;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ByteList();
	}

	@Override
	public String toString(Converter converter) {
		if (converter instanceof ByteConverter) {
			return toString((ByteConverter) converter, false);
		}
		return toString(null, false);
	}

	@Override
	public String toString(ByteConverter converter, boolean isDynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this.getBytes(isDynamic));
	}

	@Override
	public ByteBuffer getBytes(boolean isDynamic) {
		return null;
	}

	@Override
	public void writeBytes(ByteBuffer buffer, boolean isDynamic, boolean lastEntity, boolean isPrimitive) {
	}

	@Override
	public boolean add(Object... values) {
		return false;
	}
}
