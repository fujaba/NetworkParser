package de.uniks.jism.bytes;
/*
Copyright (c) 2012, Stefan Lindel
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

THIS SOFTWARE 'Json Id Serialisierung Map' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;

import de.uniks.jism.AbstractIdMap;
import de.uniks.jism.CloneFilter;
import de.uniks.jism.IdMap;
import de.uniks.jism.bytes.creator.BitEntityCreator;

public class ByteSimpleMap extends AbstractIdMap{

	@Override
	public Object cloneObject(Object reference, CloneFilter filter) {
		return null;
	}
	
//	public Object decode(ByteBuffer buffer){
//		
//	}
	
	public Object decode(ByteBuffer buffer, BitEntityCreator creator){
		HashMap<String, Object> values=new HashMap<String, Object>();
		BitEntity[] bitProperties = creator.getBitProperties();
		Object newInstance = creator.getSendableInstance(false);
		for(BitEntity entity : bitProperties){
			Object element = getEntity(buffer, entity, values);
			if(element != null){
				creator.setValue(newInstance, entity.getPropertyName(), element, IdMap.NEW);
			}
		}
		return newInstance;
	}
	
	public Object getEntity(ByteBuffer buffer, BitEntity entry, HashMap<String, Object> values){
 		if(entry.size()<1){
			// Reference or Value
			if(entry.isTyp(BitEntity.BIT_REFERENCE)){
				String propertyName = entry.getPropertyName();
				if(values.containsKey(propertyName)){
					return values.get(propertyName);
				}
			}else if(entry.isTyp(BitEntity.BIT_BYTE, BitEntity.BIT_NUMBER, BitEntity.BIT_STRING)){
				// Value
				return entry.getPropertyName();
			}
		}
		// Wert ermitteln
		
		// Init the Values
		ByteBuffer result = null;
		for(Iterator<BitValue> i= entry.valueIterator();i.hasNext();){
			BitValue bitValue = i.next();
			
			int orientationSource= bitValue.getOrientation();
			int orientationTarget = entry.getOrientation();
			
			int temp = Integer.valueOf(""+getEntity(buffer, bitValue.getStart(), values));
			int posOfByte = temp/8;
			int posOfBit = (8-((temp+1)%8))%8;
			
			temp = Integer.valueOf(""+getEntity(buffer, bitValue.getLen(), values));
			int noOfByte=temp/8;
			int number=0;
			int resultPos = 0;
			int theByte = buffer.get(posOfByte);
			if(theByte<0){
				theByte+=256;
			}
			
			if(temp%8>0){
				noOfByte++;
			}
			

			result = ByteBuffer.allocate(noOfByte);
			orientationSource=orientationSource*-1;
			while(temp>0){
				int bitvalue = (theByte >> posOfBit)&1;
				if(orientationTarget>0){
					number = (number<<1)+bitvalue;
				}else{
					number += bitvalue << resultPos;
				}
				temp--;
				if(temp<1){
					break;
				}
				
				posOfBit+=orientationSource;
				resultPos++;
				if(posOfBit>7||posOfBit<0){
					posOfBit = 0;
					++posOfByte;
					if(posOfByte<buffer.limit()){
						theByte = buffer.get(posOfByte);
						if(theByte<0){
							theByte+=256;
						}
					}
				}
				if(resultPos ==8){
					result.put((byte)number);
					resultPos = 0;
				}
				
			}
			if(resultPos>0){
				result.put((byte)number);
			}
		}
		
		// Set the Typ
		Object element=null;

		result.flip();
		if(entry.getTyp().equals(BitEntity.BIT_BYTE)){
			byte[] array = result.array();
			if(array.length==1){
				element = new Byte(array[0]);
			}else{
				Byte[] item=new Byte[array.length];
				for(int i=0;i<array.length;i++){
					item[i] = array[i];
				}
				element = item;
			}
		}else if(entry.getTyp().equals(BitEntity.BIT_NUMBER)){
			if(result.limit()== Byte.SIZE/ByteEntity.BITOFBYTE){
				element = result.get();
			}else if(result.limit()== Short.SIZE/ByteEntity.BITOFBYTE){
				element = result.getShort();
			} else if(result.limit()== Integer.SIZE/ByteEntity.BITOFBYTE){
				element = result.getInt();
			} else if(result.limit()== Long.SIZE/ByteEntity.BITOFBYTE){
				element = result.getLong();
			} else if(result.limit()== Float.SIZE/ByteEntity.BITOFBYTE){
				element = result.getFloat();
			} else if(result.limit()== Double.SIZE/ByteEntity.BITOFBYTE){
				element = result.getDouble();
			}else{
				element = result.getInt();
			}
		}else if(entry.getTyp().equals(BitEntity.BIT_STRING)){
			result.flip();
			element = new String(result.array());
			
		}
		values.put(entry.getPropertyName(), element);
		return element;
	}
}
