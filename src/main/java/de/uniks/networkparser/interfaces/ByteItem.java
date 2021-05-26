package de.uniks.networkparser.interfaces;

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
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.bytes.ByteConverter;

public interface ByteItem extends BaseItem {
	public static final byte BIT_STRING = 0x53; /* S = String; */
	public static final byte BIT_NUMBER = 0x4E; /* N = Number */
	public static final byte BIT_BYTE = 0x42; /* B = Byte */
	public static final byte BIT_REFERENCE = 0x52; /* R = Reference */

	/**
	 * @param converter ByteConverter for Format
	 * @param isDynamic ByteStream for minimize output
	 * @return the ByteItem as String
	 */
	public String toString(ByteConverter converter, boolean isDynamic);

	/**
	 * @param isDynamic ByteStream for minimize output
	 * @return ByteStream
	 */
	public ByteBuffer getBytes(boolean isDynamic);

	/**
	 * Write the Entity to the buffer
	 *
	 * @param buffer      for writing
	 * @param isDynamic   dynamic switsch
	 * @param lastEntity  is the entity is the last of a list
	 * @param isPrimitive need the entity no datatyp
	 */
	public void writeBytes(ByteBuffer buffer, boolean isDynamic, boolean lastEntity, boolean isPrimitive);

	/**
	 * @param isDynamic ByteStream for minimize output
	 * @param isLast    is the Element is the Last of Group
	 * @return the Size of Bytes
	 */
	public int calcLength(boolean isDynamic, boolean isLast);

	public byte getType();

	/** @return true if the ByteItem is Empty */
	public boolean isEmpty();

	/**
	 * Size of Item
	 * 
	 * @return the Size of the Item
	 */
	public int size();
}
