package de.uniks.networkparser.bytes;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import java.util.ArrayList;
import java.util.Iterator;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class ByteParser {
	public Object decode(ByteBuffer buffer, BitEntityCreator creator) {
		SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();
		BitEntity[] bitProperties = creator.getBitProperties();
		Object newInstance = creator.getSendableInstance(false);
		for (BitEntity entity : bitProperties) {
			Object element = getEntity(buffer, entity, values);
			if (element != null) {
				creator.setValue(newInstance, entity.getPropertyName(),
						element, SendableEntityCreator.NEW);
			}
		}
		return newInstance;
	}

	public Object getEntity(ByteBuffer buffer, BitEntity entry,
			SimpleKeyValueList<String, Object> values) {
		if (entry.size() < 1) {
			// Reference or Value
			if (entry.isType(BitEntity.BIT_REFERENCE)) {
				String propertyName = entry.getPropertyName();
				if (values.containsKey(propertyName)) {
					return values.getValue(propertyName);
				}
			} else if (entry.isType(BitEntity.BIT_BYTE, BitEntity.BIT_NUMBER,
					BitEntity.BIT_STRING)) {
				// Value
				return entry.getPropertyName();
			}
		}
		// Wert ermitteln

		// Init the Values
		ArrayList<ByteBuffer> results = new ArrayList<ByteBuffer>();
		ArrayList<Integer> resultsLength = new ArrayList<Integer>();

		for (Iterator<BitValue> i = entry.iterator(); i.hasNext();) {
			BitValue bitValue = i.next();

			int orientationSource = bitValue.getOrientation();
			int orientationTarget = entry.getOrientation();
			BitEntity bit = new BitEntity().with(bitValue.getStart());
			int temp = Integer.parseInt("" + getEntity(buffer, bit, values));
			int posOfByte = temp / 8;
			int posOfBit = (8 - ((temp + 1) % 8)) % 8;

			bit = new BitEntity().with(bitValue.size());
			bit.with(bitValue.getProperty(), bitValue.getType());
			int length = Integer.parseInt(""
					+ getEntity(buffer,bit, values));
			int noOfByte = length / 8;
			if (length % 8 > 0) {
				noOfByte++;
			}

			resultsLength.add(length);
			ByteBuffer result = ByteBuffer.allocate(noOfByte);

			int theByte = buffer.byteAt(posOfByte);
			if (theByte < 0) {
				theByte += 256;
			}

			int resultPos = 0;
			int number = 0;
			int sourceBit = (length < 8 - resultPos) ? length : 8 - resultPos;

			theByte = theByte >> (posOfBit - sourceBit + 1);
			while (length > 0) {
				sourceBit = (length < 8 - resultPos) ? length : 8 - resultPos;
				int sourceBits = (theByte & (0xff >> (8 - sourceBit)));

				if (orientationTarget > 0) {
					number = (number << (sourceBit));
					if (orientationSource > 0)
						// Source Target
						number += sourceBits;
					else {
						// Bits vertauschen
						for (int z = sourceBit; z > 0; z--) {
							number += sourceBits
									& (0x1 << sourceBit) << (sourceBit - z);
						}
					}
				} else {
					if (orientationSource > 0)
						// Source Target
						number += sourceBits << sourceBit;
					else {
						// Bits vertauschen
						for (int z = sourceBit; z > 0; z--) {
							number += sourceBits
									& (0x1 << sourceBit) << (sourceBit - z);
						}
					}
				}

				theByte = (byte) (theByte >> (sourceBit));
				resultPos += sourceBit;
				length -= sourceBit;
				if (resultPos == 8) {
					result.put((byte) number);
					resultPos = 0;
					number = 0;
					if (length > 0) {
						theByte = buffer.byteAt(posOfByte);
						if (theByte < 0) {
							theByte += 256;
						}
					}
				}
			}
			if (resultPos > 0) {
				result.put((byte) number);
			}

			// Save one Result to List
			result.flip(true);
			results.add(result);
		}

		// Merge all Results to one
		int length = 0;
		for (Integer item : resultsLength) {
			length += item;
		}
		int number = length / 8 + ((length % 8 > 0) ? 1 : 0);

		ByteBuffer result = new ByteBuffer();
		result.withBufferLength(number);

		int resultPos = 0;
		number = 0;
		for (int i = 0; i < results.size(); i++) {
			ByteBuffer source = results.get(i);
			length = resultsLength.get(i);
			while (length > 0) {
				byte theByte = source.getByte();
				int sourceBit = (length < 8 - resultPos) ? length
						: 8 - resultPos;
				number = (number << (sourceBit))
						+ (theByte & (0xff >> (8 - sourceBit)));
				theByte = (byte) (theByte >> (sourceBit));
				resultPos += sourceBit;
				length -= sourceBit;
				if (resultPos == 8) {
					result.put((byte) number);
					resultPos = 0;
					number = 0;
					if (length > 0) {
						theByte = source.getByte();
					}
				}
			}
		}
		if (resultPos > 0) {
			result.put((byte) number);
		}

		result.flip(true);

		// Set the Type
		Object element = null;

		if (entry.getType() == BitEntity.BIT_BYTE) {
			byte[] array = result.array();
			if (array.length == 1) {
				element = Byte.valueOf(array[0]);
			} else {
				Byte[] item = new Byte[array.length];
				for (int i = 0; i < array.length; i++) {
					item[i] = array[i];
				}
				element = item;
			}
		} else if (entry.getType() == BitEntity.BIT_NUMBER) {
			if (result.length() == Byte.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getByte();
			} else if (result.length() == Short.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getShort();
			} else if (result.length() == Integer.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getInt();
			} else if (result.length() == Long.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getLong();
			} else if (result.length() == Float.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getFloat();
			} else if (result.length() == Double.SIZE / ByteEntity.BITOFBYTE) {
				element = result.getDouble();
			} else {
				element = result.getInt();
			}
		} else if (entry.getType() == BitEntity.BIT_STRING) {
			result.flip(false);
			element = String.valueOf(result.array());

		}
		values.put(entry.getPropertyName(), element);
		return element;
	}
}
