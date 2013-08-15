package de.uniks.jism.bytes;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import de.uniks.jism.EntityList;
import de.uniks.jism.bytes.converter.ByteConverter;
import de.uniks.jism.bytes.converter.ByteConverterHTTP;
import de.uniks.jism.interfaces.ByteItem;
import de.uniks.jism.interfaces.JISMEntity;

public class ByteList extends EntityList implements ByteItem {
	/** The children of the ByteEntity. */
	private byte typ = 0;

	@Override
	public EntityList getNewArray() {
		return new ByteList();
	}

	@Override
	public JISMEntity getNewObject() {
		return new ByteEntity();
	}

	@Override
	public String toString(int indentFactor) {
		return toString(null);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		return toString(null);
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
	public String toString(ByteConverter converter, boolean dynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this, dynamic);
	}

	public BufferedBytes getBytes(boolean isDynamic) {
		int len = calcLength(isDynamic);
		BufferedBytes buffer = ByteUtil.getBuffer(len, getTyp());
		if (buffer == null) {
			return null;
		}
		
		for(int i=0;i<values.size();i++){
			BufferedBytes child = null;
			if (values.get(i) instanceof ByteItem) {
				child = ((ByteItem) values.get(i)).getBytes(isDynamic);
			}
			if (child == null) {
				buffer.put(ByteIdMap.DATATYPE_NULL);
			} else {
				if(i==values.size()-1&&child.byteAt(0)!=ByteIdMap.DATATYPE_CHECK){
					// Its the Last Entity
					byte typ=child.byteAt(0); 
					int typLen = 1+ByteUtil.getTypLen(typ);
					if(typLen>1){
						buffer.put(ByteUtil.getTyp(child.byteAt(0), ByteIdMap.DATATYPE_STRINGLAST));
//						byte[] array = new byte[child.length()-typLen];
						byte[] array = child.getValue(typLen, child.length()-typLen);
						buffer.put(array);
						continue;
					}
				}
				byte[] array = child.getValue(child.length());
				System.out.println(ByteUtil.getStringTyp(array[0]));
				buffer.put(array);
			}
		}
		buffer.flip();
		return buffer;
	}

	public int calcLength(boolean isDynamic) {
		int size=size();
		if (size == 0 ) {
			return 0;
		}
		int length = 0;
		if (typ != 0) {
			length = ByteEntity.TYPBYTE;
		}
		Object[] valueList = this.values.toArray(new Object[size]);
		
		// SonderFall Last Entity
		if(valueList[size-1] instanceof ByteEntity){
			ByteEntity item =(ByteEntity) valueList[size-1];
			int typLen=ByteUtil.getTypLen(item.getTyp());
			if(typLen>0){
				// Must be a Group and not the optimize LastEntity
				length+=item.getValue().length+1;
			}else{
				length+=item.calcLength(isDynamic);
			}
		}else{
			length+=((ByteItem)valueList[size-1]).calcLength(isDynamic);
		}
		for (int i = size - 2; i >= 0; i--) {
			length += ((ByteItem)valueList[i]).calcLength(isDynamic);
 		}
		return length;
	}

	public Byte getTyp() {
		return typ;
	}

	public void setTyp(Byte value) {
		this.typ = value;
	}
}
