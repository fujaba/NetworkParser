package de.uniks.networkparser.bytes;

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
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.converter.ByteConverterHTTP;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;

public class ByteList extends SimpleList<ByteItem> implements ByteItem {
	/** The children of the ByteEntity. */
	private byte typ = 0;

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new ByteEntity();
		}
		return new ByteList();
	}

	@Override
	public String toString() {
		return toString(null);
	}

	/**
	 * Convert the bytes to a String
	 *
	 * @param converter
	 *			Grammar
	 * @return converted bytes as String
	 */
	@Override
	public String toString(Converter converter) {
		if(converter instanceof ByteConverter) {
			return toString((ByteConverter)converter, false);
		}
		return toString(null, false);
	}

	/**
	 * Convert the bytes to a String
	 *
	 * @param converter
	 *			Grammar
	 * @param dynamic
	 *			if byte is dynamic
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter, boolean dynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this.getBytes(dynamic));
	}

	@Override
	public ByteBuffer getBytes(boolean isDynamic) {
		int len = calcLength(isDynamic, true);
		ByteBuffer buffer = ByteUtil.getBuffer(len);
		writeBytes(buffer, isDynamic, true, isPrimitive(isDynamic));
		buffer.flip(true);
		return buffer;
	}

	@Override
	public void writeBytes(ByteBuffer buffer, boolean isDynamic,
			boolean last, boolean isPrimitive) {
		// Override for each ByteList
		isPrimitive = isPrimitive(isDynamic);
		int size = calcChildren(isDynamic, last);
		byte typ;
		if (isPrimitive) {
			typ = ByteTokener.DATATYPE_CLAZZSTREAM;
		} else {
			typ = ByteUtil.getTyp(getTyp(), size, last);
		}
		ByteUtil.writeByteHeader(buffer, typ, size);

		for (int i = 0; i < size(); i++) {
			((ByteItem) get(i)).writeBytes(buffer, isDynamic,
					i == size() - 1, isPrimitive);
		}
	}

	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		if (size() == 0) {
			return 1;
		}
		int length = calcChildren(isDynamic, isLast);
		// add The Headerlength
		if (typ != 0) {
			length += ByteEntity.TYPBYTE
					+ ByteUtil.getTypLen(typ, length, isLast);
		}
		return length;
	}

	public int calcChildren(boolean isDynamic, boolean isLast) {
		int length, size = size();
		if (size < 1) {
			return 0;
		}
		boolean isPrimitive = isDynamic;
		int nullerBytes = 0;
		if (this.get(size - 1) instanceof ByteEntity) {
			// HEADER + VALUE
			isPrimitive = isPrimitive
					&& this.get(0).getTyp() == ByteTokener.DATATYPE_CLAZZTYP;
			if (this.get(size - 1).getTyp() == ByteTokener.DATATYPE_NULL) {
				nullerBytes++;
			}
		} else {
			isPrimitive = false;
		}
		length = this.get(size - 1).calcLength(isDynamic, true);
		// length=len+ByteUtil.getTypLen(valueList[size-1].getTyp(), len - 1);
		for (int i = size - 2; i >= 0; i--) {
			int len = this.get(i).calcLength(isDynamic, false);
			if (isPrimitive) {
				if (this.get(i).getTyp() == ByteTokener.DATATYPE_NULL) {
					nullerBytes++;
				}
				isPrimitive = (this.get(i).size() == len - 1);
			}
			length += len;
		}
		if (isPrimitive) {
			// Only for ByteList with value dynamic and values with cant be
			// short
			// add one for ClazzSTEAM Byte as first Byte
			length = length - size + ByteEntity.TYPBYTE + ByteEntity.TYPBYTE
					+ nullerBytes;
		}
		return length;
	}

	private boolean isPrimitive(boolean isDynamic) {
		if (!isDynamic) {
			return false;
		}
		if (this.size() < 1) {
			return false;
		}
		if (!(this.get(this.size() - 1) instanceof ByteEntity)) {
			return false;
		}
		if (this.get(0).getTyp() != ByteTokener.DATATYPE_CLAZZTYP) {
			return false;
		}
		for (int i = 1; i < this.size(); i++) {
			int len = this.get(i).calcLength(isDynamic, false);
			if ((this.get(i).size() != len - 1)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public byte getTyp() {
		return typ;
	}

	public ByteList withTyp(Byte value) {
		if(value != null)
			this.typ = value;
		return this;
	}

	public SimpleList<ByteItem> withValue(String value) {
		ByteConverterString converter = new ByteConverterString();
		this.add(((ByteEntity)getNewList(true)).withValue(ByteTokener.DATATYPE_FIXED,
				converter.decode(value)));
		return this;
	}

	@Override
	public ByteList with(Object... values) {
		if (values != null) {
			for (Object value : values) {
				if (value instanceof ByteItem) {
					this.add((ByteItem) value);
				}
			}
		}
		return this;
	}

	@Override
	public boolean remove(Object value) {
		return removeByObject(value) >= 0;
	}
}
