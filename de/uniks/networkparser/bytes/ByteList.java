package de.uniks.networkparser.bytes;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.bytes.converter.ByteConverterHTTP;
import de.uniks.networkparser.bytes.converter.ByteConverterString;
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteConverter;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.FactoryEntity;

public class ByteList extends AbstractList<ByteItem> implements ByteItem, FactoryEntity{
	/** The children of the ByteEntity. */
	private byte typ = 0;

	@Override
	public ByteList getNewArray() {
		return new ByteList();
	}

	@Override
	public ByteEntity getNewObject() {
		return new ByteEntity();
	}

	@Override
	public String toString() {
		return toString(null);
	}

	/**
	 * Convert the bytes to a String
	 * 
	 * @param converter
	 *            Grammar
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter) {
		return toString(converter, false);
	}

	/**
	 * Convert the bytes to a String
	 * 
	 * @param converter
	 *            Grammar
	 * @param dynamic
	 *            if byte is dynamic
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter, boolean dynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this, dynamic);
	}

	@Override
	public BufferedBytes getBytes(boolean isDynamic) {
		int len = calcLength(isDynamic, true);
		BufferedBytes buffer = ByteUtil.getBuffer(len);
		writeBytes(buffer, isDynamic, true);
		buffer.flip();
		return buffer;
	}
	
	@Override
	public void writeBytes(BufferedBytes buffer, boolean isDynamic, boolean last){
		int size=calcChildren(isDynamic, last);
		byte typ = ByteUtil.getTyp(getTyp(), size, last);
      ByteUtil.writeByteHeader(buffer, typ, size);

		for(int i=0;i<values.size();i++){
			((ByteItem) values.get(i)).writeBytes(buffer, isDynamic, i==values.size()-1);
		}
	}

	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		if (size() == 0 ) {
			return 1;
		}
		int length = calcChildren(isDynamic, isLast);
		// add The Headerlength
		if (typ != 0) {
			length += ByteEntity.TYPBYTE + ByteUtil.getTypLen(typ, length, isLast);
		}
		return length;
	}
	
	public int calcChildren(boolean isDynamic, boolean isLast) {
		int length, size=size();
		if(size<1){
			return 0;
		}
		ByteItem[] valueList = this.values.toArray(new ByteItem[size]);
		
		// SonderFall Last Entity
		boolean isPrimitive=isDynamic;
		if(valueList[size-1] instanceof ByteEntity){
			// HEADER + VALUE
			length=valueList[size-1].calcLength(isDynamic, true);
		}else{
			ByteList list = (ByteList) valueList[size-1];
			int len=list.calcChildren(isDynamic, true);
			if(list.getTyp()!=0){
				len++;
			}
			isPrimitive=false;
			length=len+ByteUtil.getTypLen(valueList[size-1].getTyp(), len - 1, true);
		}
//		length=len+ByteUtil.getTypLen(valueList[size-1].getTyp(), len - 1);
		for (int i = size - 2; i >= 0; i--) {
			int len = valueList[i].calcLength(isDynamic, false);
			if(isPrimitive){
				isPrimitive = (valueList[i].size()==len - 1);
			}
			length += len;
 		}
		if(isPrimitive){
			// Only for ByteList with value dynamic and values with cant be short
//FIXME			length-= size;
		}
		return length;
	}

	@Override
	public byte getTyp() {
		return typ;
	}

	public ByteList withTyp(Byte value) {
		this.typ = value;
		return this;
	}

	public AbstractList<ByteItem> withValue(String value) {
		ByteConverterString	converter = new ByteConverterString();
		this.add(getNewObject().withValue(ByteIdMap.DATATYPE_FIXED, converter.decode(value)));
		return this;
	}

	@Override
	public AbstractList<ByteItem> getNewInstance() {
		return new ByteList();
	}

	@Override
	public ByteList with(Object... values) {
		if(values != null){
			for(Object value : values){
				if(value instanceof ByteItem) {
					this.add((ByteItem) value);
				}
			}
		}
		return this;
	}
}
